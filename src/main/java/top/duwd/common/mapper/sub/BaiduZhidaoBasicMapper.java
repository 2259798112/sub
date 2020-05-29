package top.duwd.common.mapper.sub;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import top.duwd.common.domain.sub.entity.BaiduZhidaoBasic;

import java.util.Date;
import java.util.List;

public interface BaiduZhidaoBasicMapper extends Mapper<BaiduZhidaoBasic> {
    @Select("select t.* from t_baidu_zhidao_basic t where t.title is NULL and t.count < #{param2} and t.update_time < #{param3} order by id desc limit #{param1}")
    @ResultMap("BaseResultMap")
    List<BaiduZhidaoBasic> findListNoParse(int limit, int count, Date date);

    @Select("select t.* from t_baidu_zhidao_basic t where t.title is NULL and t.count ='0' order by id desc limit #{param1}")
    @ResultMap("BaseResultMap")
    List<BaiduZhidaoBasic> findListNoParseNowait(int limit);
}