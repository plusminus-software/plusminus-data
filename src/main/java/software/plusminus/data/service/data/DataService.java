package software.plusminus.data.service.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SuppressWarnings("squid:S00119")
public interface DataService {
    
    // TODO should redirect call to type-specific service or type-specific repository (if exist),
    // use generic implementation otherwise

    <T, ID> T read(Class<T> type, ID id);

    <T> Page<T> read(Class<T> type, Pageable pageable);

    <T> T create(T object);

    <T> T update(T object);

    <T> T patch(T patch);

    <T> void delete(T object);

}
