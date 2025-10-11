package software.plusminus.crud.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import software.plusminus.data.service.DataService;

@SuppressWarnings("java:S119")
@AllArgsConstructor
public class DataCrudService<T, ID> implements CrudService<T, ID> {

    private Class<T> type;
    private DataService dataService;

    @Override
    public T getById(ID id) {
        return dataService.getById(type, id);
    }

    @Override
    public Page<T> getPage(Pageable pageable) {
        return dataService.getPage(type, pageable);
    }

    @Override
    public T create(T object) {
        return dataService.create(object);
    }

    @Override
    public T update(T object) {
        return dataService.update(object);
    }

    @Override
    public T patch(T patch) {
        return dataService.patch(patch);
    }

    @Override
    public void delete(T object) {
        dataService.delete(object);
    }
}
