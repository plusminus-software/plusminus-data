package software.plusminus.json.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import software.plusminus.metadata.MetadataContext;

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
    public JavaType typeFromId(DatabindContext context, String id) {
        if (superType.getRawClass().getSimpleName().equals(id)) {
            return superType;
        }
        Class<?> subclass = MetadataContext.getClass(id);
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
