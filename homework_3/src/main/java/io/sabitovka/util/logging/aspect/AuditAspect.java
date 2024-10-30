package io.sabitovka.util.logging.aspect;

import io.sabitovka.auth.AuthInMemoryContext;
import io.sabitovka.auth.entity.UserDetails;
import io.sabitovka.util.logging.annotation.Audit;
import io.sabitovka.util.logging.annotation.IgnoreAudit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Aspect
public class AuditAspect {
    @Pointcut("@annotation(io.sabitovka.util.logging.annotation.Audit)")
    public void annotatedByAudit() {}

    @Around("annotatedByAudit()")
    public Object logAuditAction(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Audit audit = signature.getMethod().getAnnotation(Audit.class);

        Object result = joinPoint.proceed();

        Optional<UserDetails> userDetails = Optional.ofNullable(AuthInMemoryContext.getContext().isLoggedIn()
                ? AuthInMemoryContext.getContext().getAuthentication() : null);

        String action = audit.action().isEmpty() ? signature.getMethod().getName() : audit.action();
        String username = userDetails.map(UserDetails::getUsername).orElse("Не авторизован");
        String ip = AuthInMemoryContext.getContext().getIp();
        LocalDateTime localDateTime = LocalDateTime.now();

        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] joinPointArgs = joinPoint.getArgs();
        List<String> args = new ArrayList<>();
        for (int i = 0; i < joinPointArgs.length; i++) {
            if (!parameters[i].isAnnotationPresent(IgnoreAudit.class)) {
                args.add(joinPointArgs[i].toString());
            } else {
                args.add("*");
            }
        }

        System.out.printf("[Audit Log]: Пользователь: %s, IP: %s, Действие: %s, Аргументы: %s, Время: %s\n", username, ip, action, args, localDateTime);

        return result;
    }
}
