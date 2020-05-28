package top.duwd.sub.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;
import top.duwd.dutil.http.RequestBuilderCustom;
import top.duwd.sub.service.BaiduSearchResultService;

import java.util.List;

@Slf4j
@Component
public class KeywordBaiduRealJob {
    @Autowired
    private BaiduSearchResultService baiduSearchResultService;
    private static final String prefix_baidu = "http://www.baidu.com";

    /**
     * 1， 获取 keyword 表中 count < 3 的待处理关键词
     * 2，获取百度PC 百度Mobile 所需header cookie
     * 3，取 百度PC 百度Mobile 获取具体搜索结果
     */
    public void genRealUrl(String site) {
        checkReal();

        List<KeywordBaiduSearchResult> list = baiduSearchResultService.findListNoUrlReal(site, 20);
        if (list == null || list.size() == 0) {
            log.info("没有要获取真实链接的关键词");
        } else {
            for (KeywordBaiduSearchResult item : list) {
                String urlSource = item.getUrlSource();
                if (!StringUtils.isEmpty(urlSource)) {
                    if (urlSource.startsWith(prefix_baidu)) {
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

    private void checkReal() {
        List<KeywordBaiduSearchResult> list = baiduSearchResultService.findListNoUrlRealWithPrefix(prefix_baidu, 100);
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
