package software.plusminus.tenant.config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import software.plusminus.tenant.context.TenantContext;
import software.plusminus.tenant.service.TenantProvider;
import software.plusminus.test.IntegrationTest;

import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

class TenantContextTest extends IntegrationTest {

    @Autowired
    private TenantProvider firstProvider;
    @Autowired
    private TenantProvider secondProvider;
    @Autowired
    private TenantContext tenantContext;

    @ParameterizedTest
    @CsvSource({
        "firstTenant,secondTenant,firstTenant",
        ",secondTenant,secondTenant",
        ",,",
    })
    void tenantContext(String firstTenant, String secondTenant, String expectedTenant) {
        when(firstProvider.currentTenant()).thenReturn(firstTenant);
        when(secondProvider.currentTenant()).thenReturn(secondTenant);

        String result = tenantContext.get();

        check(result).is(expectedTenant);
    }
}
