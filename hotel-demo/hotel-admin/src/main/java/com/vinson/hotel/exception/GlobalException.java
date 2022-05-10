package com.vinson.hotel.exception;

import com.vinson.hotel.utils.ResultCode;
import com.vinson.hotel.utils.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 全局异常处理类
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler
    public ResultDTO<?> doException(Exception ex){
        ex.printStackTrace();
        log.error(ex.getMessage());
        return ResultDTO.ERROR(ResultCode.EXCEPTION.code(),ex.getMessage());
    }

    @ExceptionHandler
    public ResultDTO<?> securityException(SecurityException ex){
        ex.printStackTrace();
        log.warn(ex.getMessage());
        return ResultDTO.ERROR(HttpStatus.UNAUTHORIZED.value(),ex.getMessage());
    }

    @ExceptionHandler
    public ResultDTO<?> runtimeException(RuntimeException ex) {
        ex.printStackTrace();
        log.error("Runtime Exception", ex);
        return ResultDTO.ERROR(ResultCode.EXCEPTION.code(),ex.getMessage());
    }

    @ExceptionHandler
    public ResultDTO<?> sqlException(SQLException ex) {
        ex.printStackTrace();
        log.error("SQL Exception", ex);
        return ResultDTO.ERROR(ResultCode.EXCEPTION.code(),ex.getMessage());
    }

}
