package io.sabitovka.auditlogging.config;

import io.sabitovka.auditlogging.aspect.LoggableAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class LoggingStarterConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public LoggableAspect loggableAspect() {
        return new LoggableAspect();
    }
}
