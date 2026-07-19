package software.plusminus.sync;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.AopTestUtils;
import software.plusminus.audit.context.DeviceContext;
import software.plusminus.data.service.DataService;
import software.plusminus.jwt.service.IssuerContext;
import software.plusminus.sync.service.fetcher.ByUuidFinder;
import software.plusminus.sync.service.merger.VersionMerger;
import software.plusminus.tenant.context.TenantContext;
import software.plusminus.test.IntegrationTest;

/* All sync integration tests share this single set of spies so that Spring's test context cache
   reuses one application context for the whole module: @SpyBean/@MockBean declarations are part
   of the cache key, so per-class declarations would boot a separate context per class. */
public abstract class SyncIntegrationTest extends IntegrationTest {

    @SpyBean
    private DataService dataService;
    @SpyBean
    private DeviceContext deviceContext;
    @SpyBean
    private TenantContext tenantContext;
    @SpyBean
    private IssuerContext issuerContext;
    @SpyBean
    private ByUuidFinder byUuidFinder;
    @SpyBean
    private VersionMerger versionMerger;

    /* DataService is @Transactional, so its bean is an AOP proxy around the spy.
       Spring Boot 2.2 can't resolve a mock through the proxy, so it never resets it between tests. */
    @Before
    @BeforeEach
    public void resetDataServiceSpy() {
        Mockito.reset(dataServiceSpy());
    }

    protected DataService dataService() {
        return dataService;
    }

    protected DataService dataServiceSpy() {
        return AopTestUtils.getUltimateTargetObject(dataService);
    }

    protected DeviceContext deviceContext() {
        return deviceContext;
    }

    protected TenantContext tenantContext() {
        return tenantContext;
    }

    protected IssuerContext issuerContext() {
        return issuerContext;
    }

    protected ByUuidFinder byUuidFinder() {
        return byUuidFinder;
    }

    protected VersionMerger versionMerger() {
        return versionMerger;
    }
}
