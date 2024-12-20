package io.sabitovka.habittracker.auth.aspect;

import io.sabitovka.habittracker.auth.annotation.RequiresAuthorization;
import io.sabitovka.habittracker.auth.AuthInMemoryContext;
import io.sabitovka.habittracker.auth.entity.UserDetails;
import io.sabitovka.habittracker.enums.ErrorCode;
import io.sabitovka.habittracker.exception.ApplicationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Аспект для проверки авторизации перед вызовом метода
 */
@Aspect
@Component
public class RequireAuthorizationAspect {
    @Pointcut("@annotation(io.sabitovka.habittracker.auth.annotation.RequiresAuthorization)")
    public void annotatedByRequiresAnnotation() {}

    @Around("annotatedByRequiresAnnotation()")
    public Object checkAuthorization(ProceedingJoinPoint joinPoint) throws Throwable {
        UserDetails authentication = AuthInMemoryContext.getContext().getAuthentication();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RequiresAuthorization annotation = method.getAnnotation(RequiresAuthorization.class);
        if (annotation.onlyAdmin() && !authentication.isAdmin()) {
            throw new ApplicationException(ErrorCode.FORBIDDEN, "Доступ только администраторам");
        }

        return joinPoint.proceed();
    }
}
