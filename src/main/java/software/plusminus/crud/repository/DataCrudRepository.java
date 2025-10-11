package software.plusminus.crud.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import software.plusminus.data.repository.DataRepository;

import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
@AllArgsConstructor
public class DataCrudRepository<T, ID> implements CrudRepository<T, ID> {

    private Class<T> type;
    private DataRepository dataRepository;

    @Override
    public T save(T entity) {
        return dataRepository.save(entity);
    }

    @Nullable
    @Override
    public T getById(ID id) {
        return dataRepository.getById(type, id);
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
