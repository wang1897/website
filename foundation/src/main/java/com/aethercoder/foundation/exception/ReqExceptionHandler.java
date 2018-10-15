package com.aethercoder.foundation.exception;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.FileNotExistException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.foundation.advice.RequestContext;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.service.LocaleMessageService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.inject.Inject;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.stream.Collectors;

@ControllerAdvice
public class ReqExceptionHandler {

    @Autowired
    public LocaleMessageService localeMessageUtil;

    @Inject
    private RequestContext requestContext;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 异常处理方法
     * 返回400
     */

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BlockChainException.class)
    @ResponseBody
    public ReqExceptionEntity handleBlockChainException(HttpServletRequest request, AppException ex) {
        String message = null;
        String requestParam = "";

        try {
            requestParam = getRequestParam(request);
            message = localeMessageUtil.getLocalErrorMessage(ex.getErrorCode(), ex.getMessageArgs());
        } catch (NoSuchMessageException e) {
            message = ex.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error(ex.toString() + " " + ex.getMessage() + requestParam, ex);
        return ReqExceptionEntity.bulid(ex.getErrorCode(), message);
    }

    /**
     * 异常处理方法
     * 返回400
     */

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AppException.class)
    @ResponseBody
    public ReqExceptionEntity handleException(AppException appException) {
        String message = null;
        try {
            message = localeMessageUtil.getLocalErrorMessage(appException.getErrorCode(), appException.getMessageArgs());
        } catch (Exception e) {
            message = appException.getMessage();
        }
        logger.warn("appException", appException.getErrorCode(), message, appException);
        return ReqExceptionEntity.bulid(appException.getErrorCode(), message);
    }

    /**
     * 异常处理方法
     * 返回404
     */

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileNotExistException.class)
    @ResponseBody
    public ReqExceptionEntity handleException(FileNotExistException fileNotExistException) {
        logger.warn("fileNotExistException", fileNotExistException.getFilename(), fileNotExistException.getMessage());
        return null;
    }

    /**
     * 异常处理方法
     * 返回500
     */

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)  // 500
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ReqExceptionEntity handleException(HttpServletRequest request,  Exception ex) {
        String requestParam = "";
        try {
            requestParam = getRequestParam(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error(ex.toString() + " " + ex.getMessage() + requestParam, ex);
        return ReqExceptionEntity.bulid(ex.getMessage(), ex.getStackTrace());
    }

    private String getRequestParam(HttpServletRequest request) throws IOException{
        String body = requestContext.getRequestBody();
        String user = "";
        StringBuilder req = new StringBuilder();
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
        if (accountNo == null) {
            accountNo = "";
        }
//        Object o = SecurityContextHolder.getContext().getAuthentication().getCredentials();
//        if (o != null) {
//            user = o.toString();
//        }
        req.append(" ---accountNo:").append(user).append("--- ");
        req.append(" ---url:").append(request.getRequestURL()).append("--- ");

        req.append(" ---parameters ");
        Enumeration<String> requestNames = request.getParameterNames();
        while (requestNames.hasMoreElements()) {
            String reqName = requestNames.nextElement();
            String value = request.getParameter(reqName);
            req.append(" ").append(reqName).append(":").append(value);
        }
        req.append("--- ");

        req.append(" ---headers ");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String value = request.getHeader(headerName);
            req.append(" ").append(headerName).append(":").append(value);
        }
        req.append("--- ");
        req.append(" ---body ");
        req.append(body == null ? "" : body);
        req.append("---");



        return req.toString();
    }


}
