package top.duwd.sub.controller;

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
import top.duwd.sub.service.KeywordUserService;

@RestController
@Slf4j
@RequestMapping("/keyword")
public class KeywordController {

    @Autowired
    private KeywordUserService keywordUserService;
    @Autowired
    private ApiResultManager apm;

    /**
     *
     * @param json
     * {
     *     "keyword":"手机",
     *     "plat":"baidu/chinaZ",
     *     "import":"华为手机/魅族手机"
     * }
     * @return
     */
    @PostMapping(value = "/add")
    public ApiResult keywordAdd(@CurrentUser SubUser subUser, @RequestBody String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String keyword = jsonObject.getString("keyword");
        String platArray = jsonObject.getJSONArray("platArray").toJSONString();
        String importArray = jsonObject.getString("importArray");
        int add = keywordUserService.add(subUser, keyword, platArray, importArray);
        return apm.success(add);
    }
}
