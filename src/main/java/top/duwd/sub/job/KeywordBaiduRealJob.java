package top.duwd.sub.job;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;
import top.duwd.dutil.http.RequestBuilderCustom;
import top.duwd.sub.service.BaiduSearchResultService;

import java.util.List;

@ElasticJobConf(name = "KeywordBaiduRealJob", cron = "*/20 * * * * ?", shardingTotalCount = 1, description = "关键词Baidu真实链接")
@Slf4j
public class KeywordBaiduRealJob implements SimpleJob {
    @Autowired
    private BaiduSearchResultService baiduSearchResultService;

    @Override
    public void execute(ShardingContext shardingContext) {
        run();
    }

    /**
     * 1， 获取 keyword 表中 count < 3 的待处理关键词
     * 2，获取百度PC 百度Mobile 所需header cookie
     * 3，取 百度PC 百度Mobile 获取具体搜索结果
     */
    public void run() {
        String prefix= "http://www.baidu.com";
        String site= "知乎";
        checkReal(prefix);

        List<KeywordBaiduSearchResult> list = baiduSearchResultService.findListNoUrlReal(site, 10);
        if (list == null || list.size() == 0) {
            log.info("没有要获取真实链接的关键词");
        } else {
            for (KeywordBaiduSearchResult item : list) {
                String urlSource = item.getUrlSource();
                if (!StringUtils.isEmpty(urlSource)) {
                    if (urlSource.startsWith(prefix)) {
                        String location = RequestBuilderCustom.get302Location(item.getUrlSource());
                        if (StringUtils.isEmpty(location)) {
                            log.info("获取[url={}] 真实链接异常,设置为相同链接", urlSource);
                            item.setUrlReal(item.getUrlSource());
                        } else {
                            log.info("获取[location={}] ", location);
                            item.setUrlReal(location);
                        }
                    } else {
                        item.setUrlReal(item.getUrlSource());
                    }
                }
                baiduSearchResultService.updateUrlRead(item);
            }
        }
    }

    public void checkReal(String prefix) {
        List<KeywordBaiduSearchResult> list = baiduSearchResultService.findListNoUrlRealWithPrefix(prefix, 100);
        if (list == null || list.size() == 0) {
            log.info("没有要直接修改链接的关键词");
        } else {
            for (KeywordBaiduSearchResult item : list) {
                item.setUrlReal(item.getUrlSource());
                baiduSearchResultService.updateUrlRead(item);
            }
        }
    }
}
