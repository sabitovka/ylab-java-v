package io.sabitovka.config;

import io.sabitovka.filter.AuthFilter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MainWebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.scan("io.sabitovka");
        context.setServletContext(container);

        ServletRegistration.Dynamic dispatcher = container.addServlet("mvc", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        registerFilters(container, context);
    }

    private void registerFilters(ServletContext container, AnnotationConfigWebApplicationContext context) {
        context.refresh();
        AuthFilter authFilter = context.getBean(AuthFilter.class);
        FilterRegistration.Dynamic authFilterRegistration = container.addFilter("AuthFilter", authFilter);
        authFilterRegistration.addMappingForUrlPatterns(null, false, "/api/*");
    }
}
