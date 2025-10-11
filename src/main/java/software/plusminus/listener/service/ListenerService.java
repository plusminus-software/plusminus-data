package software.plusminus.listener.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;
import software.plusminus.listener.CreateListener;
import software.plusminus.listener.DeleteListener;
import software.plusminus.listener.JoinPoint;
import software.plusminus.listener.ReadListener;
import software.plusminus.listener.UpdateListener;
import software.plusminus.listener.exception.ListenerInitializationException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ListenerService {

    private Map<Class<?>, List<ReadListener<?>>> readListeners;
    private Map<Class<?>, List<CreateListener<?>>> createListeners;
    private Map<Class<?>, List<UpdateListener<?>>> updateListeners;
    private Map<Class<?>, List<DeleteListener<?>>> deleteListeners;

    @Autowired
    void init(ObjectProvider<ReadListener<?>> readListenersList,
              ObjectProvider<CreateListener<?>> createListenersList,
              ObjectProvider<UpdateListener<?>> updateListenersList,
              ObjectProvider<DeleteListener<?>> deleteListenersList) {

        this.readListeners = toMap(readListenersList.orderedStream()
                .collect(Collectors.toList()), ReadListener.class);
        this.createListeners = toMap(createListenersList.orderedStream()
                .collect(Collectors.toList()), CreateListener.class);
        this.updateListeners = toMap(updateListenersList.orderedStream()
                .collect(Collectors.toList()), UpdateListener.class);
        this.deleteListeners = toMap(deleteListenersList.orderedStream()
                .collect(Collectors.toList()), DeleteListener.class);
    }

    public <T> void afterRead(T object) {
        readListeners.entrySet().stream()
                .filter(e -> e.getKey().isAssignableFrom(object.getClass()))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .map(listener -> (ReadListener<? super T>) listener)
                .filter(listener -> listener.supports(object))
                .forEach(listener -> listener.onRead(object));
    }

    public <T> void beforeCreate(T object) {
        onCreate(object, JoinPoint.BEFORE);
    }

    public <T> void afterCreate(T object) {
        onCreate(object, JoinPoint.AFTER);
    }

    public <T> void beforeUpdate(T object) {
        onUpdate(object, JoinPoint.BEFORE);
    }

    public <T> void afterUpdate(T object) {
        onUpdate(object, JoinPoint.AFTER);
    }

    public <T> void beforePatch(T object) {
        onPatch(object, JoinPoint.BEFORE);
    }

    public <T> void afterPatch(T object) {
        onPatch(object, JoinPoint.AFTER);
    }

    public <T> void beforeDelete(T object) {
        onDelete(object, JoinPoint.BEFORE);
    }

    public <T> void afterDelete(T object) {
        onDelete(object, JoinPoint.AFTER);
    }

    private <T, L extends T> Map<Class<?>, List<L>> toMap(List<L> listeners, Class<T> serviceLevelType) {
        return listeners.stream()
                .collect(Collectors.groupingBy(listener -> getGenericType(listener, serviceLevelType)));
    }

    private <T> void onCreate(T object, JoinPoint joinPoint) {
        createListeners.entrySet().stream()
                .filter(e -> e.getKey().isAssignableFrom(object.getClass()))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .filter(listener -> listener.joinPoint() == joinPoint)
                .map(listener -> (CreateListener<? super T>) listener)
                .filter(listener -> listener.supports(object))
                .forEach(listener -> listener.onCreate(object));
    }

    private <T> void onUpdate(T object, JoinPoint joinPoint) {
        updateListenersStream(object, joinPoint)
                .forEach(listener -> listener.onUpdate(object));
    }

    private <T> void onPatch(T object, JoinPoint joinPoint) {
        updateListenersStream(object, joinPoint)
                .forEach(listener -> listener.onPatch(object));
    }

    private <T> void onDelete(T object, JoinPoint joinPoint) {
        deleteListeners.entrySet().stream()
                .filter(e -> e.getKey().isAssignableFrom(object.getClass()))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .filter(listener -> listener.joinPoint() == joinPoint)
                .map(listener -> (DeleteListener<? super T>) listener)
                .filter(listener -> listener.supports(object))
                .forEach(listener -> listener.onDelete(object));
    }

    private <T, L extends T> Class<?> getGenericType(L listener, Class<T> listenerType) {
        ResolvableType type = ResolvableType.forClass(listener.getClass())
                .as(listenerType);
        Class<?> genericType = type.getGeneric(0).resolve();
        if (genericType == null) {
            throw new ListenerInitializationException("Cannot initialize listener: " + listener.getClass());
        }
        return genericType;
    }

    private <T> Stream<UpdateListener<? super T>> updateListenersStream(T object, JoinPoint joinPoint) {
        return updateListeners.entrySet().stream()
                .filter(e -> e.getKey().isAssignableFrom(object.getClass()))
                .map(Map.Entry::getValue)
                .flatMap(List::stream)
                .filter(listener -> listener.joinPoint() == joinPoint)
                .map(listener -> (UpdateListener<? super T>) listener)
                .filter(l -> l.supports(object))
                .map(listener -> (UpdateListener<? super T>) listener);
    }
}
