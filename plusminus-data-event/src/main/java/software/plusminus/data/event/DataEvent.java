package software.plusminus.data.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * {@code BeforeCreateEvent}/{@code BeforeUpdateEvent}/{@code BeforeDeleteEvent} fire before the write:
 * the entity is detached and has no id on create - mutate or veto here.
 * {@code CreateEvent}/{@code UpdateEvent}/{@code DeleteEvent}/{@code ReadEvent} fire after it happened:
 * the entity is managed and has an id - mutating here costs an extra statement and is ignored
 * for non updatable columns.
 *
 * @param <E> the entity type
 */
@Getter
public abstract class DataEvent<E> extends ApplicationEvent implements ResolvableTypeProvider {

    private transient E entity;

    protected DataEvent(E entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(entity));
    }
}
