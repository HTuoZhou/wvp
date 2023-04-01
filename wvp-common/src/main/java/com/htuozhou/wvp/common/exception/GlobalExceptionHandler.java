package com.htuozhou.wvp.common.exception;

import com.htuozhou.wvp.common.result.ApiFinalResult;
import com.htuozhou.wvp.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * @author hanzai
 * @date 2023/2/2
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 服务器异常
     */
    @ExceptionHandler(Exception.class)
    public ApiFinalResult<Object> exceptionHandler(Exception e, HttpServletRequest request) {
        log.info("请求URL: {}", request.getRequestURL().toString(), e);
        return ApiFinalResult.error(ResultCodeEnum.FAIL, e.getLocalizedMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiFinalResult<Object> businessExceptionHandler(BusinessException e, HttpServletRequest request) {
        log.info("请求URL: {}", request.getRequestURL().toString(), e);
        return ApiFinalResult.error(e.getCode(), e.getMsg(), e.getData());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiFinalResult<Object> constraintViolationExceptionHandler(ConstraintViolationException e, HttpServletRequest request) {
        log.info("请求URL: {}", request.getRequestURL().toString(), e);

        StringBuilder stringBuilder = new StringBuilder();

        Set<ConstraintViolation<?>> constraintViolationSet = e.getConstraintViolations();
        for (ConstraintViolation<?> x : constraintViolationSet) {
            stringBuilder.append(x.getMessageTemplate()).append("，");
        }
        stringBuilder.substring(0, stringBuilder.length() - 1);

        return ApiFinalResult.error(ResultCodeEnum.PARAMETER, stringBuilder);
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiFinalResult<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.info("请求URL: {}", request.getRequestURL().toString(), e);

        StringBuilder stringBuilder = new StringBuilder();

        BeanPropertyBindingResult result = (BeanPropertyBindingResult) e.getBindingResult();
        List<ObjectError> errors = result.getAllErrors();
        for (ObjectError x : errors) {
            stringBuilder.append(x.getDefaultMessage()).append("，");
        }
        stringBuilder.substring(0, stringBuilder.length() - 1);

        return ApiFinalResult.error(ResultCodeEnum.PARAMETER, stringBuilder);
    }

}
