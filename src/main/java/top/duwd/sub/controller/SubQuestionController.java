package top.duwd.sub.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.common.annotation.CurrentUser;
import top.duwd.common.domain.sub.entity.SubQuestion;
import top.duwd.common.domain.sub.entity.SubQuestionDetail;
import top.duwd.common.domain.sub.entity.SubUser;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.sub.service.SubQuestionDetailService;
import top.duwd.sub.service.SubQuestionService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
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

    /**
     * 获取用户订阅 话题列表 list
     * /q/list
     * <p>
     * int questionType
     * int pageNum
     * int pageSize
     * <p>
     * sort=id asc
     * <p>
     * header token
     */
    @GetMapping("/sub/list")
    public ApiResult qList(@CurrentUser SubUser subUser, @RequestParam int questionType,
                           @RequestParam int pageNum, @RequestParam int pageSize,
                           @RequestParam String sort, @RequestParam int direct) {

        PageInfo<SubQuestion> list = subQuestionService.listByUserId(subUser.getUserId(), questionType, pageNum, pageSize, sort, direct);
        return apm.success(list);
    }


    /**
     * 添加问题订阅
     *
     * @param subUser
     * @param qid
     * @return 1:成功订阅
     */
    @GetMapping("/sub/add")
    public ApiResult subAdd(@CurrentUser SubUser subUser, @RequestParam int qid) {

        int save = subQuestionService.subAdd(subUser, qid);
        return apm.success(save);
    }

    /**
     * 取消问题订阅
     *
     * @param subUser
     * @param qid
     * @return 1:成功取消订阅
     */
    @GetMapping("/sub/del")
    public ApiResult subDel(@CurrentUser SubUser subUser, @RequestParam int qid) {
        int save = subQuestionService.subDel(subUser, qid);
        return apm.success(save);
    }


    /**
     * 获取用户订阅 话题id chart
     * /q/chart
     * <p>
     * int qid
     * int type
     * TYPE_HOUR = 1;
     * TYPE_DAY = 2;
     * Date start
     * Date end
     * <p>
     * header token
     */

//    @GetMapping("/chart")
    public ApiResult qChart(@RequestParam int qid, @RequestParam String type, @RequestParam Date start, @RequestParam Date end) {
        subQuestionDetailService.chart(qid, type, start, end);
        return apm.success();
    }

    @GetMapping("/chart/default")
    public ApiResult qChart(@RequestParam int qid, @RequestParam int type) {
        List<List<Object>> dataSet = subQuestionDetailService.chartDefault(qid, type);
        return apm.success(dataSet);
    }

    /**
     * 获取用户订阅 话题id details
     * /q/details
     * <p>
     * int qid
     * int pageNum
     * int pageSize
     * <p>
     * header token
     */

    @GetMapping("/details")
    public ApiResult qDetails(@RequestParam int qid,
                            @RequestParam int pageNum, @RequestParam int pageSize,
                            @RequestParam String sort, @RequestParam int direct) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("questionId", qid);
        PageInfo<SubQuestionDetail> list = subQuestionDetailService.findListByMapPage(map, pageNum, pageSize, sort, direct);
        return apm.success(list);
    }

}
