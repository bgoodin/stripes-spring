package com.silvermindsoftware.stripes.action;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import net.sourceforge.stripes.action.ActionBeanContext;

import javax.servlet.ServletContext;

/**
 * User: brandongoodin
 * Date: Nov 27, 2007
 * Time: 12:51:51 PM
 */
public class DefaultSpringContextManager implements SpringContextManager {

	public ApplicationContext getApplicationContext(ServletContext servletContext) {
		return WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}

}
