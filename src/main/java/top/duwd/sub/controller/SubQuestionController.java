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

    // /q/list?

    /**
     * 获取用户订阅 话题列表 list
     * /q/list
     *
     * int questionType
     * int pageNum
     * int pageSize
     *
     * sort order by id asc
     *
     * header token
     */


    /**
     * 获取用户订阅 话题id chart
     * /q/chart
     *
     * int qid
     * String type hour日 day天
     * Date start
     * Date end
     *
     * header token
     */


    /**
     * 获取用户订阅 话题id details
     * /q/details
     *
     * int qid
     * int pageNum
     * int pageSize
     *
     * header token
     */


    /**
     * 订阅
     * /q/sub
     * qid=123456 int
     *
     * header token
     */

    /**
     * 取消订阅
     * /q/unsub
     * qid=123456 int
     *
     * header token
     */

}
