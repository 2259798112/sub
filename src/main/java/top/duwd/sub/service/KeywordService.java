package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.Keyword;
import top.duwd.common.exception.DuExceptionManager;
import top.duwd.common.mapper.sub.KeywordMapper;
import top.duwd.common.mapper.sub.KeywordUserMapper;
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
    private KeywordUserMapper keywordUserMapper;
    @Autowired
    private DuExceptionManager em;


    /**
     * 查找 主词 和 列表次
     *
     * @param keywordMain
     * @param keywordList
     * @return
     */
    private List<String> filterCustomImport(String keywordMain, List<String> keywordList) {
        //过滤 主词和 导入词

        Example example = new Example(Keyword.class);
        example.createCriteria().andEqualTo("keywordMain", keywordMain)
                .andEqualTo("plat", Const.KEYWORD_PLAT_CUSTOM)
                .andIn("keywordTail", keywordList);

        List<Keyword> keywords = keywordMapper.selectByExample(example);
        if (keywords == null || keywords.size() == 0) {
            return null;
        }

        for (Keyword keyword : keywords) {
            if (keywordList.contains(keyword.getKeywordTail())) {
                keywordList.remove(keyword.getKeywordTail());
            }
        }
        return keywordList;
    }

    //自己 导入关键词列表 已经过滤过的keywordList
    public List<Keyword> importKeyword(String keywordMain, List<String> keywordList) {
        List<String> words = this.filterCustomImport(keywordMain, keywordList);

        if (words != null && words.size() > 0) {
            ArrayList<Keyword> keywords = new ArrayList<>();
            Date date = new Date();
            for (String keyword : words) {
                Keyword item = new Keyword();
                item.setKeywordMain(keywordMain);
                item.setKeywordTail(keyword);
                item.setPlat(Const.KEYWORD_PLAT_CUSTOM);
                item.setCounter(0);
                item.setCounterM(0);
                item.setCreateTime(date);
                item.setUpdateTime(date);
                keywords.add(item);
            }
            //数据库关键词去重
            return keywords;
        } else {
            return null;
        }
    }


    //用于定时调度
    public List<Keyword> findKeywordListFromBaidu(String keyword) {
        //baidu PC
        ArrayList<Keyword> keywords = new ArrayList<>();
        Date date = new Date();

        HashMap<String, String> hMapPC = null;
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

    private void searchRSMobile(String keyword, ArrayList<Keyword> keywords, Date date, String rsKeyword) {
        Keyword pcKeyword = new Keyword();
        pcKeyword.setPlat(Const.BaiduMo);
        pcKeyword.setKeywordMain(keyword);
        pcKeyword.setKeywordTail(rsKeyword);
        pcKeyword.setCounter(0);
        pcKeyword.setCounterM(0);
        pcKeyword.setCreateTime(date);
        pcKeyword.setUpdateTime(date);
        keywords.add(pcKeyword);
    }

    private void serachRSPC(String keyword, ArrayList<Keyword> keywords, Date date, String rsKeyword) {
        Keyword pcKeyword = new Keyword();
        pcKeyword.setPlat(Const.BaiduPC);
        pcKeyword.setKeywordMain(keyword);
        pcKeyword.setKeywordTail(rsKeyword);
        pcKeyword.setCounter(0);
        pcKeyword.setCounterM(0);
        pcKeyword.setCreateTime(date);
        pcKeyword.setUpdateTime(date);
        keywords.add(pcKeyword);
    }

    //用于定时调度
    public List<Keyword> findMoreFromChinaZ(String keyword) {

        List<String> keywords = chinaZ.keyword(null, keyword, 1, 5);
        if (keywords == null || keywords.size() == 0) {
            return null;
        }

        ArrayList<Keyword> list = new ArrayList<>(keywords.size());
        Date date = new Date();
        for (String word : keywords) {
            Keyword entity = new Keyword();
            entity.setKeywordMain(keyword);
            entity.setKeywordTail(word);
            entity.setPlat(Const.KEYWORD_PLAT_ChinaZ);
            entity.setCreateTime(date);
            entity.setUpdateTime(date);
            entity.setCounter(0);
            entity.setCounterM(0);
            list.add(entity);
        }
        return list;
    }

    int insertList(List<Keyword> list) {
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

}
