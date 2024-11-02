package io.sabitovka.util.logging.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggableAspect {
    @Pointcut("within(@io.sabitovka.util.logging.annotation.Loggable *) && execution(* *(..))")
    public void annotatedByLoggable() {}

    @Pointcut("execution(* io.sabitovka.controller.*.*(..))")
    public void anyControllerMethod() {}

    @Around("anyControllerMethod() || annotatedByLoggable()")
    public Object logMethodTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis() - start;
        System.out.printf("Выполнен метод %s [%d ms].\n%n", proceedingJoinPoint.getSignature(), end);
        return result;
    }
}
