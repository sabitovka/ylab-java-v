package io.sabitovka.auditlogging.annotation;

import io.sabitovka.auditlogging.condition.OnEnableAuditAnnotation;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnEnableAuditAnnotation.class)
public @interface ConditionalOnEnableAuditAnnotation {
}
