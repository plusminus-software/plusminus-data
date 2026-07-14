package software.plusminus.metadata;

import java.util.List;

public interface MetadataProvider {

    List<Class<?>> provideClasses();

}
