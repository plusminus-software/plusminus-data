package software.plusminus.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SuppressWarnings("java:S119")
public interface CrudService<T, ID> {

    T getById(ID id);

    Page<T> getPage(Pageable pageable);

    T create(T object);

    T update(T object);

    T patch(T patch);

    void delete(T object);

}
