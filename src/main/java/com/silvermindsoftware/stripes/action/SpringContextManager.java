package com.silvermindsoftware.stripes.action;

import net.sourceforge.stripes.action.ActionBeanContext;
import org.springframework.context.ApplicationContext;

public interface SpringContextManager {

	public ApplicationContext getApplicationContext(ActionBeanContext actionBeanContext);

}
