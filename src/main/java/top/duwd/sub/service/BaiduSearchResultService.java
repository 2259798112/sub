package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;
import top.duwd.common.mapper.sub.KeywordBaiduSearchResultMapper;
import top.duwd.dutil.http.html.dto.BaiduItem;
import top.duwd.dutil.http.html.dto.BaiduSearchResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class BaiduSearchResultService {

    @Autowired
    private KeywordBaiduSearchResultMapper mapper;

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
            log.info("批量保存 百度搜索结果 [searchResults={}]",JSON.toJSONString(searchResults));
            int count = mapper.insertList(searchResults);
            return count;
        } else {
            return 0;
        }

    }
}
