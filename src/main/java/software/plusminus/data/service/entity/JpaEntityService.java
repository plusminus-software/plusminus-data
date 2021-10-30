package software.plusminus.data.service.entity;

import org.springframework.stereotype.Service;
import software.plusminus.data.exception.NotFoundException;
import software.plusminus.util.FieldUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.ManagedType;

@Service
@SuppressWarnings("squid:S00119")
public class JpaEntityService implements EntityService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public <T> Set<Object> findSubentities(T object) {
        return FieldUtils.getDeepFieldValues(object,
                field -> Stream.of(Entity.class, Embeddable.class)
                        .anyMatch(annotation -> field.getType().isAnnotationPresent(annotation))
                        || Collection.class.isAssignableFrom(field.getType()))
                .stream()
                .filter(value -> value.getClass().isAnnotationPresent(Entity.class))
                .collect(Collectors.toSet());
    }

    @Override
    @Nullable
    public <T> Class<T> findClass(String type) {
        return entityManager.getMetamodel().getManagedTypes().stream()
                .map(ManagedType::getJavaType)
                .filter(clazz -> clazz.getSimpleName().equals(type))
                .findFirst()
                .map(clazz -> (Class<T>) clazz)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Nullable
    public <ID> Class<ID> findIdType(Class<?> entityType) {
        return Stream.of(
                FieldUtils.findFirstWithAnnotation(entityType, Id.class),
                FieldUtils.findFirstWithAnnotation(entityType, org.springframework.data.annotation.Id.class))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get)
                .map(Field::getType)
                .map(type -> (Class<ID>) type)
                .orElse(null);
    }
}