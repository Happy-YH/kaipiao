package com.example.invoice.kingdee;

import lombok.Data;

import java.io.Serializable;

/**
 * 金蝶接口统一响应结果封装类
 */
@Data
public class KingdeeResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private T data;
    private boolean success;

    public static <T> KingdeeResult<T> success(T data) {
        KingdeeResult<T> result = new KingdeeResult<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    public static <T> KingdeeResult<T> success(String message, T data) {
        KingdeeResult<T> result = new KingdeeResult<>();
        result.setCode(0);
        result.setMessage(message);
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    public static <T> KingdeeResult<T> error(String message) {
        KingdeeResult<T> result = new KingdeeResult<>();
        result.setCode(-1);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }

    public static <T> KingdeeResult<T> error(Integer code, String message) {
        KingdeeResult<T> result = new KingdeeResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }
}
