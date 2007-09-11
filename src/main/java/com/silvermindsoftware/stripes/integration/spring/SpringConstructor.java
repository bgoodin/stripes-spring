package com.silvermindsoftware.stripes.integration.spring;

import java.lang.annotation.*;

/**
 * This simply specifies the constructor as a Spring constructor. You
 * MUST also define each parameter with SpringParam {@link @com.silvermindsoftware.stripes.integration.spring.SpringParam()}
 * and you can only have one constructor specified as a Spring constructor.
 * If you define more the first constructor returned by class.getConstructors()
 * with a SpringConstructor annoataion will be used. You should not use SpringConstructor
 * {@link @com.silvermindsoftware.stripes.integration.spring.SpringConstructor} if you use this
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
@Documented
public @interface SpringConstructor {
}
