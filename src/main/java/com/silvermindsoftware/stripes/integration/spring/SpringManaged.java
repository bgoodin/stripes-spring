package com.silvermindsoftware.stripes.integration.spring;

import java.lang.annotation.*;

/**
 * The Spring managed notation is when you want to use Spring as your
 * factor for the ActionBean. I prefer defining it all in the ActionBean
 * rather than the context xml file. But, I figured I'd throw this in for
 * those who wish to manage their ActionBeans in spring. Be sure to set your
 * ActionBeans as scope="prototype" in Spring.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface SpringManaged {
	/**
	 * This is the id of the bean defined in the Spring context. If no id is specified
	 * the name of the short name of the class will be used and the first letter of the
	 * class will be lower cased.
	 * <p/>
	 * For example:
	 * <p/>
	 * com.foo.PersonActionBean will be converted to personActionBean for the id
	 *
	 * @return String
	 */
	String id() default "";
}
