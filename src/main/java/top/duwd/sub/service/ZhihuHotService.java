package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.duwd.common.domain.sub.entity.ZhihuHot;
import top.duwd.common.mapper.sub.ZhihuHotMapper;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class ZhihuHotService {
    @Autowired
    private ZhihuHotMapper mapper;

    public int savaList(List<Integer> list, String type) {
        HashSet<Integer> integers = new HashSet<>(list);
        //查询添加
        Date date = new Date();
        int saveCount = 0;
        for (Integer qid : integers) {
            int count = mapper.selectCountByQid(qid);
            if (count > 0) {
                log.info("[{}] already in db", qid);
            } else {
                ZhihuHot entity = new ZhihuHot(null, qid, date, date, type);
                try {
                    saveCount += mapper.insert(entity);
                } catch (Exception e) {
                    log.error("insert fail entity=[{}]", JSON.toJSONString(entity));
                }
            }
        }
        return saveCount;
    }

    public List<ZhihuHot> findList(int limit){
        List<ZhihuHot> list = mapper.findList(limit);
        return list;
    }

    public int update(ZhihuHot zhihuHot){
        zhihuHot.setUpdateTime(new Date());
        return mapper.updateByPrimaryKey(zhihuHot);
    }
}
