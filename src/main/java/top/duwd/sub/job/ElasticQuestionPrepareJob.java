package top.duwd.sub.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.genid.GenId;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.SubQuestion;
import top.duwd.common.domain.sub.entity.SubQuestionDetail;
import top.duwd.common.service.proxy.ProxyService;
import top.duwd.dutil.date.DateUtil;
import top.duwd.sub.service.SubQuestionDetailService;
import top.duwd.sub.service.SubQuestionService;

import javax.persistence.criteria.CriteriaBuilder;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@ElasticJobConf(name = "ElasticQuestionPrepareJob", cron = "0 50 * * * ?", shardingTotalCount = 1,description = "准备数据")
@Slf4j
public class ElasticQuestionPrepareJob implements SimpleJob {
    @Autowired
    private SubQuestionService subQuestionService;
    @Autowired
    private SubQuestionDetailService subQuestionDetailService;

    public static String SNAP_TIME = "";

    @Scheduled(cron = "0 30 * * * ?")
    public void setSnapTime() {
        setNextHourSnapTime();
    }

    private void setNextHourSnapTime(){
        log.info("setNextHourSnapTime");
        Date date = DateUtil.addMin(new Date(), 60);
        String snapTime = getSnapTime(date);
        SNAP_TIME = snapTime;
    }


    public static String getSnapTime(Date date) {
        return JSON.toJSONString(date, SerializerFeature.WriteDateUseDateFormat).replace(" ", "-").substring(1, 14);
    }


    private List<SubQuestionDetail> genListFromQuestionIdList(List<Integer> qidList, Date date, String snapTime) {
        List<SubQuestionDetail> list = new ArrayList<>(qidList.size());
        for (Integer qid : qidList) {
            SubQuestionDetail subQuestionDetail = new SubQuestionDetail();
            subQuestionDetail.setQuestionId(qid);
            subQuestionDetail.setCreateTime(date);
            subQuestionDetail.setUpdateTime(date);
            subQuestionDetail.setSnapTime(snapTime);
            list.add(subQuestionDetail);
        }
        return list;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        if (shardingContext.getShardingItem()!=0){
            return;
        }

        log.info("subQuestionDetailService.insertList");
        List<Integer> qidList = subQuestionService.findValidQuestion();
        if (qidList == null) {
            return;
        }

        if (StringUtils.isEmpty(SNAP_TIME)){
            setNextHourSnapTime();
        }

        List<SubQuestionDetail> details = genListFromQuestionIdList(qidList, new Date(), SNAP_TIME);
        int count = subQuestionDetailService.insertList(details, 1000);
        log.info("准备数据，批量插入 {} 次",count);
    }
}
