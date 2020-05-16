package top.duwd.common.exception;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorCodes {

    public static final Map<Integer, String> map = new HashMap<>();

    public static final int SYSTEM_ERROR = 500;
    public static final String SYSTEM_ERROR_VALUE = "系统异常";

    public static final int USER_EMPTY = 6001;
    public static final String USER_EMPTY_VALUE = "用户不能为空或NULL";

    public static final int USER_TYPE_LIMIT = 60001;
    public static final String USER_TYPE_LIMIT_VALUE = "账户受限，请升级账户";

    public static final int USER_LOGIN_ERROR = 60002;
    public static final String USER_LOGIN_ERROR_VALUE = "用户登录失败";

    public static final int USER_LOGIN_NO_UID = 60003;
    public static final String USER_LOGIN_NO_UID_VALUE = "用户名或密码不正确";

    public static final int TOKEN_OUT_DATE = 60004;
    public static final String TOKEN_OUT_DATE_VALUE = "登录过期，请重新登陆";

    public static final int USER_TEL_ALREADY_IN_DB = 60005;
    public static final String USER_TEL_ALREADY_IN_DB_VALUE = "用户手机号已经存在";


    public static final int USER_PASSWORD_LENGTH_TOO_SHORT = 60006;
    public static final String USER_PASSWORD_LENGTH_TOO_SHORT_VALUE = "密码长度小于6位";

    public static final int USER_PASSWORD_HAS_SPACE = 60007;
    public static final String USER_PASSWORD_HAS_SPACE_VALUE = "密码不能包含空格";


    public static final int SUB_QUESTION_DUPLICATE = 70001;
    public static final String SUB_QUESTION_DUPLICATE_VALUE = "问题重复订阅，请检查";

    public static final int SUB_QUESTION_NOT_IN_DB = 70002;
    public static final String SUB_QUESTION_NOT_IN_DB_VALUE = "问题不存在";
    public static final int SUB_QUESTION_IS_NOT_A_QUESTION = 70003;
    public static final String SUB_QUESTION_IS_NOT_A_QUESTION_VALUE = "订阅的问题不存在";


    static {
        map.put(SYSTEM_ERROR, SYSTEM_ERROR_VALUE);
        map.put(USER_EMPTY, USER_EMPTY_VALUE);
        map.put(USER_TYPE_LIMIT, USER_TYPE_LIMIT_VALUE);
        map.put(SUB_QUESTION_DUPLICATE, SUB_QUESTION_DUPLICATE_VALUE);
        map.put(SUB_QUESTION_NOT_IN_DB, SUB_QUESTION_NOT_IN_DB_VALUE);
        map.put(USER_LOGIN_ERROR, USER_LOGIN_ERROR_VALUE);
        map.put(USER_LOGIN_NO_UID, USER_LOGIN_NO_UID_VALUE);
        map.put(TOKEN_OUT_DATE, TOKEN_OUT_DATE_VALUE);
        map.put(USER_TEL_ALREADY_IN_DB, USER_TEL_ALREADY_IN_DB_VALUE);
        map.put(USER_PASSWORD_LENGTH_TOO_SHORT, USER_PASSWORD_LENGTH_TOO_SHORT_VALUE);
        map.put(USER_PASSWORD_HAS_SPACE, USER_PASSWORD_HAS_SPACE_VALUE);
        map.put(SUB_QUESTION_IS_NOT_A_QUESTION, SUB_QUESTION_IS_NOT_A_QUESTION_VALUE);
    }

}
