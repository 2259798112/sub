package top.duwd.common.mapper.sub;

import java.util.List;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import top.duwd.common.domain.sub.entity.SubQuestionDetail;

public interface SubQuestionDetailMapper extends Mapper<SubQuestionDetail> {

    int insertList(List<SubQuestionDetail> list);

    @Select("select t.* from t_sub_question_detail t where t.title is NULL and t.snap_time=#{param1} limit #{param2} ")
    @ResultMap("BaseResultMap")
    List<SubQuestionDetail> findLastN(String snapTime, int limit);

    @Select("select t.* from t_sub_question_detail t where t.question_id=#{param1} order by t.update_time DESC")
    @ResultMap("BaseResultMap")
    List<SubQuestionDetail> findListByQuestionId(int qid);

    @Select("SELECT t.id, t.question_id FROM t_sub_question_detail t " +
            "WHERE t.title is NULL and t.id MOD '10' = #{param1} and t.snap_time=#{param2}  ORDER BY t.id ASC limit #{param3}")
    @ResultMap("BaseResultMap")
    List<SubQuestionDetail> findListByIdMod(int mode, String snapTime, int limit);
}