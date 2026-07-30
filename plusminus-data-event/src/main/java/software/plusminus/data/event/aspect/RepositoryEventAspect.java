package software.plusminus.data.event.aspect;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import software.plusminus.data.event.DataEventPublisher;
import software.plusminus.data.event.service.TransactionService;
import software.plusminus.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Publishes data events around Spring Data repository calls.
 *
 * <p>Write operations and the events published around them run in a single transaction,
 * which is started if the caller has none - see {@link TransactionService}. Without spring-tx
 * on the classpath that bean is absent and the writes proceed as before, untransacted.
 *
 * <p>Bulk operations that receive neither entities nor ids
 * ({@code deleteAll()}, {@code deleteAllInBatch()}, {@code deleteInBatch(...)})
 * do not publish events.
 */
@Aspect
@Component
@AllArgsConstructor
public class RepositoryEventAspect {

    private DataEventPublisher publisher;
    private Optional<TransactionService> transactionService;

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

    @Pointcut("execution(* org.springframework.data.repository.Repository+.deleteById(..))")
    void deleteById() {
        // pointcut declaration
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.deleteAllById(..))")
    void deleteAllById() {
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
        return inTransaction(() -> {
            boolean isNew = publishBeforeWrite(entity);
            Object saved = proceed(joinPoint);
            publishWrite(saved, isNew);
            return saved;
        });
    }

    @Around("saveAll() && args(entities)")
    public Object aroundSaveAll(ProceedingJoinPoint joinPoint, Iterable<?> entities) {
        return inTransaction(() -> {
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
        });
    }

    @Around("delete() && args(entity)")
    public Object aroundDelete(ProceedingJoinPoint joinPoint, Object entity) {
        return inTransaction(() -> {
            publisher.publishBeforeDelete(entity);
            Object result = proceed(joinPoint);
            publisher.publishDelete(entity);
            return result;
        });
    }

    @Around("deleteAll() && args(entities)")
    public Object aroundDeleteAll(ProceedingJoinPoint joinPoint, Iterable<?> entities) {
        return inTransaction(() -> {
            entities.forEach(publisher::publishBeforeDelete);
            Object result = proceed(joinPoint);
            entities.forEach(publisher::publishDelete);
            return result;
        });
    }

    @Around("deleteById() && args(id)")
    public Object aroundDeleteById(ProceedingJoinPoint joinPoint, Object id) {
        return inTransaction(() -> {
            Object entity = findEntity(joinPoint.getTarget(), id);
            publisher.publishBeforeDelete(entity);
            Object result = proceed(joinPoint);
            publisher.publishDelete(entity);
            return result;
        });
    }

    @Around("deleteAllById() && args(ids)")
    public Object aroundDeleteAllById(ProceedingJoinPoint joinPoint, Iterable<?> ids) {
        return inTransaction(() -> {
            List<Object> entities = new ArrayList<>();
            ids.forEach(id -> {
                Object entity = findEntity(joinPoint.getTarget(), id);
                if (entity != null) {
                    entities.add(entity);
                }
            });
            entities.forEach(publisher::publishBeforeDelete);
            Object result = proceed(joinPoint);
            entities.forEach(publisher::publishDelete);
            return result;
        });
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

    private <T> T inTransaction(Supplier<T> write) {
        if (!transactionService.isPresent()) {
            return write.get();
        }
        return transactionService.get().run(write);
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

    @SuppressWarnings("unchecked")
    private Object findEntity(Object repository, Object id) {
        if (!(repository instanceof CrudRepository)) {
            return null;
        }
        return ((CrudRepository<?, Object>) repository).findById(id).orElse(null);
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
