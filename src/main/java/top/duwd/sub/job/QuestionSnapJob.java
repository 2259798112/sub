package top.duwd.sub.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.genid.GenId;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.SubQuestion;
import top.duwd.common.domain.sub.entity.SubQuestionDetail;
import top.duwd.common.service.proxy.ProxyService;
import top.duwd.dutil.date.DateUtil;
import top.duwd.sub.service.SubQuestionDetailService;
import top.duwd.sub.service.SubQuestionService;

import javax.persistence.criteria.CriteriaBuilder;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuestionSnapJob {
    @Autowired
    private SubQuestionService subQuestionService;
    @Autowired
    private SubQuestionDetailService subQuestionDetailService;
    @Autowired
    private ProxyService proxyService;

    private static final HashSet<Integer> dbSet = new HashSet<>();
    private static int PROXY_COUNT = 0;

    public static String SNAP_TIME = "";


    @Scheduled(cron = "0 55 * * * ?")
    public void run() {
        log.info("subQuestionDetailService.insertList");
        List<Integer> qidList = subQuestionService.findValidQuestion();
        if (qidList == null) {
            return;
        }

        if (StringUtils.isEmpty(SNAP_TIME)){
            setNextHourSnapTime();
        }

        List<SubQuestionDetail> details = genListFromQuestionIdList(qidList, new Date(), SNAP_TIME);
        subQuestionDetailService.insertList(details, 1000);
    }


    @Scheduled(cron = "0 10 * * * ?")
    public void snapTime() {
        setNextHourSnapTime();
    }

    public void setNextHourSnapTime(){
        log.info("setNextHourSnapTime");
        Date date = DateUtil.addMin(new Date(), 60);
        String snapTime = getSnapTime(date);
        SNAP_TIME = snapTime;
    }



    @Async("SubExecutor")
    @Scheduled(fixedRate = 200)
    public void snap() {
        String snapTime = getSnapTime(DateUtil.addMin(new Date(), 0));
        if (StringUtils.isEmpty(SNAP_TIME) || !snapTime.equalsIgnoreCase(SNAP_TIME)) {
            log.info("snapTime={} SNAP_TIME={}",snapTime,SNAP_TIME);
            return;
        }

        List<SubQuestionDetail> lastN = subQuestionDetailService.findLastN(snapTime, 20);
        if (lastN == null || lastN.size() == 0) {
            log.info("lastN={}",JSON.toJSONString(lastN));
            dbSet.clear();
            return;
        }

        Set<Integer> dbQidSet = lastN.stream().map(SubQuestionDetail::getQuestionId).collect(Collectors.toSet());
        ArrayList<Integer> lastNN = new ArrayList<>(dbQidSet);
        for (Integer integer : dbQidSet) {
            if (dbSet.contains(integer)) {
                lastNN.remove(integer);
            } else {
                dbSet.add(integer);
            }
        }


        for (Integer qid : lastNN) {
            Proxy proxy = proxyService.getProxy(1);
            int parse = subQuestionDetailService.parse(qid, proxy);
            if (parse > 0) {
                log.info("成功解析 qid={}", qid);
            } else {
                if (parse == Const.PROXY_INVALID) {
                    if (PROXY_COUNT > 10) {
                        proxyService.getProxyAndSaveDB();
                        PROXY_COUNT = 0;
                    } else {
                        PROXY_COUNT++;
                    }
                }
                dbSet.remove(qid);
            }
        }
    }

    private String getSnapTime(Date date) {
        return JSON.toJSONString(date, SerializerFeature.WriteDateUseDateFormat).replace(" ", "-").substring(1, 14);
    }


    private List<SubQuestionDetail> genListFromQuestionIdList(List<Integer> qidList, Date date, String snapTime) {
        List<SubQuestionDetail> list = new ArrayList<>(qidList.size());
        for (Integer qid : qidList) {
            SubQuestionDetail subQuestionDetail = new SubQuestionDetail();
            subQuestionDetail.setQuestionId(qid);
            subQuestionDetail.setCreateTime(date);
            subQuestionDetail.setUpdateTime(date);
            subQuestionDetail.setSnapTime(snapTime);
            list.add(subQuestionDetail);
        }
        return list;
    }
}
