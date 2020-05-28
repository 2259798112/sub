package top.duwd.sub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BaiduZhidaoService {

    @Autowired
    private BaiduSearchResultService baiduSearchResultService;

    public static final String prefix = "zhidao.baidu.com/question/";
    public static final String SITE_ZHIDAO = "百度知道";

    //对 百度结果 知乎 去重
    public int deleteRepeat() {
       return baiduSearchResultService.deleteRepeat(SITE_ZHIDAO);
    }


    public static void main(String[] args) {

        ArrayList<String> strings = new ArrayList<>();
        strings.add("https://www.zhihu.com/question/25338860");
        strings.add("https://www.zhihu.com/question/21324495?sort=created");
        strings.add("https://www.zhihu.com/question/353163620/answer/875384348");
        strings.add("https://zhuanlan.zhihu.com/p/89260253");

        for (String string : strings) {
            System.out.println(string);
            long qid = findQid(string, prefix);

            System.out.println(qid);
        }

    }

    private static long findQid(String content, String prefix) {
        if (StringUtils.isEmpty(content)) {
            return 0;
        }
        if (!content.contains(prefix)) {
            return 0;
        }


        //正则表达式，用于匹配非数字串，+号用于匹配出多个非数字串
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        boolean b = matcher.find();
        if (b) {
            String group = matcher.group(0);
            return Long.parseLong(group);
        } else {
            return 0;
        }
    }
}
