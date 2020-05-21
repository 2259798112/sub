package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.Keyword;
import top.duwd.common.domain.sub.entity.KeywordUser;
import top.duwd.common.domain.sub.entity.SubUser;
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

    /**
     * 挖掘 关键词
     *
     * @param keyword
     * @return
     */


    //自己 导入关键词列表 已经过滤过的keywordList
    public List<Keyword> importKeyword(String keywordMain, List<String> keywordList) {

        if (keywordList != null && keywordList.size() > 0) {
            ArrayList<Keyword> keywords = new ArrayList<>();
            Date date = new Date();
            HashSet<String> set = new HashSet<>(keywordList);
            for (String keyword : set) {
                Keyword item = new Keyword();
                item.setKeywordMain(keywordMain);
                item.setKeywordTail(keyword);
                item.setPlat(Const.CUSTOM);
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

    //从平台 挖掘关键词
    /*
    public List<Keyword> findFromWeb(String keyword, String... plat) {
        ArrayList<Keyword> keywords = new ArrayList<>();

        for (String p : plat) {
            if (Const.Baidu.equalsIgnoreCase(p)) {
                List<Keyword> list = findKeywordListFromBaidu(keyword);
                if (list != null && list.size() > 0) {
                    keywords.addAll(list);
                }
            }

            if (Const.ChinaZ.equalsIgnoreCase(p)) {
                List<Keyword> list = findMoreFromChinaZ(keyword);
                if (list != null && list.size() > 0) {
                    keywords.addAll(list);
                }
            }
        }
        return keywords;
    }

     */

    //用于定时调度
    List<Keyword> findKeywordListFromBaidu(String keyword) {
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
            entity.setPlat(Const.ChinaZ);
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

    public void add(SubUser subUser, String keyword, JSONArray platArray, JSONArray importArray) {
        if (!StringUtils.isEmpty(keyword)){
            //先保留原始数据

            KeywordUser keywordUser = new KeywordUser();
            keywordUser.setKeywordMain(keyword);
            keywordUser.setUserId(subUser.getUserId());
            if (platArray !=null){
                keywordUser.setPlat(platArray.toJSONString());
            }
            if (importArray !=null){
                keywordUser.setImportList(importArray.toJSONString());
            }
            Date date = new Date();
            keywordUser.setCreateTime(date);
            keywordUser.setUpdateTime(date);


        }
    }
}
