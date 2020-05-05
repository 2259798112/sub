package top.duwd.sub.service;

import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

public interface IBaseService<T> {
    List<T> findListByKV(String k,Object v);
    List<T> findListByMap(Map<String,Object> map);
}
