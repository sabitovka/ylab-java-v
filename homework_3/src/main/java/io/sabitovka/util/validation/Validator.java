package io.sabitovka.util.validation;

import io.sabitovka.common.Constants;
import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.exception.ValidationException;
import io.sabitovka.util.validation.annotation.ValidEmail;
import io.sabitovka.util.validation.annotation.ValidName;
import io.sabitovka.util.validation.annotation.ValidPassword;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Validator {
    public static void validate(Object object) {
        List<String> messages = new ArrayList<>();

        Field[] declaredFields = object.getClass().getDeclaredFields();
        try {
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(ValidName.class)) {
                    validateName(object, field, messages);
                }

                if (field.isAnnotationPresent(ValidPassword.class)) {
                    validatePassword(object, field, messages);
                }

                if (field.isAnnotationPresent(ValidEmail.class)) {
                    validateEmail(object, field, messages);
                }
            }

            if (!messages.isEmpty()) {
                throw new ValidationException(messages);
            }
        } catch (IllegalAccessException exception) {
            throw new ApplicationException(ErrorCode.INTERNAL_ERROR, exception);
        }
    }

    private static void validateName(Object object, Field field, List<String> messages) throws IllegalAccessException {
        String name = (String) field.get(object);
        if (name == null || name.trim().isEmpty()) {
            ValidName annotation = field.getAnnotation(ValidName.class);
            String message = annotation.message();
            messages.add(message);
        }
    }

    private static void validatePassword(Object object, Field field, List<String> messages) throws IllegalAccessException {
        String password = (String) field.get(object);
        if (password == null || !password.matches(Constants.PASSWORD_REGEX)) {
            ValidPassword annotation = field.getAnnotation(ValidPassword.class);
            String message = annotation.message();
            messages.add(message);
        }
    }

    private static void validateEmail(Object object, Field field, List<String> messages) throws IllegalAccessException {
        String email = (String) field.get(object);
        if (email == null || !email.matches(Constants.EMAIL_REGEX)) {
            ValidEmail annotation = field.getAnnotation(ValidEmail.class);
            String message = annotation.message();
            messages.add(message);
        }
    }
}
