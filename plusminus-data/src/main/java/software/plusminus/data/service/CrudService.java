package software.plusminus.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.util.EntityUtils;

@SuppressWarnings("java:S119")
public interface CrudService<T, ID> {

    @Transactional(readOnly = true)
    T getById(ID id);

    @Transactional(readOnly = true)
    Page<T> getPage(Pageable pageable);

    @Transactional
    T create(T object);

    @Transactional
    T update(T object);

    @Transactional
    T patch(T patch);

    @Transactional
    void delete(T object);

    @Transactional
    default T save(T object) {
        return EntityUtils.findId(object) == null
                ? create(object)
                : update(object);
    }
}
