package software.plusminus.listener;

public interface CreateListener<T> {

    default boolean supports(T object) {
        return true;
    }

    default JoinPoint joinPoint() {
        return JoinPoint.BEFORE;
    }

    void onCreate(T object);

}
