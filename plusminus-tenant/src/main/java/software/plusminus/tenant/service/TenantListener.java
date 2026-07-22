package software.plusminus.tenant.service;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import software.plusminus.data.event.BeforeCreateEvent;
import software.plusminus.data.event.BeforeDeleteEvent;
import software.plusminus.data.event.BeforeUpdateEvent;
import software.plusminus.data.event.CrudAction;
import software.plusminus.data.event.ReadEvent;
import software.plusminus.tenant.annotation.Tenant;
import software.plusminus.tenant.context.TenantContext;
import software.plusminus.tenant.exception.NotFoundException;
import software.plusminus.tenant.exception.TenantException;
import software.plusminus.util.FieldUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
@Component
public class TenantListener {

    private static final ThreadLocal<Boolean> DISABLE = new ThreadLocal<>();

    private TenantContext tenantContext;

    @EventListener
    public void onCreate(BeforeCreateEvent<?> event) {
        onAction(event.getEntity(), CrudAction.CREATE);
    }

    @EventListener
    public void onUpdate(BeforeUpdateEvent<?> event) {
        onAction(event.getEntity(), CrudAction.UPDATE);
    }

    @EventListener
    public void onDelete(BeforeDeleteEvent<?> event) {
        onAction(event.getEntity(), CrudAction.DELETE);
    }

    @EventListener
    public void onRead(ReadEvent<?> event) {
        onAction(event.getEntity(), CrudAction.READ);
    }

    private void onAction(Object object, CrudAction action) {
        if (isDisabled()) {
            return;
        }
        Optional<Field> field = FieldUtils.findFirstWithAnnotation(object.getClass(), Tenant.class);
        if (!field.isPresent()) {
            return;
        }
        checkAccess(object, field.get(), action);
    }

    private void checkAccess(Object object, Field field, CrudAction action) {
        String objectTenant = FieldUtils.read(object, String.class, field);
        String contextTenant = tenantContext.get();
        if (objectTenant == null && contextTenant != null && action != CrudAction.READ) {
            FieldUtils.write(object, contextTenant, field);
            return;
        }
        //TODO check that entity in DB has the same tenant
        if (!Objects.equals(safeString(objectTenant), safeString(contextTenant))) {
            if (action == CrudAction.READ) {
                throw new NotFoundException();
            }
            throw new TenantException("Cannot perform action " + action
                    + " on object " + object
                    + " with tenant '" + objectTenant
                    + "' as the current tenant is '" + contextTenant + "'");
        }
    }

    private String safeString(@Nullable String string) {
        return string == null ? "" : string;
    }

    private boolean isDisabled() {
        return Boolean.TRUE.equals(DISABLE.get());
    }

    public void runWithoutTenantCheck(Runnable run) {
        DISABLE.set(Boolean.TRUE);
        try {
            run.run();
        } finally {
            DISABLE.remove();
        }
    }

    public <T> T callWithoutTenantCheck(Supplier<T> run) {
        DISABLE.set(Boolean.TRUE);
        try {
            return run.get();
        } finally {
            DISABLE.remove();
        }
    }
}
