package software.plusminus.data.event;

public class BeforeDeleteEvent<E> extends DataEvent<E> {

    public BeforeDeleteEvent(E entity) {
        super(entity);
    }
}
