package com.silvermindsoftware.stripes.action;

import net.sourceforge.stripes.config.BootstrapPropertyResolver;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.AnnotatedClassActionResolver;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpringActionResolverTest {

    private SpringActionResolver resolver;
    private Configuration configuration;
    private BootstrapPropertyResolver bootstrap;

    @Before
    public void beforeSpringActionResolverTest() throws Exception {
        resolver = new SpringActionResolver();
        configuration = mock(Configuration.class);
        bootstrap = mock(BootstrapPropertyResolver.class);
        when(bootstrap.getProperty(AnnotatedClassActionResolver.PACKAGES)).thenReturn("test");
        when(configuration.getBootstrapPropertyResolver()).thenReturn(bootstrap);
    }

    @Test
    public void should_init_with_default_spring_context_manager() throws Exception {
        // setup test - none required, this is the default

        // run test
        resolver.init(configuration);

        // verify outcome
        final Field field = SpringActionResolver.class.getDeclaredField("springContextManager");
        field.setAccessible(true);
        SpringContextManager manager = (SpringContextManager) field.get(resolver);
        assertEquals(DefaultSpringContextManager.class, manager.getClass());

    }

    @Test
    public void should_init_with_custom_spring_context_manager() throws Exception {
        // setup test
        when(bootstrap.getProperty(SpringActionResolver.SPRING_CONTEXT_MANAGER_CLASS_NAME)).thenReturn("com.silvermindsoftware.stripes.action.SpringActionResolverTest$TestSpringContextManager");

        // run test
        resolver.init(configuration);

        // verify outcome
        final Field field = SpringActionResolver.class.getDeclaredField("springContextManager");
        field.setAccessible(true);
        SpringContextManager manager = (SpringContextManager) field.get(resolver);
        assertEquals(TestSpringContextManager.class, manager.getClass());

    }

    public static class TestSpringContextManager implements SpringContextManager {
        public ApplicationContext getApplicationContext(ServletContext servletContext) {
            return null;
        }
    }

}
