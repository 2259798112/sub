package top.duwd.common.exception;

import lombok.Data;

@Data
public class DuException extends RuntimeException{

    public DuException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;

    private String msg;
}
