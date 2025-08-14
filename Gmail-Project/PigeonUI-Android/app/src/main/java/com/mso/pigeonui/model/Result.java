package com.mso.pigeonui.model;

public class Result<T> {
    private T data;
    private String error;
    private boolean success;

    public Result(T data) {
        this.data = data;
        this.success = true;
    }

    public Result(String error) {
        this.error = error;
        this.success = false;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> failure(Throwable throwable) {
        return new Result<>(throwable.getMessage());
    }

}
