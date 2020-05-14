package top.duwd.sub.job;

import com.alibaba.fastjson.JSON;
import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

import java.text.SimpleDateFormat;
import java.util.Date;

//@ElasticJobConf(name = "T", cron = "0/2 * * * * ?",shardingItemParameters = "0=yi,1=er", shardingTotalCount = 2,description = "简单任务")
public class MySimpleJob implements SimpleJob {

    @Override
    public void execute(ShardingContext context) {


        int shardingItem = context.getShardingItem();

        String shardParameter = context.getShardingParameter();

        System.out.println(Thread.currentThread().getName());
        System.out.printf("shardingItem=%d, shardParameter=%s \n",shardingItem,shardParameter);


    }

}
