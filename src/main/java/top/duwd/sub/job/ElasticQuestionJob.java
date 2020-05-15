package top.duwd.sub.job;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.SubQuestionDetail;
import top.duwd.common.service.proxy.ProxyService;
import top.duwd.sub.service.SubQuestionDetailService;

import java.net.Proxy;
import java.util.Date;
import java.util.List;

//@ElasticJobConf(name = "ElasticQuestionJob", cron = "0/2 0-20 * * * ?", shardingTotalCount = 10, description = "问题快照任务")
@Slf4j
public class ElasticQuestionJob implements SimpleJob {

    @Autowired
    private SubQuestionDetailService subQuestionDetailService;
    @Autowired
    private ProxyService proxyService;

    private static int PROXY_COUNT = 0;
    private final static int PROXY_MUL_ENABLE_COUNT = 30;
    private final static int THREAD_LIMIT = 50;

    @Override
    public void execute(ShardingContext shardingContext) {
        String snapTime = ElasticQuestionPrepareJob.getSnapTime(new Date());

        int shardingItem = shardingContext.getShardingItem();
        log.info("Thread {} , 分片ID {}", Thread.currentThread().getName(), shardingItem);

        List<SubQuestionDetail> list = subQuestionDetailService.findListByIdMod(shardingItem, snapTime, THREAD_LIMIT);
        if (list == null || list.size() == 0) {
            return;
        }

        Proxy proxy = null;
        if (list.size() < PROXY_MUL_ENABLE_COUNT) {
            proxy = proxyService.getProxy(1);
        } else {
            proxy = proxyService.getProxy(1, shardingItem);
        }

        if (proxy == null) {
            PROXY_COUNT++;return;
        }

        for (SubQuestionDetail subQuestionDetail : list) {
            Integer qid = subQuestionDetail.getQuestionId();

            //先解析后存储
            int parse = subQuestionDetailService.parse(qid, proxy);
            if (parse > 0) {
                log.info("成功解析 qid={}", qid);
            } else {
                if (parse == Const.PROXY_INVALID) {
                    //ip 问题
                    if (PROXY_COUNT > 5) {
                        proxyService.getProxyAndSaveDB(shardingItem);
                        PROXY_COUNT = 0;
                    } else {
                        PROXY_COUNT++;
                    }
                }
            }
        }
    }

}
