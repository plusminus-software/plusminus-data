package software.plusminus.tenant.fixtures;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import software.plusminus.tenant.service.TenantProvider;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Order(0)
    TenantProvider firstProvider() {
        return mock(TenantProvider.class);
    }

    @Bean
    @Order(1)
    TenantProvider secondProvider() {
        return mock(TenantProvider.class);
    }
}
