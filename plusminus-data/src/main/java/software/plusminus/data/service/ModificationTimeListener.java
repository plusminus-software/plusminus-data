package software.plusminus.data.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.plusminus.data.annotation.ModificationTime;
import software.plusminus.data.event.BeforeCreateEvent;
import software.plusminus.data.event.BeforeUpdateEvent;
import software.plusminus.data.util.TemporalUtil;
import software.plusminus.util.FieldUtils;

@Component
public class ModificationTimeListener {

    @EventListener
    public void onCreate(BeforeCreateEvent<?> event) {
        populate(event.getEntity());
    }

    @EventListener
    public void onUpdate(BeforeUpdateEvent<?> event) {
        populate(event.getEntity());
    }

    private void populate(Object object) {
        FieldUtils.getFieldsStream(object.getClass())
                .filter(field -> field.isAnnotationPresent(ModificationTime.class))
                .forEach(field -> FieldUtils.write(object, TemporalUtil.now(field.getType()), field));
    }
}
