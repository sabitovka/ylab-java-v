package io.sabitovka.util.validation;

import io.sabitovka.common.Constants;
import io.sabitovka.exception.ValidationException;
import io.sabitovka.util.validation.annotation.Email;
import io.sabitovka.util.validation.annotation.Name;
import io.sabitovka.util.validation.annotation.Password;
import io.sabitovka.util.validation.annotation.RunWith;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

                Method[] methods = Validator.class.getDeclaredMethods();
                for (Method method : methods) {
                    RunWith runWith = method.getAnnotation(RunWith.class);
                    if (runWith == null) {
                        continue;
                    }

                    Annotation fieldAnnotation = field.getAnnotation(runWith.annotation());
                    if (fieldAnnotation == null) {
                        continue;
                    }

                    method.invoke(Validator.class, object, field, messages);
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException("Произошла неизвестная ошибка при валидации полей: %s".formatted(exception.getMessage()));
        }

        if (!messages.isEmpty()) {
            throw new ValidationException(messages);
        }
    }

    @RunWith(annotation = Name.class)
    private static void validateName(Object object, Field field, List<String> messages) throws IllegalAccessException {
        String name = (String) field.get(object);
        if (name == null || name.trim().isEmpty()) {
            Name annotation = field.getAnnotation(Name.class);
            String message = annotation.message();
            messages.add(message);
        }
    }

    @RunWith(annotation = Password.class)
    private static void validatePassword(Object object, Field field, List<String> messages) throws IllegalAccessException {
        String password = (String) field.get(object);
        if (password == null || !password.matches(Constants.PASSWORD_REGEX)) {
            Password annotation = field.getAnnotation(Password.class);
            String message = annotation.message();
            messages.add(message);
        }
    }

    @RunWith(annotation = Email.class)
    private static void validateEmail(Object object, Field field, List<String> messages) throws IllegalAccessException {
        String email = (String) field.get(object);
        if (email == null || !email.matches(Constants.EMAIL_REGEX)) {
            Email annotation = field.getAnnotation(Email.class);
            String message = annotation.message();
            messages.add(message);
        }
    }
}
