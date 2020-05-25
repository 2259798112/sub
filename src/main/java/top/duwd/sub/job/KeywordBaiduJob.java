package top.duwd.sub.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.Keyword;
import top.duwd.dutil.http.html.Baidu;
import top.duwd.dutil.http.html.dto.BaiduSearchResult;
import top.duwd.sub.service.BaiduSearchResultService;
import top.duwd.sub.service.KeywordService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ElasticJobConf(name = "KeywordBaiduJob", cron = "0 */1 * * * ?", shardingTotalCount = 1, description = "关键词Baidu")
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

    /**
     *1， 获取 keyword 表中 count < 3 的待处理关键词
     * 2，获取百度PC 百度Mobile 所需header cookie
     * 3，取 百度PC 百度Mobile 获取具体搜索结果
     */
    public void run() {

        boolean wait = true;
        //1,获取最近未处理的 user keyword
        int size = 3;
        int count = 3;
        List<Keyword> list = keywordService.findKeyworToBaiduSearch(count, size);
        if (list == null || list.size() == 0) {
            log.info("没有要百度搜素的关键词");
        } else {
            ArrayList<BaiduSearchResult> BaiduSearchResultList = new ArrayList<>();


            Map<String, String> pcMap = keywordService.getBaiduHeaderMap(Const.BaiduPC);
            Map<String, String> moMap = keywordService.getBaiduHeaderMap(Const.BaiduMo);

            for (Keyword keyword : list) {
                int countPC = 0;
                int countMobile = 0;

                if (keyword.getCounter()<3){

                    BaiduSearchResult pcSearch = baidu.searchPC(pcMap, keyword.getKeywordTail(), 1, null,wait);
                    log.info("baidu PC search [BaiduSearchResult={}]", JSON.toJSONString(pcSearch,SerializerFeature.PrettyFormat));
                    if (pcSearch != null) {
                        BaiduSearchResultList.add(pcSearch);
                        countPC = 3;
                    }else {
                        countPC++;
                    }
                    keyword.setCounter(countPC);
                }

                if (keyword.getCounterM()<3){
                    BaiduSearchResult moSearch = baidu.searchM(moMap, keyword.getKeywordTail(), 1, null,wait);
                    log.info("baidu mobile search [BaiduSearchResult={}]", JSON.toJSONString(moSearch, SerializerFeature.PrettyFormat));
                    if (moSearch != null) {
                        //重定向
                        BaiduSearchResultList.add(moSearch);
                        countMobile = 3;
                    }else {
                        countMobile ++;
                    }
                    keyword.setCounterM(countMobile);
                }

                keywordService.updateByPrimaryKey(keyword);
            }
            baiduSearchResultService.insertList(BaiduSearchResultList);

        }
    }
}
