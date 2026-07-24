package software.plusminus.json.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import software.plusminus.metadata.AmbiguousTypeException;
import software.plusminus.metadata.MetadataContext;

import java.io.IOException;

public class BeanIdResolver extends TypeIdResolverBase {

    private JavaType superType;

    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }

    @Override
    public String idFromValue(Object obj) {
        return idFromValueAndType(obj, obj.getClass());
    }

    @Override
    public String idFromValueAndType(Object obj, Class<?> subType) {
        return subType.getSimpleName();
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        if (superType.getRawClass().getSimpleName().equals(id)) {
            return superType;
        }
        Class<?> subclass;
        try {
            subclass = MetadataContext.getClass(id);
        } catch (AmbiguousTypeException e) {
            throw new InvalidTypeIdException(null, e.getMessage(), superType, id);
        }
        if (subclass == null) {
            throw new InvalidTypeIdException(null, "Unknown type id: " + id, superType, id);
        }
        subclass = reloadWithClassLoaderIfNeeded(superType.getRawClass().getClassLoader(), subclass);
        return context.constructSpecializedType(superType, subclass);
    }
    
    private Class<?> reloadWithClassLoaderIfNeeded(ClassLoader classLoader, Class<?> c) {
        if (c.getClassLoader() == classLoader) {
            return c;
        }
        try {
            return classLoader.loadClass(c.getName());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Can't reload " + c.getSimpleName()
                    + " class with classloader " + classLoader.getClass().getSimpleName());
        }
    }
}
