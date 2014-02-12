package com.silvermindsoftware.stripes.integration.spring;

import java.lang.annotation.*;

/**
 * This simply specifies the constructor as a Spring constructor.
 * You can only have one constructor specified as a Spring constructor.
 * You can annotate parameters with @SpringParam
 * {@link @com.silvermindsoftware.stripes.integration.spring.SpringParam()}
 * or, you can set the autowire parameter to true to allow the action resolver
 * to ask the spring context for a bean of the same type as the parameter.
 * If you annotate more than one constructor the first annotated constructor
 * returned by class.getConstructors() will be used.
 * You can also mix @SpringParam with auto wiring - in that case, the
 * annotated parameters will be set first, then any remaining null values will
 * be auto-wired by type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
@Documented
public @interface SpringConstructor {
	boolean autowire() default true;
}
