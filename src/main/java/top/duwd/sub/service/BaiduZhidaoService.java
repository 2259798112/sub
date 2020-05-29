package top.duwd.sub.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.domain.sub.entity.BaiduZhidaoBasic;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;
import top.duwd.common.domain.sub.entity.ZhihuQuestionBasic;
import top.duwd.common.mapper.sub.BaiduZhidaoBasicMapper;
import top.duwd.common.mapper.sub.KeywordBaiduSearchResultMapper;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.http.RequestBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BaiduZhidaoService {

    @Autowired
    private BaiduSearchResultService baiduSearchResultService;
    @Autowired
    private KeywordBaiduSearchResultMapper keywordBaiduSearchResultMapper;
    @Autowired
    private BaiduZhidaoBasicMapper mapper;
    @Autowired
    private RequestBuilder requestBuilder;

    public static final String prefix = "zhidao.baidu.com/question/";
    public static final String SITE_ZHIDAO = "百度知道";

    //对 百度结果 知乎 去重
    public int deleteRepeat() {
        return baiduSearchResultService.deleteRepeat(SITE_ZHIDAO);
    }

    /**
     * 获取未处理的 百度结果  知乎question
     *
     * @return
     */
    public List<KeywordBaiduSearchResult> findListZhidaoGenQid() {
        List<KeywordBaiduSearchResult> list = keywordBaiduSearchResultMapper.findListBySite("%" + prefix + "%", 100, SITE_ZHIDAO);

        List<KeywordBaiduSearchResult> results = new ArrayList<>();
        List<Long> qids = new ArrayList<>();//同一个qid 只处理一个， 避免去重不完整


        if (list != null && list.size() > 0) {
            for (KeywordBaiduSearchResult result : list) {
                long qid = findQid(result.getUrlReal(), prefix);
                if (qid != 0 && qids.indexOf(qid) < 0) {//有效的 qid，而且为本次处理不重复, 只为过滤
                    qids.add(qid);
                    results.add(result);
                }
            }
        }
        if (results.size() > 0) {
            return results;
        }
        return null;
    }

    private static long findQid(String content, String prefix) {
        if (StringUtils.isEmpty(content)) {
            return 0;
        }
        if (!content.contains(prefix)) {
            return 0;
        }


        //正则表达式，用于匹配非数字串，+号用于匹配出多个非数字串
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        boolean b = matcher.find();
        if (b) {
            String group = matcher.group(0);
            return Long.parseLong(group);
        } else {
            return 0;
        }
    }

    /**
     * 保存基本信息 id qid
     *
     * @param searchResult
     * @return
     */
    public int saveBasic(KeywordBaiduSearchResult searchResult) {
        long qid = findQid(searchResult.getUrlReal(), prefix);
        Example example = new Example(ZhihuQuestionBasic.class);
        example.createCriteria().andEqualTo("qid", qid);
        int count = mapper.selectCountByExample(example);
        if (count > 0) {
            return 1;
        }

        BaiduZhidaoBasic entity = new BaiduZhidaoBasic();
        entity.setQid(qid);
        entity.setCount(0);
        Date date = new Date();
        entity.setCreateTime(date);
        entity.setUpdateTime(date);
        return mapper.insert(entity);
    }

    //update min 之内的数据不查
    public List<BaiduZhidaoBasic> findListNoParse(int limit, int count, int min) {
        List<BaiduZhidaoBasic> wait = mapper.findListNoParse(limit, count, DateUtil.addMin(new Date(), -min));
        if (wait == null || wait.size()==0){
            return mapper.findListNoParseNowait(limit);
        }else {
            return wait;
        }

    }

    /**
     * 解析
     *
     * @param entity
     * @return
     */
    public BaiduZhidaoBasic parse(BaiduZhidaoBasic entity) throws Exception {

        //获取基本信息
        Long qid = entity.getQid();
        HashMap<String, String> hMap = new HashMap<>();
        hMap.put("Connection", "keep-alive");
        hMap.put("Pragma", "no-cache");
        hMap.put("Cache-Control", "no-cache");
        hMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        hMap.put("Accept", "text/html");
        hMap.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
        hMap.put("Host", "zhidao.baidu.com");
        String url = String.format("https://zhidao.baidu.com/question/%d.html", qid);

        String page = requestBuilder.get(url, hMap);
        Document document = Jsoup.parse(page);
        //设置title
        Elements title = document.select("#wgt-ask > h1 > span");
        if (title != null && title.size() > 0) {
            entity.setTitle(title.first().text());
        }
        //设置回答数
        Elements answers = document.getElementsByClass("question-all-answers-title");
        if (answers != null && answers.size() > 0) {
            String answerText = answers.first().text();
            //正则表达式，用于匹配非数字串，+号用于匹配出多个非数字串
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(answerText);
            boolean b = matcher.find();
            if (b) {
                String group = matcher.group(0);
                int answerCount = Integer.parseInt(group);
                entity.setAnswerCount(answerCount);
            }
        }
        //设置发布日期
        Elements datePublished = document.getElementsByAttributeValue("itemprop=", "datePublished");
        if (datePublished != null && datePublished.size() > 0) {
            String timeString = datePublished.first().attr("content");
            //2019-11-08 17:37:18
            Date created = DateUtil.getDateFromStringPattern(timeString, "yyyy-MM-dd HH:mm:ss");
            entity.setCreated(created);
        }

        //获取pv信息
        String qbpv = String.format("https://zhidao.baidu.com/api/qbpv?q=%d", qid);
        hMap.put("Referer", url);
        String pvString = requestBuilder.get(qbpv, hMap);
        int pv = Integer.parseInt(pvString);
        //获取 创建天数
        int days = (int) ((new Date().getTime() - entity.getCreated().getTime()) / (1000 * 3600 * 24));
        entity.setPv(pv);
        entity.setPvDayRate(days == 0 ? pv : (pv / days));
        entity.setCount(entity.getCount() + 1);
        return entity;
    }


    public int update(BaiduZhidaoBasic entity) {
        entity.setUpdateTime(new Date());
        return mapper.updateByPrimaryKey(entity);
    }

}
