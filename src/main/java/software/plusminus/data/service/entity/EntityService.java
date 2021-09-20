package software.plusminus.data.service.entity;

import java.util.Set;
import javax.annotation.Nullable;

@SuppressWarnings("squid:S00119")
public interface EntityService {

    <T> Set<Object> findSubentities(T object);

    @Nullable
    <T> Class<T> findClass(String type);

    @Nullable
    <ID> Class<ID> findIdType(Class<?> entityType);

}
