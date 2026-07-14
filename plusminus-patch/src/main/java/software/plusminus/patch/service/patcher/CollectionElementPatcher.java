package software.plusminus.patch.service.patcher;

import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.Collection;

public interface CollectionElementPatcher {

    @Nullable
    <T> Object toKey(Field field, Collection<T> collection, T element);

    @Nullable
    default <T> T toValue(Field field, Collection<T> collection, T element) {
        return element;
    }

}
