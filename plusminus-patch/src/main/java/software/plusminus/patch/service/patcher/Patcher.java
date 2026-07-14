package software.plusminus.patch.service.patcher;

public interface Patcher {

    <T> void patch(T source, T target);

}
