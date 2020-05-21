package top.duwd.common.mapper.sub;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import top.duwd.common.domain.sub.entity.KeywordUser;

import java.util.Date;
import java.util.List;

public interface KeywordUserMapper extends Mapper<KeywordUser> {

    @Select("select id, keyword_main, user_id, plat, import_list, create_time, update_time" +
            " from t_keyword_user t" +
            " where t.create_time != t.update_time and t.create_time BETWEEN #{param1} AND #{param2}")
    @ResultMap("BaseResultMap")
    List<KeywordUser> findListToParse(Date start, Date end);
}