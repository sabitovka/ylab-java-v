package io.sabitovka.habittracker.config;

import io.sabitovka.habittracker.common.Constants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(info =
        @Info(
                title = "Habit Track API",
                version = "5.0.0",
                description = "Habit Tracker - Приложение для отслеживания привычек",
                contact = @Contact(name = "Karim Sabitov")
        )
)
@SecurityScheme(
        name = Constants.BEARER_AUTHORIZATION,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}
