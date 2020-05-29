package top.duwd.sub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;
import top.duwd.common.domain.sub.entity.ZhihuQuestionBasic;
import top.duwd.common.mapper.sub.KeywordBaiduSearchResultMapper;
import top.duwd.common.mapper.sub.ZhihuQuestionBasicMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ZhihuQuestionBasicService {

    @Autowired
    private ZhihuQuestionBasicMapper mapper;
    @Autowired
    private KeywordBaiduSearchResultMapper keywordBaiduSearchResultMapper;
    @Autowired
    private BaiduSearchResultService baiduSearchResultService;

    public static final String prefix = "zhihu.com/question/";
    public static final String SITE_ZHIHU = "知乎";

    //对 百度结果 知乎 去重
    public int deleteRepeat() {
       return baiduSearchResultService.deleteRepeat(SITE_ZHIHU);
    }

    /**
     * 获取未处理的 百度结果 为知乎question
     * 获取 qid
     *
     * @return
     */
    public List<KeywordBaiduSearchResult> findListZhihuQuestionGenQid() {
        List<KeywordBaiduSearchResult> list = keywordBaiduSearchResultMapper.findListBySite("%" + prefix + "%", 100,SITE_ZHIHU);
        List<KeywordBaiduSearchResult> results = new ArrayList<>();
        List<Integer> qids = new ArrayList<>();


        if (list != null && list.size() > 0) {
            for (KeywordBaiduSearchResult result : list) {
                int qid = findQid(result.getUrlReal(), prefix);
                if (qid != 0 && qids.indexOf(qid) < 0) {//有效的 qid，而且为本次处理不重复
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


    public static void main(String[] args) {

        ArrayList<String> strings = new ArrayList<>();
        strings.add("https://www.zhihu.com/question/25338860");
        strings.add("https://www.zhihu.com/question/21324495?sort=created");
        strings.add("https://www.zhihu.com/question/353163620/answer/875384348");
        strings.add("https://zhuanlan.zhihu.com/p/89260253");

        for (String string : strings) {
            System.out.println(string);
            int qid = findQid(string, prefix);

            System.out.println(qid);
        }

    }

    private static int findQid(String content, String prefix) {
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
            return Integer.parseInt(group);
        } else {
            return 0;
        }
    }

    @Transactional
    public int save(KeywordBaiduSearchResult searchResult) {
        int qid = findQid(searchResult.getUrlReal(), prefix);
        Example example = new Example(ZhihuQuestionBasic.class);
        example.createCriteria().andEqualTo("qid",qid);
        int count = mapper.selectCountByExample(example);
        if (count >0){
            return 1;
        }

        ZhihuQuestionBasic zhihuQuestionBasic = new ZhihuQuestionBasic();
        zhihuQuestionBasic.setQid(qid);
        Date date = new Date();
        zhihuQuestionBasic.setCreateTime(date);
        zhihuQuestionBasic.setUpdateTime(date);
        return mapper.insert(zhihuQuestionBasic);
    }

    public List<ZhihuQuestionBasic> findNoParse(int limit) {
        List<ZhihuQuestionBasic> list = mapper.findNoParseZhihu(limit);

        return list;
    }


    public int update(ZhihuQuestionBasic basic) {
        return mapper.updateByPrimaryKey(basic);
    }
}
