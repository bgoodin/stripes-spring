package com.silvermindsoftware.stripes.integration.spring;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface SpringParam {
	/**
	 * This is the id of the bean defined in your Spring context
	 *
	 * @return String
	 */
	String refId();
}
