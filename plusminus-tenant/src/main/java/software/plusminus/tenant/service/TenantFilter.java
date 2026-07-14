package software.plusminus.tenant.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.hibernate.HibernateFilter;
import software.plusminus.tenant.context.TenantContext;

import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
@Component
public class TenantFilter implements HibernateFilter {

    private TenantContext tenantContext;

    @Override
    public String filterName() {
        return "tenantFilter";
    }

    @Override
    public Map<String, Object> parameters() {
        String tenant = tenantContext.get();
        return Collections.singletonMap("tenant", tenant == null ? "" : tenant);
    }
}
