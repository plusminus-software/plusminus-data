package software.plusminus.dehydration.config;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.voodoodyne.jackson.jsog.JSOGRefSerializer;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.plusminus.dehydration.DehydrationContext;
import software.plusminus.dehydration.DehydrationSerializer;
import software.plusminus.util.AnnotationUtils;
import software.plusminus.util.ClassUtils;
import software.plusminus.util.FieldUtils;

import java.lang.reflect.Field;

@Configuration
@AllArgsConstructor
@ComponentScan("software.plusminus.dehydration")
public class DehydrationAutoconfig {

    private static final String HIBERNATE_PROXY_INTERFACE = "org.hibernate.proxy.HibernateProxy";
    private static final String ENTITY_ANNOTATION = "Entity";

    private DehydrationContext dehydrationContext;

    @Autowired
    public void jacksonConfiguration(ObjectMapper objectMapper) {
        objectMapper.registerModule(new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addBeanSerializerModifier(new BeanSerializerModifier() {
                    @Override
                    public JsonSerializer<?> modifySerializer(
                            SerializationConfig config, BeanDescription desc, JsonSerializer<?> serializer) {
                        Class<?> beanClass = desc.getBeanClass();
                        if (isHibernateProxy(beanClass)) {
                            beanClass = beanClass.getSuperclass();
                        }
                        if (AnnotationUtils.findAnnotation(ENTITY_ANNOTATION, beanClass) != null
                                && serializer instanceof BeanSerializer) {
                            BeanSerializer beanSerializer = (BeanSerializer) serializer;
                            fixBugWithJsog(beanSerializer);
                            return new DehydrationSerializer(dehydrationContext, beanSerializer);
                        }
                        return serializer;
                    }
                });
            }
        });
    }

    private boolean isHibernateProxy(Class<?> type) {
        return ClassUtils.getHierarchyWithInterfaces(type).stream()
                .anyMatch(c -> c.getName().equals(HIBERNATE_PROXY_INTERFACE));
    }

    private void fixBugWithJsog(BeanSerializer serializer) {
        ObjectIdWriter objectIdWriter = FieldUtils.readFirstWithType(serializer, ObjectIdWriter.class);
        if (objectIdWriter != null && objectIdWriter.serializer == null) {
            Field serializerField = FieldUtils.findFirstWithType(ObjectIdWriter.class, JsonSerializer.class)
                    .orElseThrow(IllegalStateException::new);
            FieldUtils.write(objectIdWriter, new JSOGRefSerializer(), serializerField);
        }
    }
}
