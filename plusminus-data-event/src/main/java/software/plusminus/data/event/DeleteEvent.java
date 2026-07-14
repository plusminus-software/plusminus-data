package software.plusminus.data.event;

public class DeleteEvent<E> extends DataEvent<E> {

    public DeleteEvent(E entity) {
        super(entity);
    }
}
