package top.duwd.sub.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.common.annotation.CurrentUser;
import top.duwd.common.annotation.Ignore;
import top.duwd.common.domain.sub.entity.SubUser;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.sub.service.SubQuestionDetailService;
import top.duwd.sub.service.SubQuestionService;
import top.duwd.sub.service.SubUserService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@Slf4j
public class SubUserController {
    @Resource
    private ApiResultManager apm;
    @Resource
    private SubUserService userService;

    @GetMapping("/login")
    public ApiResult login(){

        return apm.success("login");
    }


    @GetMapping("/ignore")
    public ApiResult ignore(){

        return apm.success("ignore");
    }

    @GetMapping("/normal")
    public ApiResult normal(@CurrentUser SubUser user){

        return apm.success(user);
    }


}
