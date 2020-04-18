package com.hwj.tieba.exception;

import com.hwj.tieba.resp.ResponseEnum;
import com.hwj.tieba.resp.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {
    private Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    public ServerResponse captureException(Exception e){
        log.error(e.getMessage(),e);
        return ServerResponse.createByErrorCodeMessage(ResponseEnum.ERROR.getCode(),"系统繁忙，请稍后再试");
    }

    @ExceptionHandler(TieBaException.class)
    public ServerResponse captureException(TieBaException e){
        log.error(e.getMessage(),e);
        return ServerResponse.createByErrorCodeMessage(e.getExceptionStatus(),e.getMessage());
    }

}
