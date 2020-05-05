package top.duwd.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    private ApiResultManager api;

    @ExceptionHandler(Exception.class)
    public ApiResult handlerException(Exception e) {

        ApiResult apiResult = null;
        //如果是自定义的异常，返回对应的错误信息
        if (e instanceof DuException) {
            DuException ex = (DuException) e;
            apiResult = api.fail(null, ex.getCode(), ex.getMsg());

        } else {
            apiResult = api.failDefault();
        }

        return apiResult;
    }
}
