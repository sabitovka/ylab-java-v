package io.sabitovka.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("io.sabitovka")
@EnableAspectJAutoProxy
public class AppConfig {
}
