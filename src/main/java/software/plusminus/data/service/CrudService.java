package software.plusminus.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import software.plusminus.util.EntityUtils;

@SuppressWarnings("java:S119")
public interface CrudService<T, ID> {

    T getById(ID id);

    Page<T> getPage(Pageable pageable);

    T create(T object);

    T update(T object);

    T patch(T patch);

    void delete(T object);

    default T save(T object) {
        return EntityUtils.findId(object) == null
                ? create(object)
                : update(object);
    }
}
