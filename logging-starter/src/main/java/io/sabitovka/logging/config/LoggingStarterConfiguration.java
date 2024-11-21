package io.sabitovka.logging.config;

import io.sabitovka.logging.aspect.LoggableAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Конфигурация логирования
 */
@Configuration
@EnableAspectJAutoProxy
public class LoggingStarterConfiguration {
    @Bean
    public LoggableAspect loggableAspect() {
        return new LoggableAspect();
    }
}
