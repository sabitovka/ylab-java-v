package io.sabitovka.config;

import io.sabitovka.filter.AuthFilter;
import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockServletContext;

@Configuration
@ComponentScan(
        basePackages = "io.sabitovka",
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                WebConfig.class,
                        }
                )
        }
)
public class TestConfig {
    @Bean
    public ServletContext servletContext() {
        return new MockServletContext();
    }
}
