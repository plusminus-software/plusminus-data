package software.plusminus.data.event.aspect;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import software.plusminus.data.event.DataEventPublisher;
import software.plusminus.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Aspect
@Component
@AllArgsConstructor
public class RepositoryEventAspect {

    private DataEventPublisher publisher;

    @Pointcut("execution(* org.springframework.data.repository.Repository+.save(..)) "
            + "|| execution(* org.springframework.data.repository.Repository+.saveAndFlush(..))")
    void save() {
        // pointcut declaration
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.saveAll(..)) "
            + "|| execution(* org.springframework.data.repository.Repository+.saveAllAndFlush(..))")
    void saveAll() {
        // pointcut declaration
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.delete(..))")
    void delete() {
        // pointcut declaration
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.deleteAll(..))")
    void deleteAll() {
        // pointcut declaration
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.find*(..)) "
            + "|| execution(* org.springframework.data.repository.Repository+.get*(..)) "
            + "|| execution(* org.springframework.data.repository.Repository+.read*(..)) "
            + "|| execution(* org.springframework.data.repository.Repository+.query*(..))")
    void read() {
        // pointcut declaration
    }

    @Around("save() && args(entity)")
    public Object aroundSave(ProceedingJoinPoint joinPoint, Object entity) {
        boolean isNew = publishBeforeWrite(entity);
        Object saved = proceed(joinPoint);
        publishWrite(saved, isNew);
        return saved;
    }

    @Around("saveAll() && args(entities)")
    public Object aroundSaveAll(ProceedingJoinPoint joinPoint, Iterable<?> entities) {
        List<Boolean> newFlags = new ArrayList<>();
        entities.forEach(entity -> newFlags.add(publishBeforeWrite(entity)));
        Object saved = proceed(joinPoint);
        if (saved instanceof Iterable) {
            int index = 0;
            for (Object entity : (Iterable<?>) saved) {
                publishWrite(entity, index < newFlags.size() && newFlags.get(index));
                index++;
            }
        }
        return saved;
    }

    @Around("delete() && args(entity)")
    public Object aroundDelete(ProceedingJoinPoint joinPoint, Object entity) {
        publisher.publishBeforeDelete(entity);
        Object result = proceed(joinPoint);
        publisher.publishDelete(entity);
        return result;
    }

    @Around("deleteAll() && args(entities)")
    public Object aroundDeleteAll(ProceedingJoinPoint joinPoint, Iterable<?> entities) {
        entities.forEach(publisher::publishBeforeDelete);
        Object result = proceed(joinPoint);
        entities.forEach(publisher::publishDelete);
        return result;
    }

    @Around("read()")
    public Object aroundRead(ProceedingJoinPoint joinPoint) {
        Object result = proceed(joinPoint);
        publishReads(result);
        return result;
    }

    /* Rethrows the repository's exception as is: wrapping it would break callers
       that catch the original type, and ProceedingJoinPoint.proceed() throws Throwable. */
    @SneakyThrows
    private Object proceed(ProceedingJoinPoint joinPoint) {
        return joinPoint.proceed();
    }

    private boolean publishBeforeWrite(Object entity) {
        boolean isNew = entity != null && EntityUtils.findId(entity) == null;
        if (isNew) {
            publisher.publishBeforeCreate(entity);
        } else {
            publisher.publishBeforeUpdate(entity);
        }
        return isNew;
    }

    private void publishWrite(Object entity, boolean isNew) {
        if (isNew) {
            publisher.publishCreate(entity);
        } else {
            publisher.publishUpdate(entity);
        }
    }

    private void publishReads(Object result) {
        if (result instanceof Optional) {
            ((Optional<?>) result).ifPresent(publisher::publishRead);
        } else if (result instanceof Iterable) {
            ((Iterable<?>) result).forEach(publisher::publishRead);
        } else {
            publisher.publishRead(result);
        }
    }
}
