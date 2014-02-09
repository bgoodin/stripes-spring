package com.silvermindsoftware.stripes.action;

import com.silvermindsoftware.stripes.integration.spring.SpringConstructor;
import com.silvermindsoftware.stripes.integration.spring.SpringManaged;
import com.silvermindsoftware.stripes.integration.spring.SpringParam;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ActionResolver;
import net.sourceforge.stripes.controller.NameBasedActionResolver;
import net.sourceforge.stripes.exception.StripesServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

public class SpringActionResolver extends NameBasedActionResolver implements ActionResolver {

    private static final Logger log = LoggerFactory.getLogger(SpringActionResolver.class);

    public static String SPRING_CONTEXT_MANAGER_CLASS_NAME = "SpringContextManager.Class";
    public static String DEFAULT_SPRING_CONTEXT_MANAGER_CLASS_NAME = "com.silvermindsoftware.stripes.action.DefaultSpringContextManager";

    protected SpringContextManager springContextManager;

    public void init(Configuration configuration) throws Exception {

        super.init(configuration);

        final String className = configuration.getBootstrapPropertyResolver().getProperty(SPRING_CONTEXT_MANAGER_CLASS_NAME);

        if (className == null || className.trim().equals("")) {
            log.debug("using {} as spring context manager (default)", DEFAULT_SPRING_CONTEXT_MANAGER_CLASS_NAME);
            springContextManager = (SpringContextManager) Class.forName(DEFAULT_SPRING_CONTEXT_MANAGER_CLASS_NAME).newInstance();
        } else {
            log.debug("using {} as spring context manager", className);
            springContextManager = (SpringContextManager) Class.forName(className).newInstance();
        }

        log.debug("created spring context manager: ", springContextManager);

    }

    protected ActionBean makeNewActionBean(Class<? extends ActionBean> actionClass, ActionBeanContext actionBeanContext) throws Exception {

        log.debug("creating action {} with context {}", actionClass, actionBeanContext);

        if (actionClass.isAnnotationPresent(SpringManaged.class)) {

            log.debug("{} is spring managed", actionClass);

            final SpringManaged springManaged = actionClass.getAnnotation(SpringManaged.class);

            if (springManaged.id().equals("")) {

                final String shortClassName = ClassUtils.getShortName(actionClass);
                final String id = shortClassName.substring(0, 1).toLowerCase() + shortClassName.substring(1);

                log.debug("id not provided for {}, looking for bean using {}", actionClass, id);

                return getActionBeanFromSpringContext(id, actionBeanContext);

            } else {

                final String id = springManaged.id();

                log.debug("looking for {} using spring id {}", actionClass, id);

                return getActionBeanFromSpringContext(id, actionBeanContext);

            }

        } else {

            final Constructor springConstructor = getSpringConstructor(actionClass);

            if (springConstructor != null) {

                final Object[] params = new Object[springConstructor.getParameterTypes().length];

                log.debug("{} has {} parameters", springConstructor, params.length);

                final ServletContext servletContext = actionBeanContext.getServletContext();
                final ApplicationContext applicationContext = springContextManager.getApplicationContext(servletContext);

                final Annotation[][] annotations = springConstructor.getParameterAnnotations();

                log.debug("looking for annotated constructor parameters");
                for (int x = 0; x < params.length; x++) {
                    for (final Annotation annotation : annotations[x]) {
                        if (annotation.annotationType() == SpringParam.class) {
                            final SpringParam springParam = (SpringParam) annotation;
                            params[x] = applicationContext.getBean(springParam.refId());
                            log.debug("setting parameter {} to ref {}", x, springParam.refId());
                        }
                    }
                }

                if (isConstructorAutowired(springConstructor)) {

                    log.debug("attempting to autowire {}", springConstructor);

                    final Class[] parameterTypes = springConstructor.getParameterTypes();

                    for (int x = 0; x < params.length; x++) {
                        if (params[x] == null) {
                            params[x] = applicationContext.getBean(parameterTypes[x]);
                            log.debug("set parameter {} to {}", x, params[x]);
                        }
                    }
                }

                return (ActionBean) springConstructor.newInstance(params);

            }

            log.debug("using default action creation");
            return super.makeNewActionBean(actionClass, actionBeanContext);

        }

    }

    @Override
    public ActionBean getActionBean(ActionBeanContext context, String urlBinding) throws StripesServletException {
        return super.getActionBean(context, urlBinding);
    }

    @Override
    public ActionBean getActionBean(ActionBeanContext context) throws StripesServletException {
        return super.getActionBean(context);
    }

    private boolean isConstructorAutowired(final Constructor constructor) {
        final SpringConstructor annotation;
        annotation = (SpringConstructor) constructor.getAnnotation(SpringConstructor.class);
        return annotation.autowire();
    }

    private Constructor getSpringConstructor(final Class<? extends ActionBean> actionClass) {

        log.debug("checking constructors of {} for annotations", actionClass);

        for (Constructor constructor : actionClass.getConstructors()) {
            if (constructor.isAnnotationPresent(SpringConstructor.class)) {
                log.debug("found annotation on {}", constructor);
                return constructor;
            }
        }

        log.debug("no annotated constructor found for {}", actionClass);

        return null;

    }

    protected ActionBean getActionBeanFromSpringContext(String id, ActionBeanContext actionBeanContext) {

        final ActionBean actionBean;

        actionBean = (ActionBean) springContextManager.getApplicationContext(actionBeanContext.getServletContext()).getBean(id);

        if (actionBean == null) throw new RuntimeException("No bean found for id " + id);

        return actionBean;

    }

}
