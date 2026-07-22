package software.plusminus.data.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@SuppressWarnings("java:S119")
@AllArgsConstructor
public class DataCrudRepository<T, ID> implements CrudRepository<T, ID> {

    private Class<T> type;
    private DataRepository dataRepository;

    @Override
    public T save(T entity) {
        return dataRepository.save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(dataRepository.getById(type, id));
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return dataRepository.findAll(type, pageable);
    }

    @Override
    public void delete(T entity) {
        dataRepository.delete(entity);
    }
}
