package top.duwd.sub.job;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.Keyword;
import top.duwd.common.domain.sub.entity.KeywordUser;
import top.duwd.dutil.date.DateUtil;
import top.duwd.sub.service.KeywordService;
import top.duwd.sub.service.KeywordUserService;

import java.util.*;

@ElasticJobConf(name = "ElasticQuestionPrepareJob", cron = "0 */5 * * * ?", shardingTotalCount = 1, description = "准备数据")
@Slf4j
public class KeywordParseJob implements SimpleJob {
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private KeywordUserService keywordUserService;

    public static final String S = "/";

    @Override
    public void execute(ShardingContext shardingContext) {
        //解析最近一小时 update=create 的数据
        List<KeywordUser> list = keywordUserService.findListToParse(DateUtil.addMin(new Date(), -10), new Date());
        if (list != null && list.size() > 0) {
            HashMap<String, Set<String>> importMap = new HashMap<>();
            HashMap<String, Set<String>> platMap = new HashMap<>();

            for (KeywordUser keywordUser : list) {

                String importList = keywordUser.getImportList();
                if (!StringUtils.isEmpty(importList)) {
                    String[] words = importList.split(S);
                    HashSet<String> set = new HashSet<>(Arrays.asList(words));
                    importMap.put(keywordUser.getKeywordMain(), set);
                }

                String plat = keywordUser.getPlat();
                if (!StringUtils.isEmpty(plat)) {
                    String[] plats = plat.split(S);
                    HashSet<String> set = new HashSet<>(Arrays.asList(plats));
                    platMap.put(keywordUser.getKeywordMain(), set);
                }
            }

            ArrayList<Keyword> keywords = new ArrayList<>();
            if (importMap.size() > 0) {
                importMap.forEach((key, value) -> {
                    List<Keyword> k = keywordService.importKeyword(key, new ArrayList<>(value));
                    if (k != null) {
                        keywords.addAll(k);
                    }
                });
            }

            if (platMap.size() > 0) {
                platMap.forEach((key, value) -> {
                    if (value != null && value.size() > 0) {
                        for (String plat : value) {

                            if (Const.KEYWORD_PLAT_Baidu.equalsIgnoreCase(plat)) {
                                List<Keyword> keywordListFromBaidu = keywordService.findKeywordListFromBaidu(key);
                                if (keywordListFromBaidu != null && keywordListFromBaidu.size() > 0) {
                                    keywords.addAll(keywordListFromBaidu);
                                }
                            }

                            if (Const.KEYWORD_PLAT_ChinaZ.equalsIgnoreCase(plat)) {
                                List<Keyword> moreFromChinaZ = keywordService.findMoreFromChinaZ(key);
                                if (moreFromChinaZ != null && moreFromChinaZ.size() > 0) {
                                    keywords.addAll(moreFromChinaZ);
                                }
                            }
                        }
                    }
                });
            }


        }

    }
}
