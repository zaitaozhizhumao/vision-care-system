package com.vision.dto;

import lombok.Data;

/**
 * 统一返回结果类
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private Long count;

    // ========== 成功方法 ==========

    /**
     * 成功 - 无数据无消息
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg("success");
        return result;
    }

    /**
     * 成功 - 只有消息
     */
    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg(msg);
        return result;
    }

    /**
     * 成功 - 只有数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    /**
     * 成功 - 消息和数据（解决你的错误）
     */
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 成功 - 数据和总数（分页用）
     */
    public static <T> Result<T> success(T data, Long count) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        result.setCount(count);
        return result;
    }

    // ========== 失败方法 ==========

    /**
     * 失败 - 只有消息
     */
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败 - 错误码和消息
     */
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}