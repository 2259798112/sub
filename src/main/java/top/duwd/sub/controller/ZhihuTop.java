package top.duwd.sub.controller;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.sub.service.ZhihuHotService;

import java.util.List;

@RestController
@RequestMapping("/zhihu")
public class ZhihuTop {

    @Autowired
    private ZhihuHotService zhihuHotService;
    @Autowired
    private ApiResultManager apm;

    @PostMapping("/top")
    public void top(@RequestBody ZhihuTop zhihuTop){

    }


    @PostMapping("/hot")
    public ApiResult top(@RequestBody String json){
        List<Integer> list = JSONArray.parseArray(json, Integer.class);
        int hot = zhihuHotService.savaList(list, "hot");
        return apm.success(hot);
    }

}
