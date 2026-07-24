package software.plusminus.data.service.entity;

import java.util.Set;
import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
public interface EntityService {

    <T> Set<Object> findSubentities(T object);

    <T> Class<T> findClass(String type);

    @Nullable
    <ID> Class<ID> findIdType(Class<?> entityType);

}
