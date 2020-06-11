package top.duwd.common.mapper.sub;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import top.duwd.common.domain.sub.entity.ZhihuHot;

import java.util.List;

public interface ZhihuHotMapper extends Mapper<ZhihuHot> {

    @Select("SELECT count(*) from t_zhihu_hot t where t.qid=#{param1} ")
    int selectCountByQid(Integer qid);

    @Select("SELECT t.* from t_zhihu_hot t where t.create_time = t.update_time limit #{param1}")
    @ResultMap("BaseResultMap")
    List<ZhihuHot> findList(int limit);
}