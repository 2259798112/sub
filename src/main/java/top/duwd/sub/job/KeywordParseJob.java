package top.duwd.sub.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.Keyword;
import top.duwd.common.domain.sub.entity.KeywordUser;
import top.duwd.common.exception.DuException;
import top.duwd.dutil.date.DateUtil;
import top.duwd.sub.service.KeywordService;
import top.duwd.sub.service.KeywordUserService;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

//@ElasticJobConf(name = "KeywordParseJob", cron = "0 */5 * * * ?", shardingTotalCount = 1, description = "关键词导入")
@Slf4j
public class KeywordParseJob implements SimpleJob {
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private KeywordUserService keywordUserService;

    public static final String S = "/";

    @Override
    public void execute(ShardingContext shardingContext) {
        run();
    }

    public void run() {
        //1,获取最近未处理的 user keyword
        List<KeywordUser> list = keywordUserService.findListToParse(DateUtil.addMin(new Date(), -60 * 24 * 3), new Date());
        if (list == null || list.size() == 0) {
            return;
        }

        for (KeywordUser keywordUser : list) {
            importKeywordCustom(keywordUser);
            int i = importKeywordPlat(keywordUser);
            if (i > 0){
                log.error("plat 扩展关键词异常");
            }else {
                keywordUserService.updateTime(keywordUser);
            }
        }

    }
    //平台拓展关键词
    private int importKeywordPlat(KeywordUser keywordUser) {
        int failCount = 0;
        String plat = keywordUser.getPlat();
        if (StringUtils.isEmpty(plat)) {

        } else {
            List<String> plats = JSONArray.parseArray(plat, String.class);
            for (String p : plats) {
                if (Const.KEYWORD_PLAT_Baidu.equalsIgnoreCase(p)) {
                    try {
                        importKeywordBaidu(keywordUser);
                    } catch (Exception e) {
                        if (e instanceof DuException) {
                            log.error(((DuException) e).getMsg());
                        } else {
                            e.printStackTrace();
                        }
                        failCount ++;
                    }
                }

                if (Const.KEYWORD_PLAT_ChinaZ.equalsIgnoreCase(p)) {
                    try {
                        importKeywordChinaZ(keywordUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                        failCount++;
                    }
                }
            }
        }
        return failCount;
    }

    private void importKeywordChinaZ(KeywordUser keywordUser) {
        //获取 百度相关词
        List<Keyword> moreFromChinaZ = keywordService.findKeywordFromChinaZ(keywordUser.getKeywordMain());

        if (moreFromChinaZ == null || moreFromChinaZ.size() == 0) {

        } else {
            HashSet<String> set = new HashSet<>();
            for (Keyword keyword : moreFromChinaZ) {
                set.add(keyword.getKeywordTail());
            }

            List<String> czKeyword = keywordService.filterImportList(keywordUser.getKeywordMain(), set, Const.KEYWORD_PLAT_ChinaZ);
            if (czKeyword == null || czKeyword.size() == 0){
                return;
            }

            int i = keywordService.importKeyword(keywordUser.getKeywordMain(), czKeyword, Const.KEYWORD_PLAT_ChinaZ);
            if (i > 0) {
                log.info("导入关键词[成功] [filterImportList={}]", JSON.toJSONString(czKeyword));
            } else {
                log.error("导入关键词[异常] [filterImportList={}]", JSON.toJSONString(czKeyword));
            }
        }
    }

    private void importKeywordBaidu(KeywordUser keywordUser) {
        //获取 百度相关词
        List<Keyword> keywordListFromBaidu = keywordService.findKeywordListFromBaidu(keywordUser.getKeywordMain());

        if (keywordListFromBaidu == null || keywordListFromBaidu.size() == 0) {

        } else {
            HashSet<String> pcSet = new HashSet<>();
            HashSet<String> moSet = new HashSet<>();
            for (Keyword keyword : keywordListFromBaidu) {
                if (Const.BaiduPC.equalsIgnoreCase(keyword.getPlat())) {
                    pcSet.add(keyword.getKeywordTail());
                } else {
                    moSet.add(keyword.getKeywordTail());
                }
            }

            if (pcSet.size() > 0) {
                List<String> pcKeyword = keywordService.filterImportList(keywordUser.getKeywordMain(), pcSet, Const.BaiduPC);
                int i = keywordService.importKeyword(keywordUser.getKeywordMain(), pcKeyword, Const.BaiduPC);
                if (i > 0) {
                    log.info("导入关键词[成功] [filterImportList={}]", JSON.toJSONString(pcKeyword));
                } else {
                    log.error("导入关键词[异常] [filterImportList={}]", JSON.toJSONString(pcKeyword));
                }
            }
            if (moSet.size() > 0) {
                List<String> moKeyword = keywordService.filterImportList(keywordUser.getKeywordMain(), pcSet, Const.BaiduMo);
                int i = keywordService.importKeyword(keywordUser.getKeywordMain(), moKeyword, Const.BaiduMo);
                if (i > 0) {
                    log.info("导入关键词[成功] [filterImportList={}]", JSON.toJSONString(moKeyword));
                } else {
                    log.error("导入关键词[异常] [filterImportList={}]", JSON.toJSONString(moKeyword));
                }
            }

        }
    }

    private void importKeywordCustom(KeywordUser keywordUser) {
        String importList = keywordUser.getImportList();
        if (StringUtils.isEmpty(importList)) {

        } else {
            String[] importWords = importList.split(S);
            HashSet<String> set = new HashSet<>(Arrays.asList(importWords));
            //过滤掉重复的
            List<String> filterImportList = keywordService.filterImportList(keywordUser.getKeywordMain(), set, Const.KEYWORD_PLAT_CUSTOM);
            if (filterImportList != null && filterImportList.size() > 0) {
                int i = keywordService.importKeyword(keywordUser.getKeywordMain(), filterImportList, Const.KEYWORD_PLAT_CUSTOM);
                if (i > 0) {
                    log.info("导入关键词成功[importList={}] [filterImportList={}]", importList, JSON.toJSONString(filterImportList));
                } else {
                    log.error("导入关键词异常[importList={}] [filterImportList={}]", importList, JSON.toJSONString(filterImportList));
                }
            }
        }
    }
}
