package top.duwd.sub.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.common.annotation.CurrentUser;
import top.duwd.common.domain.sub.entity.SubUser;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.sub.service.KeywordService;

@RestController
@Slf4j
@RequestMapping("/keyword")
public class KeywordController {

    @Autowired
    private KeywordService keywordService;
    @Autowired
    private ApiResultManager apm;

    /**
     *
     * @param json
     * {
     *     "keyword":"手机",
     *     "plat":["baidu","chinaZ"],
     *     "import":["华为手机","魅族手机"]
     * }
     * @return
     */
    @PostMapping(value = "/add")
    public ApiResult keywordAdd(@CurrentUser SubUser subUser, @RequestBody String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String keyword = jsonObject.getString("keyword");
        JSONArray platArray = jsonObject.getJSONArray("plat");
        JSONArray importArray = jsonObject.getJSONArray("import");
        keywordService.add(subUser,keyword,platArray,importArray);
    }
}
