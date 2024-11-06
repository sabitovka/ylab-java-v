package io.sabitovka.aspect;

import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log
public class LoggableAspect {
    @Pointcut("within(@io.sabitovka.annotation.Loggable *) && execution(* *(..))")
    public void annotatedByLoggable() {}

    @Pointcut("execution(* io.sabitovka.controller.*.*(..))")
    public void anyControllerMethod() {}

    @Around("anyControllerMethod() || annotatedByLoggable()")
    public Object logMethodTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis() - start;
        log.info("Выполнен метод %s [%d ms].\n%n".formatted(proceedingJoinPoint.getSignature(), end));
        return result;
    }
}
