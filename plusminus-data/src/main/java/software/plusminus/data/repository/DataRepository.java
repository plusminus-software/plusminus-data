package software.plusminus.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.data.exception.NotFoundException;

import java.util.Optional;

@SuppressWarnings("java:S119")
public interface DataRepository {

    @Transactional(readOnly = true)
    default <T, ID> T getById(Class<T> type, ID id) throws NotFoundException {
        return findById(type, id)
                .orElseThrow(() -> new NotFoundException("Can't find object with id " + id));
    }

    @Transactional(readOnly = true)
    <T, ID> Optional<T> findById(Class<T> type, ID id);

    @Transactional(readOnly = true)
    <T> Page<T> findAll(Class<T> type, Pageable pageable);

    @Transactional
    <T> T save(T entity);

    @Transactional
    <T> void delete(T entity);

}
