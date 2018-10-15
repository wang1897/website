package com.aethercoder.foundation.schedule;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by hepengfei on 08/12/2017.
 */
@Component
public class BatchContext implements ApplicationContextAware {
    private static ApplicationContext mContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mContext = applicationContext;
    }

    public static <T> T getBean(String className) throws Exception {
        String beanName = Class.forName(className).getSimpleName();
        String a = "" + beanName.charAt(0);
        a = a.toLowerCase();
        beanName = a + beanName.substring(1);
        return (T)mContext.getBean(beanName);
    }
}
