package software.plusminus.data.event;

public class UpdateEvent<E> extends DataEvent<E> {

    public UpdateEvent(E entity) {
        super(entity);
    }
}
