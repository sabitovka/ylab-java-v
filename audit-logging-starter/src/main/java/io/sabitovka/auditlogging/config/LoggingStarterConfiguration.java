package io.sabitovka.auditlogging.config;

import io.sabitovka.auditlogging.aspect.LoggableAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class LoggingStarterConfiguration {
    @Bean
    public LoggableAspect loggableAspect() {
        return new LoggableAspect();
    }
}
