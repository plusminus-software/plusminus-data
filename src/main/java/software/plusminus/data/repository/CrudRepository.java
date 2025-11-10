package software.plusminus.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
@NoRepositoryBean
public interface CrudRepository<T, ID> extends Repository<T, ID> {

    @Transactional
    T save(T entity);

    @Nullable
    @Transactional(readOnly = true)
    T getById(ID id);

    @Transactional(readOnly = true)
    Page<T> findAll(Pageable pageable);

    @Transactional
    void delete(T entity);

}
