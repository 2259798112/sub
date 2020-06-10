package top.duwd.sub.job;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.duwd.common.domain.sub.entity.Keyword;
import top.duwd.dutil.file.FileUtil;
import top.duwd.sub.service.KeywordService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class OneJob {
    @Autowired
    private KeywordService keywordService;

    public static final String dir = "/Users/dwd/Downloads/temp";

    public static final ArrayList<String> FilterList = new ArrayList<String>();

    static {
        FilterList.add("衣");
        FilterList.add("鞋");
        FilterList.add("帽");
        FilterList.add("袍");
        FilterList.add("裤");
        FilterList.add("衫");
    }

    @Scheduled(fixedDelay = 1000* 60 * 60 * 24 * 7)
    public void run() {
        HashSet<String> set = new HashSet<>();

        String[] strings = FileUtil.readFilenameFromFolder(dir, false);
        for (String fileName : strings) {
            System.out.println(fileName);
            String content = FileUtil.readTextFile(fileName);
            if (content != null) {
                List<String> keywords = JSONArray.parseArray(content, String.class);
                HashSet<String> kSet = new HashSet<>(keywords);
                set.addAll(kSet);
            }
        }

        ArrayList<String> list = new ArrayList<>(set);
        Date date = new Date();

        ArrayList<Keyword> keywords = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String keyword = list.get(i);
            Keyword entity = new Keyword();
            //72104	朱指导	高等数学学习指导朱明星	ChinaZ	2020-06-08 16:26:25	2020-06-08 16:28:37	3	3
            entity.setPlat("JD");
            entity.setKeywordMain("JD");
            entity.setCreateTime(date);
            entity.setUpdateTime(date);
            entity.setKeywordTail(keyword);
            boolean flag = false;
            for (String key : FilterList) {
                if (keyword.contains(key)) {
                    log.info("含有过滤此 {}",keyword);
                    flag = true;
                    break;
                }
            }

            if (flag) {
                entity.setCounter(10);
                entity.setCounterM(10);
            } else {
                entity.setCounter(0);
                entity.setCounterM(0);
            }

            keywords.add(entity);

            if (keywords.size() == 100) {
                log.info("insert keyword 100");
                try {
                    keywordService.insertList(keywords);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                keywords.clear();
            }
        }
    }

}
