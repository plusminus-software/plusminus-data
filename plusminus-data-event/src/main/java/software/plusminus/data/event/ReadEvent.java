package software.plusminus.data.event;

public class ReadEvent<E> extends DataEvent<E> {

    public ReadEvent(E entity) {
        super(entity);
    }
}
