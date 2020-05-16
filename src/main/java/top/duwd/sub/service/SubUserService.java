package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import top.duwd.common.config.Const;
import top.duwd.common.domain.sub.entity.SubUser;
import top.duwd.common.domain.sub.entity.UserSession;
import top.duwd.common.exception.DuExceptionManager;
import top.duwd.common.exception.ErrorCodes;
import top.duwd.common.mapper.sub.SubUserMapper;
import top.duwd.common.mapper.sub.UserSessionMapper;
import top.duwd.dutil.date.DateUtil;

import java.util.*;

@Service
@Slf4j
public class SubUserService implements IBaseService<SubUser> {

    @Autowired
    private SubUserMapper subUserMapper;
    @Autowired
    private UserSessionMapper userSessionMapper;
    @Autowired
    private DuExceptionManager em;


    /**
     * 根据手机号+密码 注册用户
     *
     * @param tel
     * @param pwd
     * @return
     */
    public String reg(String tel, String pwd) {
        SubUser byTel = subUserMapper.findByTel(tel);
        if (byTel != null) {
            log.info("手机号[tel={}]已经被使用", pwd);
            throw em.create(ErrorCodes.USER_TEL_ALREADY_IN_DB);
        }

        String uid = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();

        SubUser subUser = new SubUser();
        subUser.setTel(tel);
        subUser.setUserId(uid);
        subUser.setPassword(genPassword(pwd));
        subUser.setType(Const.USER_TYPE_FREE);
        int i = saveUser(subUser);


        log.info("用户注册成功[user={}]", JSON.toJSONString(subUser));
        String token = genToken(uid);
        log.info("返回用户成功[token={}]", token);
        return token;
    }


    /**
     * 根据手机号登录
     *
     * @param tel
     * @param pwd
     * @return
     */
    @Transactional
    public String login(String tel, String pwd) {
        SubUser dbUser = subUserMapper.findByTel(tel);
        if (dbUser == null) {
            log.error("根据[tel={}]找不到用户", tel);
            throw em.create(ErrorCodes.USER_LOGIN_NO_UID);
        }

        if (!dbUser.getPassword().equalsIgnoreCase(genPassword(pwd))) {
            log.error("[tel={}]用户,密码不正确[pwd={}]", tel, pwd);
            throw em.create(ErrorCodes.USER_LOGIN_NO_UID);
        }

        return genToken(dbUser.getUserId());
    }

    /**
     * 根据 uid 生产用户token
     * 并将token 入库
     *
     * @param uid
     * @return
     */
    public String genToken(String uid) {
        //根据 user 生成 token
        String s = uid + JSON.toJSONString(new Date()) + new Random().nextInt();
        String token = DigestUtils.md5DigestAsHex(s.getBytes());

        int i = saveUserSession(uid, token, Const.LOGIN_PLAT_DEFAULT);
        if (i == 1) {
            return token;
        } else {
            throw em.create(ErrorCodes.USER_LOGIN_ERROR);
        }
    }

    /**
     * 通过 token 获取用户信息
     *
     * @param token
     * @return
     */
    public SubUser getUserByToken(String token) {

        UserSession userSession = userSessionMapper.selectByPrimaryKey(token);

        if (userSession == null) {
            log.error("用户[token={}]不存在", token);
            throw em.create(ErrorCodes.USER_LOGIN_ERROR);
        }

        Date now = new Date();
        if (now.before(DateUtil.addMin(now, 30 * 1440))) {
            //session 有效期
            String uid = userSession.getUid();
            SubUser subUser = subUserMapper.selectByPrimaryKey(uid);
            return subUser;

        } else {
            log.info("用户[token={}]过期,请重新登录", JSON.toJSONString(userSession));
            throw em.create(ErrorCodes.TOKEN_OUT_DATE);
        }
    }

    @Transactional
    public int saveUser(SubUser user) {
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);

        log.info("创建用户[user={}]", JSON.toJSONString(user));
        int i = subUserMapper.insert(user);
        return i;
    }

    String genPassword(String pwd) {
        if (StringUtils.isEmpty(pwd)) {
            throw em.create(ErrorCodes.USER_PASSWORD_LENGTH_TOO_SHORT);
        }
        if (StringUtils.containsWhitespace(pwd)) {
            throw em.create(ErrorCodes.USER_PASSWORD_HAS_SPACE);
        }

        return DigestUtils.md5DigestAsHex(pwd.getBytes()).toLowerCase();
    }


    @Transactional
    public int saveUserSession(String uid, String token, int plat) {
        UserSession userSession = new UserSession();
        userSession.setToken(token);
        userSession.setUid(uid);
        userSession.setPlat(plat);
        Date date = new Date();
        userSession.setCreateTime(date);
        userSession.setUpdateTime(date);
        return userSessionMapper.insert(userSession);
    }


    @Override
    public List<SubUser> findListByKV(String k, Object v) {
        return null;
    }

    @Override
    public List<SubUser> findListByMap(Map<String, Object> map) {
        return null;
    }
}
