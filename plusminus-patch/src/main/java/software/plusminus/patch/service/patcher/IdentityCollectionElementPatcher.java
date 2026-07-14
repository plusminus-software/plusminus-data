package software.plusminus.patch.service.patcher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;

@Component
@Order
@ConditionalOnProperty("data.patch.collection.identity")
public class IdentityCollectionElementPatcher implements CollectionElementPatcher {

    @Nullable
    @Override
    public <T> Object toKey(Field field, Collection<T> collection, T element) {
        return element;
    }
}
