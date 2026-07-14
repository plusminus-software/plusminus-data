package software.plusminus.data.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

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
