package io.sabitovka.util.validation.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RunWith {
    Class<? extends Annotation> annotation();
}
