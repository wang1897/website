package com.aethercoder.foundation.advice;

import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.ManagedBean;

/**
 * Created by hepengfei on 07/03/2018.
 */
@ManagedBean
@RequestScope
public class RequestContext {
    private String requestBody;

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
