package com.silvermindsoftware.stripes.action;

import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;

public interface SpringContextManager {

	public ApplicationContext getApplicationContext(ServletContext servletContext);

}
