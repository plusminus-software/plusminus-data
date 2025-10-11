package software.plusminus.listener;

public interface WriteListener<T> extends CreateListener<T>, UpdateListener<T>, DeleteListener<T> {

    @Override
    default boolean supports(T object) {
        return true;
    }

    @Override
    default JoinPoint joinPoint() {
        return JoinPoint.BEFORE;
    }

    @Override
    default void onCreate(T object) {
        onWrite(object, DataAction.CREATE);
    }

    @Override
    default void onUpdate(T object) {
        onWrite(object, DataAction.UPDATE);
    }

    @Override
    default void onPatch(T object) {
        onWrite(object, DataAction.PATCH);
    }

    @Override
    default void onDelete(T object) {
        onWrite(object, DataAction.DELETE);
    }

    void onWrite(T object, DataAction action);

}
