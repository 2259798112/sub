package top.duwd.sub.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    private SubUserService subUserService;

    /**
     * 用户登录 根据手机号+密码
     * @return
     */
    @PostMapping("/login")
    public ApiResult login(@RequestBody SubUser subUser){
        String token = subUserService.login(subUser.getTel(), subUser.getPassword());
        return apm.success(token);
    }


    /**
     * 注册用户  根据手机号+密码
     * @return
     */
    @PostMapping("/login/reg")
    public ApiResult reg(@RequestParam String tel,@RequestParam String pwd){
        String token = subUserService.reg(tel, pwd);
        return apm.success(token);
    }

}
