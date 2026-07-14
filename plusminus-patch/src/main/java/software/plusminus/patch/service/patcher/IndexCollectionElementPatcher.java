package software.plusminus.patch.service.patcher;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class IndexCollectionElementPatcher implements CollectionElementPatcher {

    @Nullable
    @Override
    public <T> Object toKey(Field field, Collection<T> collection, T element) {
        if (!List.class.isAssignableFrom(collection.getClass())) {
            return null;
        }
        return new ArrayList<>(collection).indexOf(element);
    }
}
