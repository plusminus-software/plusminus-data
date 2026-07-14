package software.plusminus.patch.service.patcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import software.plusminus.patch.annotation.CollectionPatch;
import software.plusminus.patch.exception.PatchException;
import software.plusminus.util.FieldUtils;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class CollectionPatcher implements Patcher {

    @Autowired
    private List<CollectionElementPatcher> patchers;

    @Override
    public <T> void patch(T source, T target) {
        FieldUtils.getFieldsStream(source.getClass())
                .filter(field -> AnnotationUtils.findAnnotation(field, CollectionPatch.class) != null)
                .peek(field -> {
                    if (!Collection.class.isAssignableFrom(field.getType())) {
                        throw new PatchException(
                                "Field annotated with @CollectionPatch annotation must extend Collection interface");
                    }
                })
                .forEach(field -> processCollectionField(field, source, target));
    }

    private <T> void processCollectionField(Field collectionField, T source, T target) {
        Collection<T> sourceCollection = FieldUtils.read(source, Collection.class, collectionField);
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return;
        }

        Collection<T> targetCollection = FieldUtils.read(target, Collection.class, collectionField);
        if (CollectionUtils.isEmpty(targetCollection)) {
            return;
        }

        Collection<T> mergedCollection = getMergedCollection(collectionField, sourceCollection, targetCollection);
        Collection<T> wrappedCollection = wrapCollection(sourceCollection, mergedCollection);
        FieldUtils.write(source, wrappedCollection, collectionField);
    }

    private <T> Collection<T> getMergedCollection(Field collectionField,
                                                  Collection<T> sourceCollection,
                                                  Collection<T> targetCollection) {

        Map<Object, T> sourceMap = convertCollectionToMap(collectionField, sourceCollection);
        Map<Object, T> targetMap = convertCollectionToMap(collectionField, targetCollection);

        sourceMap.forEach((key, value) -> {
            if (value == null) {
                targetMap.remove(key);
            } else {
                targetMap.put(key, value);
            }
        });
        return targetMap.values();
    }

    private <T> Map<Object, T> convertCollectionToMap(Field collectionField,
                                                      Collection<T> collection) {
        /* Stream API is not used here because Collectors.toMap throws NPE in case value is null */
        Map<Object, T> result = new LinkedHashMap<>();

        collection.stream()
                .map(element -> getKeyValue(collectionField, collection, element))
                .forEach(entry -> {
                    if (result.containsKey(entry.getKey())) {
                        throw new IllegalStateException(String.format("Duplicate key %s", entry.getKey()));
                    }
                    result.put(entry.getKey(), entry.getValue());
                });
        return result;
    }

    private <T> Map.Entry<Object, T> getKeyValue(Field field, Collection<T> collection, T element) {
        return patchers.stream()
                .map(patcher -> {
                    Object key = patcher.toKey(field, collection, element);
                    if (key == null) {
                        return null;
                    }
                    return new AbstractMap.SimpleEntry<>(key, patcher);
                })
                .filter(Objects::nonNull)
                .findFirst()
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(), entry.getValue().toValue(field, collection, element)))
                .orElseThrow(PatchException::new);
    }

    private <T> Collection<T> wrapCollection(Collection<T> sourceCollection, Collection<T> newCollection) {
        if (sourceCollection.getClass().isAssignableFrom(newCollection.getClass())) {
            return newCollection;
        }
        try {
            Collection<T> wrapCollection = sourceCollection.getClass().newInstance();
            wrapCollection.addAll(newCollection);
            return wrapCollection;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new PatchException(e);
        }
    }
}
