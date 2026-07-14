package software.plusminus.data.event;

public class CreateEvent<E> extends DataEvent<E> {

    public CreateEvent(E entity) {
        super(entity);
    }
}
