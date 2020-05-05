package top.duwd.sub.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.common.domain.sub.entity.SubQuestion;
import top.duwd.common.domain.sub.entity.SubQuestionDetail;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.sub.service.SubQuestionDetailService;
import top.duwd.sub.service.SubQuestionService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/q")
@Slf4j
public class SubQuestionController {

    @Resource
    private ApiResultManager apm;

    @Resource
    private SubQuestionService subQuestionService;
    @Resource
    private SubQuestionDetailService subQuestionDetailService;

    @GetMapping("/list")
    public ApiResult listByUserId(@RequestParam(value = "userId") String userId, @RequestParam(value = "type") Integer type, int pageNum, int pageSize) {
        PageInfo<SubQuestion> list = subQuestionService.listByUserId(userId, type, pageNum, pageSize);
        return apm.success(list);
    }

    @GetMapping("/one")
    public ApiResult questionDetails(@RequestParam(value = "questionId") Integer questionId, @RequestParam(value = "type") Integer type) {

        //type 0 hour 最近24, 1 day 最近7天
        List<SubQuestionDetail> list = subQuestionDetailService.questionDetails(questionId, type);
        return apm.success(list);
    }
}
