package com.silvermindsoftware.stripes.action;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultSpringContextManagerTest {

    @Test
    public void should_get_app_context_from_servlet_context() {
        // setup test
        final DefaultSpringContextManager contextManager = new DefaultSpringContextManager();
        final ServletContext servletContext = mock(ServletContext.class);
        final ApplicationContext expectedContext = mock(WebApplicationContext.class);

        when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(expectedContext);

        // run test
        final ApplicationContext applicationContext = contextManager.getApplicationContext(servletContext);

        // verify outcome
        assertEquals(expectedContext, applicationContext);

    }

}
