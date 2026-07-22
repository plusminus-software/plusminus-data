package software.plusminus.data.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

@SuppressWarnings("java:S119")
@AllArgsConstructor
public class SpringCrudRepository<T, ID> implements CrudRepository<T, ID> {

    private PagingAndSortingRepository<T, ID> repository;

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
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
