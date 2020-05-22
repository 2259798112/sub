package top.duwd.common.mapper.sub;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import top.duwd.common.domain.sub.entity.Keyword;

import java.util.List;

public interface KeywordMapper extends Mapper<Keyword> {

    int insertList(List<Keyword> list);

    @Select("SELECT t.* from t_keyword t where t.counter < #{param1} and t.counter_m < #{param1} ORDER BY t.id DESC LIMIT #{param2} ")
    @ResultMap("BaseResultMap")
    List<Keyword> findKeyworToBaiduSearch(int searchCount,int size);
}