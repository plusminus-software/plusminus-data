package software.plusminus.data.event.aspect;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import software.plusminus.data.event.CreateEvent;
import software.plusminus.data.event.DeleteEvent;
import software.plusminus.data.event.ReadEvent;
import software.plusminus.data.event.UpdateEvent;
import software.plusminus.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fires {@link CreateEvent}, {@link UpdateEvent}, {@link DeleteEvent} and {@link ReadEvent}
 * around any Spring Data repository operation. Events can be consumed with Spring's
 * {@code @EventListener} and are generically typed by the entity type.
 *
 * <p>A save is reported as a {@link CreateEvent} when the entity has no id yet, otherwise as an
 * {@link UpdateEvent}. Read methods that return a collection (including a
 * {@link org.springframework.data.domain.Page}) fire one {@link ReadEvent} per element.
 */
@Aspect
@Component
@AllArgsConstructor
@SuppressWarnings({"checkstyle:IllegalThrows", "PMD.SignatureDeclareThrowsException"})
public class RepositoryEventAspect {

    private final ApplicationEventPublisher publisher;

    @Pointcut("execution(* org.springframework.data.repository.Repository+.save(..)) "
            + "|| execution(* org.springframework.data.repository.Repository+.saveAndFlush(..))")
    void saveMethod() {
        // pointcut declaration
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.saveAll(..)) "
            + "|| execution(* org.springframework.data.repository.Repository+.saveAllAndFlush(..))")
    void saveAllMethod() {
        // pointcut declaration
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.delete(..))")
    void deleteMethod() {
        // pointcut declaration
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.deleteAll(..))")
    void deleteAllMethod() {
        // pointcut declaration
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.find*(..)) "
            + "|| execution(* org.springframework.data.repository.Repository+.get*(..)) "
            + "|| execution(* org.springframework.data.repository.Repository+.read*(..)) "
            + "|| execution(* org.springframework.data.repository.Repository+.query*(..))")
    void readMethod() {
        // pointcut declaration
    }

    @Around("saveMethod() && args(entity)")
    public Object aroundSave(ProceedingJoinPoint joinPoint, Object entity) throws Throwable {
        boolean isNew = isNew(entity);
        Object saved = joinPoint.proceed();
        publishWrite(saved, isNew);
        return saved;
    }

    @Around("saveAllMethod() && args(entities)")
    public Object aroundSaveAll(ProceedingJoinPoint joinPoint, Iterable<?> entities) throws Throwable {
        List<Boolean> newFlags = new ArrayList<>();
        entities.forEach(entity -> newFlags.add(isNew(entity)));
        Object saved = joinPoint.proceed();
        if (saved instanceof Iterable) {
            int index = 0;
            for (Object entity : (Iterable<?>) saved) {
                boolean isNew = index < newFlags.size() ? newFlags.get(index) : isNew(entity);
                publishWrite(entity, isNew);
                index++;
            }
        }
        return saved;
    }

    @Around("deleteMethod() && args(entity)")
    public Object aroundDelete(ProceedingJoinPoint joinPoint, Object entity) throws Throwable {
        Object result = joinPoint.proceed();
        if (entity != null) {
            publisher.publishEvent(new DeleteEvent<>(entity));
        }
        return result;
    }

    @Around("deleteAllMethod() && args(entities)")
    public Object aroundDeleteAll(ProceedingJoinPoint joinPoint, Iterable<?> entities) throws Throwable {
        Object result = joinPoint.proceed();
        entities.forEach(entity -> {
            if (entity != null) {
                publisher.publishEvent(new DeleteEvent<>(entity));
            }
        });
        return result;
    }

    @Around("readMethod()")
    public Object aroundRead(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        publishReads(result);
        return result;
    }

    private void publishReads(Object result) {
        if (result instanceof Optional) {
            ((Optional<?>) result).ifPresent(this::publishReadIfEntity);
        } else if (result instanceof Iterable) {
            ((Iterable<?>) result).forEach(this::publishReadIfEntity);
        } else {
            publishReadIfEntity(result);
        }
    }

    private void publishReadIfEntity(Object entity) {
        if (isEntity(entity)) {
            publisher.publishEvent(new ReadEvent<>(entity));
        }
    }

    private void publishWrite(Object entity, boolean isNew) {
        if (entity == null) {
            return;
        }
        if (isNew) {
            publisher.publishEvent(new CreateEvent<>(entity));
        } else {
            publisher.publishEvent(new UpdateEvent<>(entity));
        }
    }

    private boolean isNew(Object entity) {
        return entity != null && EntityUtils.findId(entity) == null;
    }

    private boolean isEntity(Object entity) {
        return entity != null && EntityUtils.findIdField(entity.getClass()).isPresent();
    }
}
