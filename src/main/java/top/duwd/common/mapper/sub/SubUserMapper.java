package top.duwd.common.mapper.sub;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import top.duwd.common.domain.sub.entity.SubUser;

public interface SubUserMapper extends Mapper<SubUser> {


    @Select("    select user_id, type, tel, weixin, password, create_time, update_time " +
            "    from t_sub_user " +
            "    where tel = #{param1}")
    @ResultMap("BaseResultMap")
    SubUser findByTel(String tel);
}