package software.plusminus.audit.service;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import software.plusminus.audit.annotation.Auditable;
import software.plusminus.data.event.CreateEvent;
import software.plusminus.data.event.CrudAction;
import software.plusminus.data.event.DeleteEvent;
import software.plusminus.data.event.UpdateEvent;

@AllArgsConstructor
@Component
@ConditionalOnBean(AuditLogService.class)
public class AuditLogListener {

    private AuditLogService service;

    @EventListener
    public void onCreate(CreateEvent<?> event) {
        log(event.getEntity(), CrudAction.CREATE);
    }

    @EventListener
    public void onUpdate(UpdateEvent<?> event) {
        log(event.getEntity(), CrudAction.UPDATE);
    }

    @EventListener
    public void onDelete(DeleteEvent<?> event) {
        log(event.getEntity(), CrudAction.DELETE);
    }

    private void log(Object entity, CrudAction action) {
        if (AnnotationUtils.findAnnotation(entity.getClass(), Auditable.class) != null) {
            service.log(entity, action);
        }
    }
}
