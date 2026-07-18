package software.plusminus.metadata;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface MetadataContext {

    @SuppressFBWarnings("MS_OOI_PKGPROTECT")
    Map<String, Class<?>> CLASS_MAP = new ConcurrentHashMap<>();

    @SuppressFBWarnings("MS_OOI_PKGPROTECT")
    Set<String> AMBIGUOUS_NAMES = ConcurrentHashMap.newKeySet();

    static <T> Class<T> getClass(String simpleName) {
        if (AMBIGUOUS_NAMES.contains(simpleName)) {
            throw new IllegalStateException("Simple class name '" + simpleName
                    + "' is ambiguous: multiple classes with this name are registered");
        }
        return (Class<T>) CLASS_MAP.get(simpleName);
    }

    static void addClass(Class<?> c) {
        Class<?> existing = CLASS_MAP.putIfAbsent(c.getSimpleName(), c);
        if (existing != null && !existing.equals(c)) {
            AMBIGUOUS_NAMES.add(c.getSimpleName());
        }
    }

}
