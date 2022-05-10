package com.vinson.hotel.utils;

import lombok.Data;

@Data
public class ResultDTO<T> {
    private Integer code;
    private String msg;
    private T data;

    public ResultDTO(Integer code, String message){
        this.code = code;
        this.msg = message;
    }

    public ResultDTO(Integer code, String message, T data){
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    public static <T> ResultDTO<T> SUCCESS(){
        return new ResultDTO<T>(ResultCode.OK.code(),ResultCode.OK.msg());
    }
    public static <T> ResultDTO<T> SUCCESS(T data){
        return new ResultDTO<>(ResultCode.OK.code(),ResultCode.OK.msg(),data);
    }

    public static <T> ResultDTO<T> ERROR(){
        return new ResultDTO<T>(ResultCode.ERROR.code(),ResultCode.ERROR.msg());
    }
    public static <T> ResultDTO<T> ERROR(Integer code, String msg){
        return new ResultDTO<T>(code,msg);
    }
    public static <T> ResultDTO<T> ERROR(T data){
        return new ResultDTO<>(ResultCode.ERROR.code(),ResultCode.ERROR.msg(),data);
    }
}
