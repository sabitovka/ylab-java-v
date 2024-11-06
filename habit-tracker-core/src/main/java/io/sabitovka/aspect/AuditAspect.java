package io.sabitovka.aspect;

import io.sabitovka.annotation.Audit;
import io.sabitovka.annotation.IgnoreAudit;
import io.sabitovka.auth.AuthInMemoryContext;
import io.sabitovka.auth.entity.UserDetails;
import io.sabitovka.model.AuditRecord;
import io.sabitovka.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditService auditService;

    @Pointcut("@annotation(io.sabitovka.annotation.Audit)")
    public void annotatedByAudit() {}

    @Around("annotatedByAudit()")
    public Object logAuditAction(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget()
                .getClass()
                .getMethod(signature.getName(), signature.getParameterTypes());
        Audit audit = method.getAnnotation(Audit.class);

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
            String arg = !parameters[i].isAnnotationPresent(IgnoreAudit.class) ? joinPointArgs[i].toString() : "*";
            args.add(arg);
        }

        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setUsername(username);
        auditRecord.setIp(ip);
        auditRecord.setTimestamp(localDateTime);
        auditRecord.setArguments(args.toString());
        auditRecord.setAction(action);

        auditService.saveAudit(auditRecord);

        return result;
    }
}
