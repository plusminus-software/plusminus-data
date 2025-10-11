package software.plusminus.listener;

public interface DeleteListener<T> {

    default boolean supports(T object) {
        return true;
    }

    default JoinPoint joinPoint() {
        return JoinPoint.BEFORE;
    }

    void onDelete(T object);

}
