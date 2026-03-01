package com.smartx.sales.handler;

import com.smartx.sales.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 🌟 核心魔法：向网关如实上报 HTTP 500！
    public Result handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return Result.error(500, "内部服务器错误: " + e.getMessage());
    }

    /**
     * 自定义业务异常
     */
    // @ExceptionHandler(ServiceException.class)
    // public Result handleServiceException(ServiceException e, HttpServletRequest request) {
    //     log.error(e.getMessage(), e);
    //     Integer code = e.getCode();
    //     return code != null ? Result.error(code, e.getMessage()) : Result.error(e.getMessage());
    // }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知运行时异常.", requestURI, e);
        return Result.error(500, e.getMessage());
    }
}
