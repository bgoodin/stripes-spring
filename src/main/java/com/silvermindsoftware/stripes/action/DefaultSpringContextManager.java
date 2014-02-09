package com.silvermindsoftware.stripes.action;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public class DefaultSpringContextManager implements SpringContextManager {
    public ApplicationContext getApplicationContext(ServletContext servletContext) {
        return WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }
}
