package top.duwd.common.exception;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionDetails {

    public static final Map<Integer, String> map = new HashMap<>();

    public static final int SYSTEM_ERROR = 500;
    public static final String SYSTEM_ERROR_VALUE = "系统异常";

    public static final int USER_EMPTY = 6001;
    public static final String USER_EMPTY_VALUE = "用户不能为空或NULL";

    public static final int USER_TYPE_LIMIT = 60001;
    public static final String USER_TYPE_LIMIT_VALUE = "账户受限，请升级账户";

    public static final int SUB_QUESTION_DUPLICATE = 70001;
    public static final String SUB_QUESTION_DUPLICATE_VALUE = "问题重复订阅，请检查";

    public static final int SUB_QUESTION_NOT_IN_DB = 70002;
    public static final String SUB_QUESTION_NOT_IN_DB_VALUE = "问题不存在";


    static {
        map.put(SYSTEM_ERROR, SYSTEM_ERROR_VALUE);
        map.put(USER_EMPTY, USER_EMPTY_VALUE);
        map.put(USER_TYPE_LIMIT, USER_TYPE_LIMIT_VALUE);
        map.put(SUB_QUESTION_DUPLICATE, SUB_QUESTION_DUPLICATE_VALUE);
        map.put(SUB_QUESTION_NOT_IN_DB, SUB_QUESTION_NOT_IN_DB_VALUE);
    }

}
