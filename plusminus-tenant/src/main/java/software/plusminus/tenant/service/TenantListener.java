package software.plusminus.tenant.service;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.plusminus.data.event.BeforeCreateEvent;
import software.plusminus.data.event.BeforeDeleteEvent;
import software.plusminus.data.event.BeforeUpdateEvent;
import software.plusminus.data.event.ReadEvent;
import software.plusminus.tenant.annotation.Tenant;
import software.plusminus.tenant.context.TenantContext;
import software.plusminus.tenant.exception.NotFoundException;
import software.plusminus.tenant.exception.TenantException;
import software.plusminus.util.FieldUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
@Component
public class TenantListener {

    private static final ThreadLocal<Boolean> DISABLE = new ThreadLocal<>();

    private TenantContext tenantContext;

    @EventListener
    public void onCreate(BeforeCreateEvent<?> event) {
        applyOnWrite(event.getEntity(), true);
    }

    @EventListener
    public void onUpdate(BeforeUpdateEvent<?> event) {
        applyOnWrite(event.getEntity(), false);
    }

    @EventListener
    public void onDelete(BeforeDeleteEvent<?> event) {
        applyOnWrite(event.getEntity(), false);
    }

    @EventListener
    public void onRead(ReadEvent<?> event) {
        Object object = event.getEntity();
        if (isDisabled()) {
            return;
        }
        Optional<Field> field = FieldUtils.findFirstWithAnnotation(object.getClass(), Tenant.class);
        if (!field.isPresent()) {
            return;
        }
        String objectTenant = FieldUtils.read(object, String.class, field.get());
        checkAccess(objectTenant, tenantContext.get(), true);
    }

    private void applyOnWrite(Object object, boolean create) {
        if (isDisabled()) {
            return;
        }
        Optional<Field> field = FieldUtils.findFirstWithAnnotation(object.getClass(), Tenant.class);
        if (!field.isPresent()) {
            return;
        }
        String objectTenant = FieldUtils.read(object, String.class, field.get());
        String contextTenant = tenantContext.get();
        if (objectTenant == null && contextTenant != null && create) {
            FieldUtils.write(object, contextTenant, field.get());
            return;
        }
        checkAccess(objectTenant, contextTenant, false);
    }

    private void checkAccess(String objectTenant, String contextTenant, boolean read) {
        String object = objectTenant == null ? "" : objectTenant;
        String context = contextTenant == null ? "" : contextTenant;
        if (!object.equals(context)) {
            if (read) {
                throw new NotFoundException();
            }
            throw new TenantException("Cannot perform action on object with tenant '" + object
                    + "' as the current tenant is '" + context + "'");
        }
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
