package top.duwd.common.mapper.sub;

import tk.mybatis.mapper.common.Mapper;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;

import java.util.List;

public interface KeywordBaiduSearchResultMapper extends Mapper<KeywordBaiduSearchResult> {

    int insertList(List<KeywordBaiduSearchResult> searchResults);
}