package software.plusminus.data.event;

public class BeforeCreateEvent<E> extends DataEvent<E> {

    public BeforeCreateEvent(E entity) {
        super(entity);
    }
}
