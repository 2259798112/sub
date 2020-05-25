package top.duwd.common.mapper.sub;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import top.duwd.common.domain.sub.entity.ZhihuQuestionBasic;

import java.util.List;

public interface ZhihuQuestionBasicMapper extends Mapper<ZhihuQuestionBasic> {


    @Select("select t.* from t_zhihu_question_basic t where t.title is NULL order by id desc limit #{param1}")
    @ResultMap("BaseResultMap")
    List<ZhihuQuestionBasic> findNoParseZhihu(int limit);
}