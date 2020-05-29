package top.duwd.sub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;
import top.duwd.common.mapper.sub.KeywordBaiduSearchResultMapper;

import java.util.ArrayList;
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

    public static final String prefix = "zhidao.baidu.com/question/";
    public static final String SITE_ZHIDAO = "百度知道";

    //对 百度结果 知乎 去重
    public int deleteRepeat() {
       return baiduSearchResultService.deleteRepeat(SITE_ZHIDAO);
    }

    /**
     * 获取未处理的 百度结果  知乎question
     * @return
     */
    public List<KeywordBaiduSearchResult> findListZhidao() {
        List<KeywordBaiduSearchResult> list = keywordBaiduSearchResultMapper.findListBySite("%" + prefix + "%", 100,SITE_ZHIDAO);
        List<KeywordBaiduSearchResult> results = new ArrayList<>();
        List<Long> qids = new ArrayList<>();


        if (list != null && list.size() > 0) {
            for (KeywordBaiduSearchResult url : list) {
                long qid = findQid(url.getUrlReal(), prefix);
                if (qid != 0 && qids.indexOf(qid) < 0) {
                    qids.add(qid);
                    results.add(url);
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
}
