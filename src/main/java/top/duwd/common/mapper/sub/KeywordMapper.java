package top.duwd.common.mapper.sub;

import java.util.List;

import tk.mybatis.mapper.common.Mapper;
import top.duwd.common.domain.sub.entity.Keyword;

public interface KeywordMapper extends Mapper<Keyword> {
    int insertList(List<Keyword> list);
}