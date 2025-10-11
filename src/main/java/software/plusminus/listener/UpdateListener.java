package software.plusminus.listener;

public interface UpdateListener<T> {

    default boolean supports(T object) {
        return true;
    }

    default JoinPoint joinPoint() {
        return JoinPoint.BEFORE;
    }

    void onUpdate(T object);

    void onPatch(T object);

}
