package com.silvermindsoftware.stripes.action;

import com.silvermindsoftware.stripes.integration.spring.SpringConstructor;
import com.silvermindsoftware.stripes.integration.spring.SpringManaged;
import com.silvermindsoftware.stripes.integration.spring.SpringParam;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.controller.ActionResolver;
import net.sourceforge.stripes.controller.NameBasedActionResolver;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

public class SpringActionResolver extends NameBasedActionResolver implements ActionResolver {


    protected ActionBean makeNewActionBean(Class<? extends ActionBean> aClass, ActionBeanContext actionBeanContext) throws Exception {

        // check for SpringClass Annotation that retrieves the bean from spring
        if (aClass.isAnnotationPresent(SpringManaged.class)) {

            SpringManaged springManaged = aClass.getAnnotation(SpringManaged.class);

            String id = null;
            ActionBean actionBean = null;

            if (springManaged.id().equals("")) {
                // preform a lookup on the default class name from the spring context
                String shortClassName = ClassUtils.getShortName(aClass);
                id = shortClassName.substring(0, 1).toLowerCase() + shortClassName.substring(1);
                return getActionBeanFromSpringContext(id, actionBeanContext);

            } else {
                // perform a lookup against the spring context from a provided bean id
                return getActionBeanFromSpringContext(springManaged.id(), actionBeanContext);
            }

        } else {

            // check for SpringConstructor

            Constructor springConstructor = null;

            // check to see if constructor injection is defined in the the ActionBean
            for (Constructor constructor : aClass.getConstructors()) {
                if (constructor.isAnnotationPresent(SpringConstructor.class)) {
                    springConstructor = constructor;
                    break;
                }
            }

            // if injection is defined in the constructor
            if (springConstructor != null) {
                Annotation[][] annotations = springConstructor.getParameterAnnotations();
                Object[] params = new Object[annotations.length];

                for (int x = 0; x < annotations.length; x++) {
                    for (Annotation annotation : annotations[x]) {
                        if (annotation.annotationType() == SpringParam.class) {
                            SpringParam springParam = (SpringParam) annotation;
                            params[x] =
                                    WebApplicationContextUtils
                                            .getWebApplicationContext(actionBeanContext.getServletContext())
                                            .getBean(springParam.refId());
                        }
                    }
                }

                return (ActionBean) springConstructor.newInstance(params);
            }

            // otherwise use default action creation
            return super.makeNewActionBean(aClass, actionBeanContext);
        }

    }

    protected ActionBean getActionBeanFromSpringContext(String id, ActionBeanContext actionBeanContext) {

        ActionBean actionBean = (ActionBean) WebApplicationContextUtils
                .getWebApplicationContext(actionBeanContext.getServletContext())
                .getBean(id);

        return actionBean;

    }
}
