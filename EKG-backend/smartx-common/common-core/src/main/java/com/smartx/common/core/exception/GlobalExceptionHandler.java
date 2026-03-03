package com.smartx.common.core.exception;

import com.smartx.common.core.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统发生异常: ", e);
        // 将报错信息包装进统一返回格式中
        return Result.fail(500, e.getMessage() != null ? e.getMessage() : "系统内部错误");
    }
}
