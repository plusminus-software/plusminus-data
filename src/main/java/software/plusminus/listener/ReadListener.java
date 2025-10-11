package software.plusminus.listener;

public interface ReadListener<T> {

    default boolean supports(T object) {
        return true;
    }

    void onRead(T object);

}
