package top.duwd.common.mapper.sub;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;

import java.util.List;

public interface KeywordBaiduSearchResultMapper extends Mapper<KeywordBaiduSearchResult> {

    int insertList(List<KeywordBaiduSearchResult> searchResults);

    @Select("select id, url_source, url_real , target_site from t_keyword_baidu_search_result where target_site = #{param1} and url_real is NULL " +
            "order by id desc limit  #{param2} ")
    @ResultMap("BaseResultMap")
    List<KeywordBaiduSearchResult> findListNoUrlReal(String targetSite, int limit);

    @Select("select id, url_source, url_real , target_site from t_keyword_baidu_search_result where url_real is NULL " +
            "order by id desc limit  #{param1} ")
    @ResultMap("BaseResultMap")
    List<KeywordBaiduSearchResult> findListNoUrlRealAll(int limit);


    @Select("select id, url_source, url_real , target_site from t_keyword_baidu_search_result where url_real is NULL and url_source NOT LIKE #{param1} " +
            "order by id desc limit  #{param2} ")
    @ResultMap("BaseResultMap")
    List<KeywordBaiduSearchResult> findListNoUrlRealWithPrefix(String prefix,int limit);


    @Select("select min(id) from t_keyword_baidu_search_result where url_real is not null and target_site='知乎' group by url_real")
    List<Integer> findRepeatIdListZhihu();

    @Select("select id from t_keyword_baidu_search_result where url_real is not null and target_site='知乎' ")
    List<Integer> findRepeatIdListAllZhihu();


    @Select("select id, url_real from t_keyword_baidu_search_result where target_site='知乎' and result_order is NULL and url_real like #{param1} order by id desc limit #{param2}")
    @ResultMap("BaseResultMap")
    List<KeywordBaiduSearchResult> findListZhihuQuestionZhihu(String prefix, int limit);

}