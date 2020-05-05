package top.duwd.common.exception;

import org.springframework.stereotype.Component;

@Component
public class DuExceptionManager {

    public DuException create(int code) {
        return new DuException(code, ExceptionDetails.map.get(code));
    }
}
