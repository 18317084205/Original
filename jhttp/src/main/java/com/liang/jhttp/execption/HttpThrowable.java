package com.liang.jhttp.execption;

public class HttpThrowable extends Exception {
    private int code;

    public HttpThrowable(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
