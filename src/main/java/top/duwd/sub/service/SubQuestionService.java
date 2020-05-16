package top.duwd.sub.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.SubQuestion;
import top.duwd.common.domain.sub.entity.SubUser;
import top.duwd.common.exception.DuExceptionManager;
import top.duwd.common.exception.ErrorCodes;
import top.duwd.common.mapper.sub.SubQuestionMapper;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SubQuestionService implements IBaseService<SubQuestion> {
    @Autowired
    private SubQuestionMapper SubQuestionMapper;
    @Autowired
    private DuExceptionManager em;

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

    private PageInfo<SubQuestion> findListByMapPage(Map<String, Object> map, int pageNum, int pageSize, String sortField, int direct) {

        Example example = new Example(SubQuestion.class);
        Example.Criteria criteria = example.createCriteria();
        map.forEach((key, value) -> criteria.andEqualTo(key, value));
        if (Const.ASC == direct) {
            example.orderBy(sortField).asc();
        } else {
            example.orderBy(sortField).desc();
        }

        PageHelper.startPage(pageNum, pageSize);
        List<SubQuestion> list = SubQuestionMapper.selectByExample(example);
        PageInfo page = new PageInfo(list);
        return page;
    }

    public List<Integer> findValidQuestion() {
        List<SubQuestion> list = this.findListByKV("valid", Const.SUB_Q_VALID);
        if (list == null) {
            return null;
        }
        Set<Integer> collect = list.stream().map(SubQuestion::getQuestionId).collect(Collectors.toSet());
        return new ArrayList<>(collect);
    }

    /**
     * 根据用户uid 查找订阅列表
     *
     * @param userId
     * @param type
     * @param pageNum
     * @param pageSize
     * @param sortField
     * @param direct
     * @return
     */
    public PageInfo<SubQuestion> listByUserId(String userId, Integer type, int pageNum, int pageSize, String sortField, int direct) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        if (Const.SUB_Q_VALID == type || Const.SUB_Q_INVALID == type) {
            map.put("valid", type);
        }

        PageInfo<SubQuestion> list = findListByMapPage(map, pageNum, pageSize, sortField, direct);
        return list;
    }

    public int subAdd(SubUser user, int qid) {

        int count = countUserIdSubQuestion(user.getUserId(), Const.SUB_Q_VALID);
        //校验用户账户级别
        switch (user.getType()) {
            case Const.USER_TYPE_FREE:
                log.info("免费用户默认订阅位10个");
            default:
                if (count >= Const.USER_TYPE_FREE_LIMIT) {
                    throw em.create(ErrorCodes.USER_TYPE_LIMIT);
                }
                break;
        }

        boolean isQuestion = checkQuestion(qid);
        if (isQuestion) {

            SubQuestion subQuestion = new SubQuestion();
            subQuestion.setUserId(user.getUserId());
            subQuestion.setQuestionId(qid);
            subQuestion.setValid(Const.SUB_Q_VALID);

            Date date = new Date();
            subQuestion.setCreateTime(date);
            subQuestion.setUpdateTime(date);

            int save = SubQuestionMapper.insert(subQuestion);
            return save;
        } else {
            throw em.create(ErrorCodes.SUB_QUESTION_IS_NOT_A_QUESTION);
        }


    }

    /**
     * 判断话题有效性
     *
     * @param qid
     * @return
     */
    private boolean checkQuestion(int qid) {
        //目前默认为有效
        return true;
    }

    int countUserIdSubQuestion(String userId, int type) {
        //校验用户订阅的数量免费10个
        Example example = new Example(SubQuestion.class);
        example.createCriteria().andEqualTo("userId", userId)
                .andEqualTo("valid", type);
        int count = SubQuestionMapper.selectCountByExample(example);
        return count;
    }

    /**
     * 根据 uid qid 设置valid = 0
     * @param subUser
     * @param qid
     * @return
     */
    public int subDel(SubUser subUser, int qid) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", subUser.getUserId());
        map.put("questionId", qid);
        List<SubQuestion> list = this.findListByMap(map);
        //正常情况只会存在一条数据
        if (list != null && list.size() == 1) {
            SubQuestion dbQuestion = list.get(0);
            if (dbQuestion.getValid() != Const.SUB_Q_INVALID) {
                dbQuestion.setValid(Const.SUB_Q_INVALID);
                dbQuestion.setUpdateTime(new Date());
                return SubQuestionMapper.updateByPrimaryKey(dbQuestion);
            }
        }
        log.error("找不到订阅话题[uid={},qid={}]",subUser.getUserId(),qid);
        return 1;
    }
}
