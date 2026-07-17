package software.plusminus.data.event;

public class BeforeUpdateEvent<E> extends DataEvent<E> {

    public BeforeUpdateEvent(E entity) {
        super(entity);
    }
}
