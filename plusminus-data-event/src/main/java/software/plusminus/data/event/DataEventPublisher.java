package software.plusminus.data.event;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import software.plusminus.util.EntityUtils;

import java.util.function.Function;

@AllArgsConstructor
@Component
public class DataEventPublisher {

    private ApplicationEventPublisher publisher;

    public void publishBeforeCreate(Object entity) {
        publishWrite(entity, BeforeCreateEvent::new);
    }

    public void publishCreate(Object entity) {
        publishWrite(entity, CreateEvent::new);
    }

    public void publishBeforeUpdate(Object entity) {
        publishWrite(entity, BeforeUpdateEvent::new);
    }

    public void publishUpdate(Object entity) {
        publishWrite(entity, UpdateEvent::new);
    }

    public void publishBeforeDelete(Object entity) {
        publishWrite(entity, BeforeDeleteEvent::new);
    }

    public void publishDelete(Object entity) {
        publishWrite(entity, DeleteEvent::new);
    }

    public void publishRead(Object entity) {
        if (entity != null && EntityUtils.findIdField(entity.getClass()).isPresent()) {
            publisher.publishEvent(new ReadEvent<>(entity));
        }
    }

    private void publishWrite(Object entity, Function<Object, DataEvent<Object>> eventFactory) {
        if (entity != null) {
            publisher.publishEvent(eventFactory.apply(entity));
        }
    }
}
