package software.plusminus.data.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
@AllArgsConstructor
public class SpringCrudRepository<T, ID> implements CrudRepository<T, ID> {

    private PagingAndSortingRepository<T, ID> repository;

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Nullable
    @Override
    public T getById(ID id) {
        return repository.findById(id)
                .orElse(null);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }
}
