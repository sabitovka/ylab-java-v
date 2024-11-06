package io.sabitovka.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = { "org.springdoc"})
@Import({
        org.springdoc.core.configuration.SpringDocConfiguration.class,
        org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration.class,
        org.springdoc.webmvc.ui.SwaggerConfig.class,
        org.springdoc.core.properties.SwaggerUiConfigProperties.class,
        org.springdoc.core.properties.SwaggerUiOAuthProperties.class
})
public class ApiConfig {
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .packagesToScan("io.sabitovka.controller")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().openapi("3.0.1")
                .info(new Info()
                        .title("Habit Tracker API")
                        .description("API приложения для отслеживания привычек")
                        .version("3.0.1"));
    }
}
