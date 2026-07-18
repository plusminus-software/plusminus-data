package software.plusminus.patch.service.patcher;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import software.plusminus.patch.annotation.StringCollectionPatch;
import software.plusminus.patch.exception.PatchException;

import java.lang.reflect.Field;
import java.util.Collection;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 2)
public class StringCollectionElementPatcher implements CollectionElementPatcher {

    @Nullable
    @Override
    public <T> String toKey(Field field, Collection<T> collection, T element) {
        if (element.getClass() != String.class) {
            return null;
        }

        StringCollectionPatch annotation = field.getAnnotation(StringCollectionPatch.class);
        if (annotation == null) {
            return null;
        }

        String[] parts = String.class.cast(element).split(annotation.splitter());
        if (annotation.index() >= parts.length) {
            throw new PatchException("Cannot extract key with index " + annotation.index()
                    + " from element '" + element + "' using splitter '" + annotation.splitter() + "'");
        }
        return parts[annotation.index()];
    }

    @Nullable
    @Override
    public <T> T toValue(Field field, Collection<T> collection, T element) {
        if (element.getClass() != String.class) {
            throw new PatchException("element must be String type");
        }

        StringCollectionPatch annotation = field.getAnnotation(StringCollectionPatch.class);
        if (annotation == null) {
            throw new PatchException("Field must be annotated by @StringCollectionPatch annotation");
        }

        if (String.class.cast(element).split(annotation.splitter()).length <= 1) {
            return null;
        }
        return element;
    }
}
