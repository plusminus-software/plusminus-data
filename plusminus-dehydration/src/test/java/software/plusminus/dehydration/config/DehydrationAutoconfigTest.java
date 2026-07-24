package software.plusminus.dehydration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;
import software.plusminus.dehydration.DehydrationContext;
import software.plusminus.dehydration.fixtures.A;
import software.plusminus.dehydration.fixtures.AProxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

public class DehydrationAutoconfigTest {

    private DehydrationContext context = mock(DehydrationContext.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void before() {
        new DehydrationAutoconfig(context).jacksonConfiguration(mapper);
    }

    @Test
    public void serializesEntityDehydrated() throws Exception {
        when(context.shouldDehydrate()).thenReturn(true);
        A entity = new A();
        entity.setId(1L);
        UUID uuid = UUID.randomUUID();
        entity.setUuid(uuid);

        String json = mapper.writeValueAsString(entity);

        check(json).isJson().is("{\":dehydrated\":true,\"class\":\"A\",\"id\":1,\"uuid\":\"" + uuid + "\"}");
    }

    @Test
    public void serializesHibernateProxyClassDehydrated() throws Exception {
        when(context.shouldDehydrate()).thenReturn(true);
        AProxy proxy = new AProxy();
        proxy.setId(2L);
        UUID uuid = UUID.randomUUID();
        proxy.setUuid(uuid);

        String json = mapper.writeValueAsString(proxy);

        check(json).isJson().is("{\":dehydrated\":true,\"class\":\"AProxy\",\"id\":2,\"uuid\":\"" + uuid + "\"}");
    }

    @Test
    public void serializesNonEntityWithDefaultSerializer() throws Exception {
        PlainObject plainObject = new PlainObject();

        String json = mapper.writeValueAsString(plainObject);

        check(json).isJson().is("{\"name\":\"test\"}");
    }

    @Test
    @SuppressFBWarnings("DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED")
    public void configuresSerializationWhenJpaAndHibernateAreNotOnClasspath() throws Exception {
        ClassLoader noJpaClassLoader = new NoJpaClassLoader();
        Class<?> configClass = noJpaClassLoader.loadClass(DehydrationAutoconfig.class.getName());
        Class<?> contextClass = noJpaClassLoader.loadClass(DehydrationContext.class.getName());
        Object config = configClass.getDeclaredConstructor(contextClass).newInstance((Object) null);
        ObjectMapper isolatedMapper = new ObjectMapper();
        configClass.getMethod("jacksonConfiguration", ObjectMapper.class).invoke(config, isolatedMapper);

        String json = isolatedMapper.writeValueAsString(new PlainObject());

        check(json).isJson().is("{\"name\":\"test\"}");
    }

    public static class PlainObject {

        public String getName() {
            return "test";
        }
    }

    /* Mimics an application classpath that has no JPA/Hibernate jars: the dehydration classes
       are redefined in this loader, so their references to org.hibernate.* or javax.persistence.*
       would fail with NoClassDefFoundError. */
    private static class NoJpaClassLoader extends ClassLoader {

        NoJpaClassLoader() {
            super(DehydrationAutoconfigTest.class.getClassLoader());
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("org.hibernate.") || name.startsWith("javax.persistence.")) {
                throw new ClassNotFoundException(name);
            }
            if (name.startsWith("software.plusminus.dehydration.")) {
                return findDehydrationClass(name);
            }
            return super.loadClass(name, resolve);
        }

        private Class<?> findDehydrationClass(String name) throws ClassNotFoundException {
            Class<?> loaded = findLoadedClass(name);
            if (loaded != null) {
                return loaded;
            }
            String resource = name.replace('.', '/') + ".class";
            try (InputStream inputStream = getParent().getResourceAsStream(resource)) {
                if (inputStream == null) {
                    throw new ClassNotFoundException(name);
                }
                byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }
    }
}
