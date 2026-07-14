package software.plusminus.metadata;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.HashMap;
import java.util.Map;

public interface MetadataContext {

    @SuppressFBWarnings("MS_OOI_PKGPROTECT")
    Map<String, Class<?>> CLASS_MAP = new HashMap<>();

    static <T> Class<T> getClass(String simpleName) {
        return (Class<T>) CLASS_MAP.get(simpleName);
    }

    static void addClass(Class<?> c) {
        CLASS_MAP.putIfAbsent(c.getSimpleName(), c);
    }

}
