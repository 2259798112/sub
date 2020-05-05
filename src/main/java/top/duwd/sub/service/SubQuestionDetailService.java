package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import top.duwd.common.domain.sub.entity.SubQuestion;
import top.duwd.common.domain.sub.entity.SubQuestionDetail;
import top.duwd.common.domain.zhihu.ZhihuQuestionEntity;
import top.duwd.common.mapper.sub.SubQuestionDetailMapper;
import top.duwd.common.service.proxy.ProxyService;
import top.duwd.common.util.CollectionUtil;
import top.duwd.dutil.http.RequestBuilder;
import top.duwd.sub.job.QuestionSnapJob;

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

    @Transactional
    public int insertList(List<SubQuestionDetail> list, int pageSize) {
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
            htmlString = requestBuilder.getWithProxy(url, hMap, proxy);
        } catch (SocketTimeoutException e) {
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
                    subQuestionDetailEntity.setSnapTime(QuestionSnapJob.SNAP_TIME);

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

    public List<SubQuestionDetail> findLastN(String snapTime, int limit) {
        List<SubQuestionDetail> list = subQuestionDetailMapper.findLastN(snapTime, limit);
        return list;
    }


    public List<SubQuestionDetail> questionDetails(Integer questionId, Integer type) {
        List<SubQuestionDetail> list = null;
        switch (type){
            case Const.TYPE_HOUR:
                PageHelper.startPage(1,24);
                list = subQuestionDetailMapper.findListByQuestionId(questionId);
                break;
            case Const.TYPE_DAY:
                break;
            default:
                break;
        }

        return list;

    }
}
