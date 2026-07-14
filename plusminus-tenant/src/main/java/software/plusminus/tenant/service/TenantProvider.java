package software.plusminus.tenant.service;

import javax.annotation.Nullable;

public interface TenantProvider {

    @Nullable
    String currentTenant();

}
