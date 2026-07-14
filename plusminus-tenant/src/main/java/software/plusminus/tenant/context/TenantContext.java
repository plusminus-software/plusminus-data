package software.plusminus.tenant.context;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.context.Context;
import software.plusminus.tenant.service.TenantProvider;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Component
public class TenantContext implements Context<String> {

    private List<TenantProvider> providers;

    @Override
    public String get() {
        return providers.stream()
                .map(TenantProvider::currentTenant)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
