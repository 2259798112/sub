package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.SubQuestionDetail;
import top.duwd.common.domain.sub.entity.ZhihuQuestionBasic;
import top.duwd.common.domain.zhihu.ZhihuQuestionEntity;
import top.duwd.common.mapper.sub.SubQuestionDetailMapper;
import top.duwd.common.service.proxy.ProxyService;
import top.duwd.common.util.CollectionUtil;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.sub.job.ElasticQuestionPrepareJob;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.*;

@Service
@Slf4j
public class SubQuestionDetailService implements IBaseService<SubQuestionDetail> {
    @Resource
    private SubQuestionDetailMapper subQuestionDetailMapper;
    @Resource
    private RequestBuilder requestBuilder;
    @Resource
    private ZhihuQuestionBasicService zhihuQuestionBasicService;
    @Resource
    private ProxyService proxyService;

    private static final String QUESTION_BASE = "https://www.zhihu.com/question/";
    private static final Map<String, String> hMap = new HashMap<>();

    static {
        hMap.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        hMap.put("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
        hMap.put("cache-control", "no-cache");
        hMap.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
    }

    @Override
    public List<SubQuestionDetail> findListByKV(String k, Object v) {
        Example example = new Example(SubQuestionDetail.class);
        example.createCriteria().andEqualTo(k, v);
        List<SubQuestionDetail> list = subQuestionDetailMapper.selectByExample(example);
        return list;
    }

    @Override
    public List<SubQuestionDetail> findListByMap(Map<String, Object> map) {
        Example example = new Example(SubQuestionDetail.class);
        Example.Criteria criteria = example.createCriteria();
        map.forEach((key, value) -> criteria.andEqualTo(key, value));
        List<SubQuestionDetail> list = subQuestionDetailMapper.selectByExample(example);
        return list;
    }

    public PageInfo<SubQuestionDetail> findListByMapPage(Map<String, Object> map, int pageNum, int pageSize, String sortField, int direct) {
        Example example = new Example(SubQuestionDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIsNotNull("title");//排除未处理
        map.forEach((key, value) -> criteria.andEqualTo(key, value));
        if (Const.ASC == direct) {
            example.orderBy(sortField).asc();
        } else {
            example.orderBy(sortField).desc();
        }

        PageHelper.startPage(pageNum, pageSize);
        List<SubQuestionDetail> list = subQuestionDetailMapper.selectByExample(example);
        PageInfo page = new PageInfo(list);
        return page;
    }

    @Transactional
    public int insertList(List<SubQuestionDetail> list, int pageSize) {
        log.info("insertList count={}", list.size());
        if (list.size() < pageSize) {
            return subQuestionDetailMapper.insertList(list);
        } else {
            int count = 0;
            List<List<SubQuestionDetail>> splitList = CollectionUtil.splitList(list, pageSize);
            for (List<SubQuestionDetail> details : splitList) {
                count += subQuestionDetailMapper.insertList(details);
            }
            return count;
        }
    }

    /**
     * 0 表明没有成功，-1 表示代理失效
     *
     * @param questionId
     * @param proxy
     * @return
     */

    @Transactional
    public int parse(int questionId, Proxy proxy) {
        String url = QUESTION_BASE + questionId;
        String htmlString = null;
        //同意使用ip代理

        //获取1min内的ip订单
        try {
            if (proxy != null){
                htmlString = requestBuilder.getWithProxy(url, hMap, proxy);
            }else {
                htmlString = requestBuilder.get(url, hMap);
            }        } catch (SocketTimeoutException e) {
            log.error("connect timed out {}", e.getMessage());
            //代理失效
            return Const.PROXY_INVALID;
        } catch (IOException e) {
            log.error("java.io.IOException {}", e.getMessage());
            //connection 403
        } catch (Exception e) {
            e.printStackTrace();
            log.error("系统异常");
        }

        if (StringUtils.isEmpty(htmlString)) {
            return 0;
        }

        Document html = Jsoup.parse(htmlString);

        if ("安全验证 - 知乎".equalsIgnoreCase(html.title())) {
            //知乎反扒限制
            log.info("需要更换IP={}", proxy.address().toString());
            return Const.PROXY_INVALID;
        } else {
            log.info(html.title() + " " + questionId);
        }

        Element ele = html.getElementById("js-initialData");

        String text = ele.toString();
        String start = "<script id=\"js-initialData\" type=\"text/json\">";
        String end = "</script>";
        String substring = text.substring(start.length(), text.length() - end.length());
        JSONObject jsonObject = JSON.parseObject(substring);

        return parseQuestion(jsonObject);
    }

    @Transactional
    public int parseBasic(int questionId,int id, Proxy proxy) {

        String url = QUESTION_BASE + questionId;
        String htmlString = null;
        //同意使用ip代理

        //获取1min内的ip订单
        try {
            if (proxy != null){
                htmlString = requestBuilder.getWithProxy(url, hMap, proxy);
            }else {
                htmlString = requestBuilder.get(url, hMap);
            }
        } catch (SocketTimeoutException e) {
            log.error("connect timed out {}", e.getMessage());
            //代理失效
            return Const.PROXY_INVALID;
        } catch (IOException e) {
            log.error("java.io.IOException {}", e.getMessage());
            //connection 403
            //if 404  update title=404
            if (e.getMessage().contains("code=404")){
                log.error("404");
                ZhihuQuestionBasic basic = new ZhihuQuestionBasic();
                basic.setId(id);
                basic.setQid(questionId);
                basic.setTitle("404");
                Date date = new Date();
                basic.setCreateTime(date);
                basic.setUpdateTime(date);
                return zhihuQuestionBasicService.update(basic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("系统异常");
        }

        if (StringUtils.isEmpty(htmlString)) {
            return 0;
        }

        Document html = Jsoup.parse(htmlString);

        if ("安全验证 - 知乎".equalsIgnoreCase(html.title())) {
            //知乎反扒限制
            log.info("需要更换IP={}", proxy.address().toString());
            return Const.PROXY_INVALID;
        } else {
            log.info(html.title() + " " + questionId);
        }

        Element ele = html.getElementById("js-initialData");

        String text = ele.toString();
        String start = "<script id=\"js-initialData\" type=\"text/json\">";
        String end = "</script>";
        String substring = text.substring(start.length(), text.length() - end.length());
        JSONObject jsonObject = JSON.parseObject(substring);
        return parseQuestionBasic(jsonObject,id,questionId);

    }


    public int parseQuestionBasic(JSONObject jsonObject, int primaryId,int questionId){
        int count = 0;
        if (jsonObject != null) {
            JSONObject entities = jsonObject.getJSONObject("initialState").getJSONObject("entities");
            JSONObject questions = entities.getJSONObject("questions");
            Set<String> ids = questions.keySet();
            if (ids.size() == 0){//登录
                ZhihuQuestionBasic basic = new ZhihuQuestionBasic();
                basic.setId(primaryId);
                basic.setTitle("login");
                basic.setQid(questionId);
                count += zhihuQuestionBasicService.update(basic);
                log.info("save success = {}", JSON.toJSONString(basic));
                return count;
            }
            for (String id : ids) {
                JSONObject questionsJSONObject = questions.getJSONObject(id);
                if (questionsJSONObject != null) {
                    ZhihuQuestionEntity entity = questionsJSONObject.toJavaObject(ZhihuQuestionEntity.class);

                    JSONObject author = questionsJSONObject.getJSONObject("author");
                    entity.setAuthorId(author.getString("id"));
                    entity.setAuthorName(author.getString("name"));
                    entity.setAuthorUrlToken(author.getString("urlToken"));
                    entity.setAuthorAvatarUrl(author.getString("avatarUrl"));
                    entity.setAuthorIsOrg(author.getBoolean("isOrg"));
                    entity.setAuthorType(author.getString("type"));
                    entity.setAuthorUserType(author.getString("userType"));
                    entity.setAuthorHeadline(author.getString("headline"));
                    entity.setAuthorGender(author.getIntValue("gender"));
                    entity.setAuthorIsAdvertiser(author.getBoolean("isAdvertiser"));
                    entity.setAuthorIsPrivacy(author.getBoolean("isPrivacy"));

                    entity.setCreated(new Date(questionsJSONObject.getLongValue("created") * 1000));
                    entity.setUpdatedTime(new Date(questionsJSONObject.getLongValue("updatedTime") * 1000));

                    ZhihuQuestionBasic basic = new ZhihuQuestionBasic();
                    BeanUtils.copyProperties(entity, basic);
                    Date date = new Date();
                    basic.setCreateTime(date);
                    basic.setUpdateTime(date);
                    basic.setId(primaryId);
                    basic.setQid(entity.getId());
                    basic.setVisitAnswerRate((entity.getAnswerCount() == 0)? entity.getVisitCount() : entity.getVisitCount() / entity.getAnswerCount());
                    basic.setVisitFollowRate((entity.getFollowerCount() == 0)? entity.getVisitCount() : entity.getVisitCount() / entity.getFollowerCount());
                    basic.setFollowAnswerRate((entity.getAnswerCount() == 0)? entity.getVisitCount() : entity.getFollowerCount() / entity.getAnswerCount());
                    int days = (int) ((new Date().getTime() - entity.getCreated().getTime()) / (1000*3600*24));
                    basic.setVisitDayRate((days == 0)?entity.getVisitCount(): entity.getVisitCount() / days);


                    try {
                        count += zhihuQuestionBasicService.update(basic);
                        log.info("save success = {}", JSON.toJSONString(basic));
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("save exception = {}", JSON.toJSONString(basic));
                    }


                    break;
                }
            }
        }
        return count;

    }

    public int parseQuestion(JSONObject jsonObject) {
        int count = 0;
        if (jsonObject != null) {
            JSONObject entities = jsonObject.getJSONObject("initialState").getJSONObject("entities");
            JSONObject questions = entities.getJSONObject("questions");
            Set<String> ids = questions.keySet();
            for (String id : ids) {
                JSONObject questionsJSONObject = questions.getJSONObject(id);
                if (questionsJSONObject != null) {
                    ZhihuQuestionEntity entity = questionsJSONObject.toJavaObject(ZhihuQuestionEntity.class);

                    JSONObject author = questionsJSONObject.getJSONObject("author");
                    entity.setAuthorId(author.getString("id"));
                    entity.setAuthorName(author.getString("name"));

                    entity.setCreated(new Date(questionsJSONObject.getLongValue("created") * 1000));
                    entity.setUpdatedTime(new Date(questionsJSONObject.getLongValue("updatedTime") * 1000));

                    SubQuestionDetail subQuestionDetailEntity = new SubQuestionDetail();
                    BeanUtils.copyProperties(entity, subQuestionDetailEntity);
                    subQuestionDetailEntity.setQuestionId(entity.getId());

                    subQuestionDetailEntity.setSnapTime(ElasticQuestionPrepareJob.getSnapTime(new Date()));

                    try {
                        count += updateAndSave(subQuestionDetailEntity);
                        log.info("save success = {}", JSON.toJSONString(subQuestionDetailEntity));
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("save exception = {}", JSON.toJSONString(subQuestionDetailEntity));
                    }

                    break;
                }
            }
        }
        return count;
    }


    /**
     * 存在更新，不存在直接创建
     *
     * @param entity
     * @return
     */
    @Transactional
    public int updateAndSave(SubQuestionDetail entity) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("questionId", entity.getQuestionId());
        map.put("snapTime", entity.getSnapTime());

        List<SubQuestionDetail> dbList = findListByMap(map);
        if (dbList == null) {
            return subQuestionDetailMapper.insert(entity);
        } else {
            Integer id = dbList.get(0).getId();
            entity.setId(id);
            entity.setCreateTime(dbList.get(0).getCreateTime());
            entity.setUpdateTime(new Date());
            return subQuestionDetailMapper.updateByPrimaryKey(entity);
        }
    }

    /**
     * 根据id 按10 取模，并获取最近的 limit 条数
     *
     * @param mod
     * @param snapTime
     * @param limit
     * @return
     */
    public List<SubQuestionDetail> findListByIdMod(int mod, String snapTime, int limit) {
        List<SubQuestionDetail> list = subQuestionDetailMapper.findListByIdMod(mod, snapTime, limit);
        return list;
    }

    /**
     * 获取图形  dataset
     * <p>
     * 第一版 值获取最近24个
     *
     * @param qid
     * @param type
     */
    public void chart(int qid, String type, Date start, Date end) {
        Example example = new Example(SubQuestionDetail.class);
        example.createCriteria().andEqualTo("questionId", qid);
        example.orderBy("id").desc();
    }

    /**
     * 默认 图形为24个
     *
     * @param qid
     * @param type
     */
    public List<List<Object>> chartDefault(int qid, int type) {
        ArrayList<String> snapTimeList = new ArrayList<>();
        Date now = new Date();
        if (Const.TYPE_HOUR == type) {
            for (int i = 0; i <= 24; i++) {
                snapTimeList.add(ElasticQuestionPrepareJob.getSnapTime(DateUtil.addMin(now, -60 * i)));
            }
        } else if (Const.TYPE_DAY == type) {
            for (int i = 0; i <= 7; i++) {
                snapTimeList.add(ElasticQuestionPrepareJob.getSnapTime(DateUtil.addMin(now, -60 * 24 * i)));
            }
        } else {
            for (int i = 0; i <= 24; i++) {
                snapTimeList.add(ElasticQuestionPrepareJob.getSnapTime(DateUtil.addMin(now, -60 * i)));
            }
        }

        log.debug("snapTimeList=[{}]", JSON.toJSONString(snapTimeList));
        Example example = new Example(SubQuestionDetail.class);
        example.createCriteria().andEqualTo("questionId", qid).andIn("snapTime", snapTimeList);
        example.orderBy("id").desc();
        List<SubQuestionDetail> list = subQuestionDetailMapper.selectByExample(example);
        List<List<Object>> dataSet = null;

        dataSet = genDataSet(list);

        return dataSet;
    }

    List<List<Object>> genDataSet(List<SubQuestionDetail> list) {
        //["Visit", "VisitAdd", "Follower", "FollowerAdd", "Answer", "AnswerAdd", "SnapTime", "Title"]
        ArrayList<Integer> Visit = new ArrayList<>();
        ArrayList<Integer> VisitAdd = new ArrayList<>();
        ArrayList<Integer> Follower = new ArrayList<>();
        ArrayList<Integer> FollowerAdd = new ArrayList<>();
        ArrayList<Integer> Answer = new ArrayList<>();
        ArrayList<Integer> AnswerAdd = new ArrayList<>();
        ArrayList<Date> SnapTime = new ArrayList<>();
        ArrayList<String> Title = new ArrayList<>();


        int size = list.size() - 1;//有效数量
        for (int i = 0; i < size; i++) {
            SubQuestionDetail q = list.get(i);
            SubQuestionDetail qNext = list.get(i + 1);
            Visit.add(q.getVisitCount());
            VisitAdd.add(q.getVisitCount() - qNext.getVisitCount());

            Follower.add(q.getFollowerCount());
            FollowerAdd.add(q.getFollowerCount() - qNext.getFollowerCount());

            Answer.add(q.getAnswerCount());
            AnswerAdd.add(q.getAnswerCount() - qNext.getAnswerCount());

            Date snapDate = DateUtil.getDateFromStringPattern(q.getSnapTime(), "yyyy-MM-dd-HH");
            log.info("[snapTime={}], [snapDate={}]", q.getSnapTime(), snapDate);
            SnapTime.add(snapDate);

            Title.add(q.getTitle());
        }

        ArrayList<List<Object>> dataSet = new ArrayList<>(size + 1);
        String[] names = {"浏览总数", "浏览增量", "话题关注人数", "话题关注增量", "回答总数", "回答增量", "时间", "标题", "浏览回答比"};
        List<Object> strings = Arrays.asList(names);
        dataSet.add(strings);

        for (int i = 0; i < size; i++) {
            ArrayList<Object> row = new ArrayList<>();
            row.add(Visit.get(i));
            row.add(VisitAdd.get(i));
            row.add(Follower.get(i));
            row.add(FollowerAdd.get(i));
            row.add(Answer.get(i));
            row.add(AnswerAdd.get(i));
            row.add(SnapTime.get(i));
            row.add(Title.get(i));
            int VisitAddAnswerAdd = (AnswerAdd.get(i) > 0 ? (VisitAdd.get(i) / AnswerAdd.get(i)) : VisitAdd.get(i));
            row.add(VisitAddAnswerAdd);
            dataSet.add(row);
        }

        log.debug("生成DataSet");
        log.debug(JSON.toJSONString(dataSet, SerializerFeature.PrettyFormat));
        return dataSet;

    }


}
