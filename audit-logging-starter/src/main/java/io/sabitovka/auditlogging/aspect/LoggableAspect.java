package io.sabitovka.auditlogging.aspect;

import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Log
public class LoggableAspect {
    @Pointcut("within(@io.sabitovka.auditlogging.annotation.Loggable *) && execution(* *(..))")
    public void annotatedByLoggable() {}

    @Around("annotatedByLoggable()")
    public Object logMethodTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis() - start;
        log.info("Выполнен метод %s [%d ms].".formatted(proceedingJoinPoint.getSignature(), end));
        return result;
    }
}
