package top.duwd.sub.job;

import com.alibaba.fastjson.JSON;
import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import top.duwd.common.domain.sub.entity.BaiduZhidaoBasic;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;
import top.duwd.sub.service.BaiduSearchResultService;
import top.duwd.sub.service.BaiduZhidaoService;
import top.duwd.sub.service.KeywordBaiduRealService;

import java.util.Date;
import java.util.List;

@ElasticJobConf(name = "BaiduZhidaoBasicJob", cron = "*/30 * * * * ?", shardingTotalCount = 1, description = "百度知道快照")
@Slf4j
public class BaiduZhidaoBasicJob implements SimpleJob {

    @Autowired
    private BaiduZhidaoService baiduZhidaoService;
    @Autowired
    private BaiduSearchResultService baiduSearchResultService;
    @Autowired
    private KeywordBaiduRealService baiduRealJob;
    public static final String site = "百度知道";
    @Override
    public void execute(ShardingContext shardingContext) {
        int shardingItem = shardingContext.getShardingItem();
        //去重
        //要处理的结果
        //获取基本信息
        //获取pv
        baiduRealJob.genRealUrl(site);
        run();
        parse();
    }


    public void run() {

        //1，删除重复记录
        //2，获取全部信息, https://www.zhihu.com/question 开头
        //插入数据
        //修改 记录
        int i = baiduZhidaoService.deleteRepeat();
        List<KeywordBaiduSearchResult> baiduSearchResultList = baiduZhidaoService.findListZhidaoGenQid();

        if (baiduSearchResultList != null) {
            for (KeywordBaiduSearchResult searchResult : baiduSearchResultList) {
                //创建基本信息 记录， 后续解析
                int save = baiduZhidaoService.saveBasic(searchResult);
                if (save > 0) {
                    searchResult.setResultOrder(1);
                    searchResult.setUpdateTime(new Date());
                    baiduSearchResultService.update(searchResult);
                }
            }
        }
    }

    public void parse() {
        List<BaiduZhidaoBasic> list = baiduZhidaoService.findListNoParse(20, 3, 60);
        if (list != null && list.size() > 0) {
            for (BaiduZhidaoBasic entity : list) {
                BaiduZhidaoBasic baiduZhidaoBasic = new BaiduZhidaoBasic();
                BeanUtils.copyProperties(entity, baiduZhidaoBasic);
                try {
                    baiduZhidaoService.parse(baiduZhidaoBasic);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("解析百度知道 异常 [entity={}]", JSON.toJSONString(baiduZhidaoBasic));
                    baiduZhidaoBasic.setCount(entity.getCount() + 1);
                    baiduZhidaoService.update(baiduZhidaoBasic);
                    continue;
                }
                log.info("解析百度知道 成功 [entity={}]", JSON.toJSONString(baiduZhidaoBasic));
                baiduZhidaoService.update(baiduZhidaoBasic);
            }
        }
    }
}
