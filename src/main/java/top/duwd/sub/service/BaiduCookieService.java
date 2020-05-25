package top.duwd.sub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.domain.sub.entity.BaiduCookie;
import top.duwd.common.mapper.sub.BaiduCookieMapper;
import top.duwd.dutil.date.DateUtil;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BaiduCookieService {
    @Autowired
    private BaiduCookieMapper baiduCookieMapper;

    public BaiduCookie findListInDays(int day,String plat) {
        Example example = new Example(BaiduCookie.class);
        example.createCriteria().andEqualTo("plat",plat)
                .andBetween("createTime", DateUtil.addMin(new Date(), -60 * 24 * day), new Date());
        example.orderBy("id").desc();
        List<BaiduCookie> baiduCookies = baiduCookieMapper.selectByExample(example);
        if (baiduCookies == null || baiduCookies.size() == 0) {
            return null;
        } else {
            return baiduCookies.get(0);
        }
    }

    @Transactional
    public int save(BaiduCookie en){
        return baiduCookieMapper.insert(en);
    }

}
