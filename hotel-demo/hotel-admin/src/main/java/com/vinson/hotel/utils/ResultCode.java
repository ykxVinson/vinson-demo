package com.vinson.hotel.utils;

public enum ResultCode {

    OK(200, "Success"),
    EXCEPTION(2001, "exception."),
    ERROR(500, "Internal Server Error.");

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    private Integer code;
    private String msg;

    public Integer code(){return code;}
    public String msg(){return msg;}
}
