package top.duwd.sub.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import top.duwd.common.domain.sub.entity.SubQuestionDetail;
import top.duwd.dutil.date.DateUtil;
import top.duwd.sub.service.SubQuestionDetailService;
import top.duwd.sub.service.SubQuestionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@ElasticJobConf(name = "ElasticQuestionPrepareJob", cron = "0 50 * * * ?", shardingTotalCount = 1,description = "准备数据")
@Slf4j
public class ElasticQuestionPrepareJob implements SimpleJob {
    @Autowired
    private SubQuestionService subQuestionService;
    @Autowired
    private SubQuestionDetailService subQuestionDetailService;


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

        List<SubQuestionDetail> details = genListFromQuestionIdList(qidList, new Date(), getSnapTime(DateUtil.addMin(new Date(),60)));
        int count = subQuestionDetailService.insertList(details, 1000);
        log.info("准备数据，批量插入 {} 次",count);
    }
}
