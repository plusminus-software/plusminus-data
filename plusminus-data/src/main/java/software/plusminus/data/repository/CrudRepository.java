package software.plusminus.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.data.exception.NotFoundException;

import java.util.Optional;

@SuppressWarnings("java:S119")
@NoRepositoryBean
public interface CrudRepository<T, ID> extends Repository<T, ID> {

    @Transactional
    T save(T entity);

    @Transactional(readOnly = true)
    default T getById(ID id) throws NotFoundException {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Can't find object with id " + id));
    }

    @Transactional(readOnly = true)
    Optional<T> findById(ID id);

    @Transactional(readOnly = true)
    Page<T> findAll(Pageable pageable);

    @Transactional
    void delete(T entity);

}
