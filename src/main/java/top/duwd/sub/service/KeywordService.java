package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.Keyword;
import top.duwd.common.mapper.sub.KeywordMapper;
import top.duwd.dutil.http.html.ChinaZ;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KeywordService implements IBaseService<Keyword> {

    @Autowired
    private ChinaZ chinaZ;

    @Autowired
    private KeywordMapper keywordMapper;

    public List<Keyword> findMoreFromChinaZ(String keyword) {

        List<Keyword> dbList = findListByKV("keywordMain", keyword);
        if (dbList != null || dbList.size() > 0) {
            log.info("[keyword={}]处理过，Pass", keyword);
            return null;
        }

        List<String> keywords = chinaZ.keyword(keyword, 1, 5);
        if (keywords == null || keywords.size() == 0) {
            return null;
        }

        ArrayList<Keyword> list = new ArrayList<>(keywords.size());
        Date date = new Date();
        for (String word : keywords) {
            Keyword entity = new Keyword();
            entity.setKeywordMain(keyword);
            entity.setKeywordTail(word);
            entity.setPlat(Const.ChinaZ);
            entity.setCreateTime(date);
            entity.setUpdateTime(date);
            entity.setCounter(0);
            entity.setCounterM(0);
            list.add(entity);
        }
        return list;
    }

    int insertList(List<Keyword> list) {
        log.info("批量保存 keyword [list={}]", JSON.toJSONString(list));
        int count = 0;
        if (list == null || list.size() == 0) {
        } else {
            count = keywordMapper.insertList(list);
        }
        log.info("批量保存 [count={}]", count);
        return count;
    }

    @Async
    public void findMoreKeyword(String keyword, String plat) {
        switch (plat) {
            case Const.ChinaZ:
                insertList(findMoreFromChinaZ(keyword));
                break;
            default:
                insertList(findMoreFromChinaZ(keyword));
                break;
        }
    }


    @Override
    public List<Keyword> findListByKV(String k, Object v) {
        Example example = new Example(Keyword.class);
        example.createCriteria().andEqualTo(k, v);
        return keywordMapper.selectByExample(example);
    }

    @Override
    public List<Keyword> findListByMap(Map<String, Object> map) {
        Example example = new Example(Keyword.class);
        Example.Criteria criteria = example.createCriteria();
        map.forEach((key, value) -> criteria.andEqualTo(key, value));
        return keywordMapper.selectByExample(example);
    }
}
