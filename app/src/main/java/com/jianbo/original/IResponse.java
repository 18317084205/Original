package com.jianbo.original;

/**
 * Created by Jianbo on 2018/4/9.
 */

public class IResponse<T> {
    public int code;
    public String message;
    public T data;
    public byte[] bytes;
}
