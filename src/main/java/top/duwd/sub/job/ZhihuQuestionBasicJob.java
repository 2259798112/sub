package top.duwd.sub.job;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;
import top.duwd.common.domain.sub.entity.ZhihuQuestionBasic;
import top.duwd.common.service.proxy.ProxyService;
import top.duwd.sub.service.BaiduSearchResultService;
import top.duwd.sub.service.KeywordBaiduRealService;
import top.duwd.sub.service.SubQuestionDetailService;
import top.duwd.sub.service.ZhihuQuestionBasicService;

import java.net.Proxy;
import java.util.Date;
import java.util.List;

@ElasticJobConf(name = "ZhihuQuestionBasicJob", cron = "0 */1 * * * ?", shardingTotalCount = 1, description = "知乎问题快照")
@Slf4j
public class ZhihuQuestionBasicJob implements SimpleJob {
    @Autowired
    private ZhihuQuestionBasicService zhihuQuestionBasicService;
    @Autowired
    private SubQuestionDetailService subQuestionDetailService;
    @Autowired
    private BaiduSearchResultService baiduSearchResultService;
    @Autowired
    private KeywordBaiduRealService baiduRealJob;
    @Autowired
    private ProxyService proxyService;

    public static final String S = "/";
    public static final String SITE_ZHIHU = "知乎";

    @Override
    public void execute(ShardingContext shardingContext) {
        int shardingItem = shardingContext.getShardingItem();

        baiduRealJob.genRealUrl(SITE_ZHIHU); //更新real url
        run();//去重数据
        parse();
    }

    public void run() {

        //1，删除重复记录
        int i = zhihuQuestionBasicService.deleteRepeat();
        //2，获取全部信息, https://www.zhihu.com/question 开头
        List<KeywordBaiduSearchResult> baiduSearchResultList = zhihuQuestionBasicService.findListZhihuQuestionGenQid();

        if (baiduSearchResultList != null) {
            for (KeywordBaiduSearchResult searchResult : baiduSearchResultList) {
                //创建基本信息 记录， 后续解析
                //3.插入数据
                int save = zhihuQuestionBasicService.save(searchResult);
                if (save > 0) {
                    //4.修改 记录
                    searchResult.setResultOrder(1);
                    searchResult.setUpdateTime(new Date());
                    baiduSearchResultService.update(searchResult);
                }
            }
        }
    }

    public void parse() {
        List<ZhihuQuestionBasic> list = zhihuQuestionBasicService.findNoParse(100);
        if (list != null && list.size() > 0) {
            Proxy proxy = null;
            if (list.size() > 10) {
                //使用代理
                proxy = proxyService.getProxy(1);
            }

            for (ZhihuQuestionBasic basic : list) {
                int i = subQuestionDetailService.parseBasic(basic.getQid(),basic.getId(), proxy);
            }
        }
    }

}
