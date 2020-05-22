package top.duwd.sub.job;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import top.duwd.common.domain.sub.entity.Keyword;
import top.duwd.dutil.http.html.Baidu;
import top.duwd.dutil.http.html.dto.BaiduSearchResult;
import top.duwd.sub.service.BaiduSearchResultService;
import top.duwd.sub.service.KeywordService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ElasticJobConf(name = "KeywordBaiduJob", cron = "0 */10 * * * ?", shardingTotalCount = 1, description = "关键词Baidu")
@Slf4j
public class KeywordBaiduJob implements SimpleJob {
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private Baidu baidu;
    @Autowired
    private BaiduSearchResultService baiduSearchResultService;

    @Override
    public void execute(ShardingContext shardingContext) {
        run();
    }

    public void run() {
        HashMap<String, String> map = keywordService.getBaiduHeaderMap();

        //1,获取最近未处理的 user keyword
        int size = 100;
        int count = 3;
        List<Keyword> list = keywordService.findKeyworToBaiduSearch(count, size);
        if (list == null || list.size() == 0) {
            log.info("没有要百度搜素的关键词");
        } else {
            ArrayList<BaiduSearchResult> BaiduSearchResultList = new ArrayList<>();
            for (Keyword keyword : list) {
                BaiduSearchResult pcSearch = baidu.searchPC(map, keyword.getKeywordTail(), 1, null);
                if (pcSearch != null) {
                    BaiduSearchResultList.add(pcSearch);
                }
                BaiduSearchResult moSearch = baidu.searchM(map, keyword.getKeywordTail(), 1, null);
                if (moSearch != null) {
                    BaiduSearchResultList.add(moSearch);
                }
            }
            baiduSearchResultService.insertList(BaiduSearchResultList);
        }
    }
}
