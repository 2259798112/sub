package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.BaiduCookie;
import top.duwd.common.domain.sub.entity.Keyword;
import top.duwd.common.exception.DuExceptionManager;
import top.duwd.common.exception.ErrorCodes;
import top.duwd.common.mapper.sub.KeywordMapper;
import top.duwd.dutil.http.html.Baidu;
import top.duwd.dutil.http.html.ChinaZ;
import top.duwd.dutil.http.html.dto.BaiduSearchResult;

import java.util.*;

@Service
@Slf4j
public class KeywordService implements IBaseService<Keyword> {

    @Autowired
    private ChinaZ chinaZ;
    @Autowired
    private Baidu baidu;
    @Autowired
    private KeywordMapper keywordMapper;
    @Autowired
    private BaiduCookieService baiduCookieService;
    @Autowired
    private DuExceptionManager em;

    //自己 导入关键词列表 已经过滤过的keywordList
    public int importKeyword(String keywordMain, List<String> keywordList, String plat) {
        if (StringUtils.isEmpty(keywordMain) || keywordList == null || keywordList.size() == 0) {
            return 0;
        }
        ArrayList<Keyword> keywords = new ArrayList<>();
        Date date = new Date();
        for (String keywordTail : keywordList) {
            Keyword keyword = new Keyword(null, 0, 0, keywordMain, keywordTail, plat, date, date);
            keywords.add(keyword);
        }
        return insertList(keywords);
    }

    //用于定时调度
    public List<Keyword> findKeywordListFromBaidu(String keyword) {
        HashMap<String, String> hMapPC = getBaiduHeaderMap();

        //baidu PC
        ArrayList<Keyword> keywords = new ArrayList<>();
        Date date = new Date();

        BaiduSearchResult searchPC = baidu.searchPC(hMapPC, keyword, 1, null);
        if (searchPC != null && searchPC.getRs() != null && searchPC.getRs().size() > 0) {
            for (String rsKeyword : searchPC.getRs()) {//遍历第一页 相关词
                serachRSPC(keyword, keywords, date, rsKeyword);

                //迭代一层
                BaiduSearchResult searchPCChild = baidu.searchPC(hMapPC, rsKeyword, 1, null);
                if (searchPCChild != null && searchPCChild.getRs() != null && searchPCChild.getRs().size() > 0) {
                    serachRSPC(keyword, keywords, date, rsKeyword);
                }
            }
        }

        HashMap<String, String> hMapMobile = null;
        BaiduSearchResult searchM = baidu.searchM(hMapMobile, keyword, 1, null);
        if (searchM != null && searchM.getRs() != null && searchM.getRs().size() > 0) {
            for (String rsKeyword : searchM.getRs()) {//遍历第一页 相关词
                searchRSMobile(keyword, keywords, date, rsKeyword);

                //迭代一层
                BaiduSearchResult searchMChild = baidu.searchM(hMapMobile, rsKeyword, 1, null);
                if (searchMChild != null && searchMChild.getRs() != null && searchMChild.getRs().size() > 0) {
                    searchRSMobile(keyword, keywords, date, rsKeyword);
                }
            }
        }
        return keywords;
    }

    @NotNull
    public HashMap<String, String> getBaiduHeaderMap() {
        HashMap<String, String> hMapPC = null;
        //读取 baidu cookie
        BaiduCookie baiduCookie = baiduCookieService.findListInDays(1, Const.KEYWORD_PLAT_Baidu);
        if (baiduCookie == null) {
            throw em.create(ErrorCodes.KEYWORD_BAIDU_COOKIE);
        }

        hMapPC.put("Cookie", baiduCookie.getCookie());
        hMapPC.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        return hMapPC;
    }

    private void searchRSMobile(String keyword, ArrayList<Keyword> keywords, Date date, String rsKeyword) {
        Keyword pcKeyword = new Keyword(null, 0, 0, keyword, rsKeyword, Const.BaiduMo, date, date);
        keywords.add(pcKeyword);
    }

    private void serachRSPC(String keyword, ArrayList<Keyword> keywords, Date date, String rsKeyword) {
        Keyword pcKeyword = new Keyword(null, 0, 0, keyword, rsKeyword, Const.BaiduPC, date, date);
        keywords.add(pcKeyword);
    }

    //获取站长之间前5页数据
    public List<Keyword> findKeywordFromChinaZ(String keyword) {

        List<String> keywords = chinaZ.keyword(null, keyword, 1, 5);
        if (keywords == null || keywords.size() == 0) {
            return null;
        }

        ArrayList<Keyword> list = new ArrayList<>(keywords.size());
        Date date = new Date();
        for (String word : keywords) {
            Keyword entity = new Keyword(null, 0, 0, keyword, word, Const.KEYWORD_PLAT_ChinaZ, date, date);
            list.add(entity);
        }
        return list;
    }

    @Transactional
    public int insertList(List<Keyword> list) {
        log.info("批量保存 keyword [list={}]", JSON.toJSONString(list));
        int count = 0;
        if (list == null || list.size() == 0) {

        } else {
            count = keywordMapper.insertList(list);
        }
        log.info("批量保存 [count={}]", count);
        return count;
    }

    @Override
    public List<Keyword> findListByKV(String k, Object v) {
        Example example = new Example(Keyword.class);
        example.createCriteria().andEqualTo(k, v);
        return keywordMapper.selectByExample(example);
    }

    @Override
    public List<Keyword> findListByMap(Map<String, Object> map) {
        Example example = new Example(Keyword.class);
        Example.Criteria criteria = example.createCriteria();
        map.forEach((key, value) -> criteria.andEqualTo(key, value));
        return keywordMapper.selectByExample(example);
    }

    /**
     * 过滤 导入关键词
     *
     * @param keywordMain
     * @param keywordTailSet
     * @return
     */
    public List<String> filterImportList(String keywordMain, HashSet<String> keywordTailSet, String plat) {
        Example example = new Example(Keyword.class);
        example.createCriteria().andEqualTo("keywordMain", keywordMain)
                .andEqualTo("plat", plat)
                .andIn("keywordTail", keywordTailSet);
        List<Keyword> keywords = keywordMapper.selectByExample(example);

        if (keywords == null || keywords.size() == 0) {
            log.info("导入列表无重复");
        } else {
            for (Keyword keyword : keywords) {
                keywordTailSet.remove(keyword.getKeywordTail());
            }
        }
        return new ArrayList<>(keywordTailSet);
    }

    //获取待搜索的关键词
    public List<Keyword> findKeyworToBaiduSearch(int count, int size) {
        List<Keyword> list = keywordMapper.findKeyworToBaiduSearch(count, size);
        return list;
    }
}
