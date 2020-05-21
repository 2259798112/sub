package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.Keyword;
import top.duwd.common.domain.sub.entity.KeywordUser;
import top.duwd.common.domain.sub.entity.SubUser;
import top.duwd.common.exception.DuExceptionManager;
import top.duwd.common.exception.ErrorCodes;
import top.duwd.common.mapper.sub.KeywordMapper;
import top.duwd.common.mapper.sub.KeywordUserMapper;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.http.html.Baidu;
import top.duwd.dutil.http.html.ChinaZ;
import top.duwd.dutil.http.html.dto.BaiduSearchResult;

import java.util.*;

@Service
@Slf4j
public class KeywordUserService implements IBaseService<KeywordUser> {

    @Autowired
    private KeywordUserMapper keywordUserMapper;
    @Autowired
    private DuExceptionManager em;

    public int add(SubUser subUser, String keyword, String platArray, String importArray) {
        if (!StringUtils.isEmpty(keyword)) {
            //先保留原始数据

            KeywordUser keywordUser = new KeywordUser();
            keywordUser.setKeywordMain(keyword);
            keywordUser.setUserId(subUser.getUserId());
            if (platArray != null) {
                keywordUser.setPlat(platArray);
            }
            if (importArray != null) {
                keywordUser.setImportList(importArray);
            }
            Date date = new Date();
            keywordUser.setCreateTime(date);
            keywordUser.setUpdateTime(date);
            return save(keywordUser);
        }
        throw em.create(ErrorCodes.KEYWORD_ADD_ERROR);
    }

    @Transactional
    public int save(KeywordUser keywordUser) {
        return keywordUserMapper.insert(keywordUser);
    }

    @Override
    public List<KeywordUser> findListByKV(String k, Object v) {
        return null;
    }

    @Override
    public List<KeywordUser> findListByMap(Map<String, Object> map) {
        return null;
    }

    /**
     * 获取最近一段时间待解析的数据
     *
     * @return
     */

    public List<KeywordUser> findListToParse(Date start, Date end) {
        return keywordUserMapper.findListToParse(start, end);
    }

    @Transactional
    public int update(KeywordUser keywordUser){
        keywordUser.setUpdateTime(new Date());
        return keywordUserMapper.updateByPrimaryKey(keywordUser);
    }


}
