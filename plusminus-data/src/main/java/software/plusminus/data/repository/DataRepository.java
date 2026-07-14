package software.plusminus.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
public interface DataRepository {

    @Nullable
    <T, ID> T getById(Class<T> type, ID id);

    <T> Page<T> findAll(Class<T> type, Pageable pageable);

    <T> T save(T entity);

    <T> void delete(T entity);

}
