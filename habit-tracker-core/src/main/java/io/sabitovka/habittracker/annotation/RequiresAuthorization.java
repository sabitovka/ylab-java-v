package io.sabitovka.habittracker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация используется для того, чтобы указать необходимость наличия авторизации перед вызовом метода
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAuthorization {
    /**
     * Указывает, что метод может быть вызван только администратором
     * @return `true` для админа, `false` иначе
     */
    boolean onlyAdmin() default false;
}
