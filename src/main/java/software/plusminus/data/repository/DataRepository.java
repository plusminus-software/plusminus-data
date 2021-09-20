package software.plusminus.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SuppressWarnings("squid:S00119")
public interface DataRepository {

    <T, ID> T findById(Class<T> type, ID id);

    <T> Page<T> findAll(Class<T> type, Pageable pageable);

    <T> T save(T entity);

    <T> T deepSave(T entity);

    <T> void delete(T entity);

}
