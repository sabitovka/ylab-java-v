package io.sabitovka.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("io.sabitovka")
@EnableAspectJAutoProxy
@EnableWebMvc
public class AppConfig {
}
