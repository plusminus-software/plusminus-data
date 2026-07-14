package software.plusminus.tenant.service;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import software.plusminus.context.Context;
import software.plusminus.security.Security;

@Order(1)
@AllArgsConstructor
@ConditionalOnClass(Security.class)
@Component
public class SecurityContextTenantProvider implements TenantProvider {

    private Context<Security> securityContext;
    
    @Nullable
    @Override
    public String currentTenant() {
        return securityContext.optional()
                .map(Security::getParameters)
                .map(parameters -> parameters.get("tenant"))
                .orElse(null);
    }
}
