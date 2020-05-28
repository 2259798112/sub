package top.duwd.sub.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zhihu")
public class ZhihuTop {

    @PostMapping("/top")
    public void top(@RequestBody ZhihuTop zhihuTop){

    }
}
