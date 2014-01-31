package com.silvermindsoftware.stripes.action;

import com.silvermindsoftware.stripes.integration.spring.SpringConstructor;
import com.silvermindsoftware.stripes.integration.spring.SpringManaged;
import com.silvermindsoftware.stripes.integration.spring.SpringParam;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ActionResolver;
import net.sourceforge.stripes.controller.NameBasedActionResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

public class SpringActionResolver extends NameBasedActionResolver implements ActionResolver {

    public static String SPRING_CONTEXT_MANAGER_CLASS_NAME = "SpringContextManager.Class";
    public static String DEFAULT_SPRING_CONTEXT_MANAGER_CLASS_NAME = "com.silvermindsoftware.stripes.action.DefaultSpringContextManager";

    protected SpringContextManager springContextManager;

    public void init(Configuration configuration) throws Exception {

        super.init(configuration);

        final String className = configuration.getBootstrapPropertyResolver().getProperty(SPRING_CONTEXT_MANAGER_CLASS_NAME);

        if (className == null || className.trim().equals("")) {
            springContextManager = (SpringContextManager) Class.forName(DEFAULT_SPRING_CONTEXT_MANAGER_CLASS_NAME).newInstance();
        } else {
            springContextManager = (SpringContextManager) Class.forName(className).newInstance();
        }

    }

    protected ActionBean makeNewActionBean(Class<? extends ActionBean> aClass, ActionBeanContext actionBeanContext) throws Exception {

        // check for SpringClass Annotation that retrieves the bean from spring
        if (aClass.isAnnotationPresent(SpringManaged.class)) {

            SpringManaged springManaged = aClass.getAnnotation(SpringManaged.class);

            final String id;

            if (springManaged.id().equals("")) {
                // perform a lookup on the default class name from the spring context
                final String shortClassName = ClassUtils.getShortName(aClass);
                id = shortClassName.substring(0, 1).toLowerCase() + shortClassName.substring(1);
                return getActionBeanFromSpringContext(id, actionBeanContext);
            } else {
                // perform a lookup against the spring context from a provided bean id
                return getActionBeanFromSpringContext(springManaged.id(), actionBeanContext);
            }

        } else {

            // check for SpringConstructor

            Constructor springConstructor = null;
            SpringConstructor anno = null;

            // check to see if constructor injection is defined in the the ActionBean
            for (Constructor constructor : aClass.getConstructors()) {
                if (constructor.isAnnotationPresent(SpringConstructor.class)) {
                    springConstructor = constructor;
                    anno = (SpringConstructor) constructor.getAnnotation(SpringConstructor.class);
                    break;
                }
            }

            final boolean autowire;
            if (null == anno) {
                autowire = false;
            } else {
                autowire = anno.autowire();
            }

            // if injection is defined in the constructor
            if (springConstructor != null) {
                final Annotation[][] annotations = springConstructor.getParameterAnnotations();
                final Object[] params = new Object[annotations.length];

                final ApplicationContext applicationContext = springContextManager.getApplicationContext(actionBeanContext.getServletContext());

                for (int x = 0; x < annotations.length; x++) {
                    for (Annotation annotation : annotations[x]) {
                        if (annotation.annotationType() == SpringParam.class) {
                            SpringParam springParam = (SpringParam) annotation;
                            params[x] = applicationContext.getBean(springParam.refId());
                        }
                    }
                }

                // OK, we have the annotated parameters, look for any nulls and try to autowire them
                if (autowire) {
                    final Class[] parameterTypes = springConstructor.getParameterTypes();
                    for (int x = 0; x < params.length; x++) {
                        if (params[x] == null) {
                            params[x] = applicationContext.getBean(parameterTypes[x]);
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

        final ActionBean actionBean;

        actionBean = (ActionBean) springContextManager.getApplicationContext(actionBeanContext.getServletContext()).getBean(id);

        if (actionBean == null) throw new RuntimeException("No bean found for id " + id);

        return actionBean;

    }

}
