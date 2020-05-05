package top.duwd.sub.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.SubQuestion;
import top.duwd.common.mapper.sub.SubQuestionMapper;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubQuestionService implements IBaseService<SubQuestion> {
    @Resource
    private SubQuestionMapper SubQuestionMapper;

    @Override
    public List<SubQuestion> findListByKV(String k, Object v) {
        Example example = new Example(SubQuestion.class);
        example.createCriteria().andEqualTo(k, v);

        List<SubQuestion> list = SubQuestionMapper.selectByExample(example);
        return list;

    }

    @Override
    public List<SubQuestion> findListByMap(Map<String, Object> map) {

        Example example = new Example(SubQuestion.class);
        Example.Criteria criteria = example.createCriteria();
        map.forEach((key, value) -> criteria.andEqualTo(key, value));

        List<SubQuestion> list = SubQuestionMapper.selectByExample(example);
        return list;
    }

    public PageInfo<SubQuestion> findListByMapPage(Map<String, Object> map,int pageNum,int pageSize) {

        Example example = new Example(SubQuestion.class);
        Example.Criteria criteria = example.createCriteria();
        map.forEach((key, value) -> criteria.andEqualTo(key, value));

        PageHelper.startPage(pageNum,pageSize);
        List<SubQuestion> list = SubQuestionMapper.selectByExample(example);
        PageInfo page = new PageInfo(list);
        return page;
    }

    public List<Integer> findValidQuestion() {
        List<SubQuestion> list = this.findListByKV("valid", Const.VALID);
        if (list == null) {
            return null;
        }
        Set<Integer> collect = list.stream().map(SubQuestion::getQuestionId).collect(Collectors.toSet());
        return new ArrayList<>(collect);
    }

    public PageInfo<SubQuestion> listByUserId(String userId, Integer type,int pageNum,int pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("valid", type);

        PageInfo<SubQuestion> list = findListByMapPage(map, pageNum, pageSize);
        return list;
    }
}
