package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;
import top.duwd.common.mapper.sub.KeywordBaiduSearchResultMapper;
import top.duwd.dutil.http.html.dto.BaiduItem;
import top.duwd.dutil.http.html.dto.BaiduSearchResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class BaiduSearchResultService {

    @Autowired
    private KeywordBaiduSearchResultMapper mapper;

    //对 百度结果 知乎 去重
    public int deleteRepeat(String site) {
        List<Integer> minIdList = mapper.findRepeatIdListBySite(site);
        List<Integer> saveIdListAll = mapper.findRepeatIdListAllBySite(site);
        List<Integer> deleteAll = new ArrayList<>();
        if (saveIdListAll != null && saveIdListAll.size() > 0) {

            for (Integer i : saveIdListAll) {
                if (!minIdList.contains(i)) {
                    deleteAll.add(i);
                }
            }

        }

        if (deleteAll.size() > 0) {
            log.info("删除重复数据 id list [{}]", JSON.toJSONString(deleteAll));
            Example example = new Example(KeywordBaiduSearchResult.class);
            example.createCriteria().andIn("id", deleteAll);
            return mapper.deleteByExample(example);
        } else {
            return 0;
        }
    }

    public int insertList(List<BaiduSearchResult> list) {
        // 转换成 KeywordBaiduSearchResult
        ArrayList<KeywordBaiduSearchResult> searchResults = new ArrayList<>();
        HashSet<String> set = new HashSet<>();
        for (BaiduSearchResult baiduSearchResult : list) {
            List<BaiduItem> items = baiduSearchResult.getItems();
            if (items != null && items.size() > 0) {
                for (BaiduItem item : items) {

                    KeywordBaiduSearchResult entity = new KeywordBaiduSearchResult();
                    entity.setKeyword(baiduSearchResult.getKeyword());
                    entity.setRs(JSON.toJSONString(baiduSearchResult.getRs()));
                    entity.setCreateTime(baiduSearchResult.getCreateTime());

                    entity.setTitle(item.getTitle());
                    entity.setUrlSource(item.getUrl());

                    if (!StringUtils.isEmpty(entity.getUrlSource()) && !entity.getUrlSource().startsWith("http://www.baidu.com")){
                        entity.setUrlReal(entity.getUrlSource());
                    }

                    entity.setBaiduType(item.getType());
                    entity.setBaiduPage(item.getPage());
                    entity.setTargetSite(item.getSite());

                    String uni = entity.getKeyword() + entity.getTitle() + entity.getBaiduType() + entity.getTargetSite();
                    if (set.contains(uni)) {
                    } else {
                        searchResults.add(entity);
                        set.add(uni);
                    }
                }
            }
        }

        if (searchResults.size() > 0) {
            log.info("批量保存 百度搜索结果 [searchResults={}]", JSON.toJSONString(searchResults));
            int count = mapper.insertList(searchResults);
            return count;
        } else {
            return 0;
        }

    }

    public List<KeywordBaiduSearchResult> findListNoUrlReal(String targetSite, int limit) {
        if (StringUtils.isEmpty(targetSite)) {
            return mapper.findListNoUrlRealAll(10);
        } else {
            return mapper.findListNoUrlReal(targetSite, limit);
        }
    }
    public List<KeywordBaiduSearchResult> findListNoUrlRealWithPrefix(String prefix, int limit) {
        String like = prefix+"%";
        return mapper.findListNoUrlRealWithPrefix(like,limit);
    }

    public int updateUrlRead(KeywordBaiduSearchResult entity) {
        entity.setUpdateTime(new Date());
        return mapper.updateByPrimaryKeySelective(entity);
    }

    public int update(KeywordBaiduSearchResult entity) {
        return mapper.updateByPrimaryKeySelective(entity);
    }


}
