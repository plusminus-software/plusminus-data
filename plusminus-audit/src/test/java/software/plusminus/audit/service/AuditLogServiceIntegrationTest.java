package software.plusminus.audit.service;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.plusminus.audit.context.DeviceContext;
import software.plusminus.audit.fixtures.TestEntity;
import software.plusminus.audit.fixtures.TransactionalService;
import software.plusminus.audit.model.AuditLog;
import software.plusminus.check.util.JsonUtil;
import software.plusminus.data.event.CrudAction;
import software.plusminus.data.service.DataService;
import software.plusminus.security.Security;
import software.plusminus.tenant.context.TenantContext;
import software.plusminus.test.IntegrationTest;

import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;

import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

public class AuditLogServiceIntegrationTest extends IntegrationTest {

    @MockBean
    private TenantContext tenantContext;
    @MockBean
    private DeviceContext deviceContext;
    @MockBean
    private TransactionIdProvider transactionIdProvider;
    @Autowired
    private TransactionalService transactionalService;
    @Autowired
    private DataService dataService;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AuditLogService service;

    private UUID transactionId = UUID.fromString("3a37e67d-a8b2-4c35-9e6f-a4e4b686ffb5");

    @Override
    public void beforeEach() {
        super.beforeEach();
        when(deviceContext.optional()).thenReturn(Optional.of("TestDevice"));
        when(transactionIdProvider.currentTransactionId()).thenReturn(transactionId);
        security().login(Security.builder()
                .username("TestUser")
                .build());
    }

    @After
    public void after() {
        context().clear();
    }

    @Test
    public void create() {
        checkAuditLog(CrudAction.CREATE);
    }

    @Test
    public void update() {
        checkAuditLog(CrudAction.UPDATE);
    }

    @Test
    public void patch() {
        checkAuditLog(CrudAction.PATCH);
    }

    @Test
    public void delete() {
        checkAuditLog(CrudAction.DELETE);
    }

    @Test
    public void tenant() {
        String tenant = "tenantFromService";
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setTenant(null);
        when(tenantContext.get()).thenReturn(tenant);

        transactionalService.inTransaction(() -> service.log(entity, CrudAction.CREATE));
        AuditLog<?> auditLog = entityManager.find(AuditLog.class, 1L);

        check(auditLog.getTenant()).is(tenant);
    }

    @Test
    public void previousAuditLog() {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);

        transactionalService.inTransaction(() -> service.log(entity, CrudAction.CREATE));
        transactionalService.inTransaction(() -> service.log(entity, CrudAction.UPDATE));

        checkCurrent();
    }

    @Test
    public void nestedTransaction() {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);

        transactionalService.inTransaction(() -> {
            transactionalService.inTransaction(() -> service.log(entity, CrudAction.CREATE));
            service.log(entity, CrudAction.UPDATE);
        });
        AuditLog<?> auditLogFirst = entityManager.find(AuditLog.class, 1L);
        AuditLog<?> auditLogNull = entityManager.find(AuditLog.class, 2L);

        check(auditLogFirst.isCurrent()).is(true);
        check(auditLogFirst.getAction()).is(CrudAction.CREATE);
        check(auditLogNull).isNull();
    }

    @Test
    public void nestedSecondTransaction() {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);

        transactionalService.inTransaction(() -> {
            service.log(entity, CrudAction.CREATE);
            transactionalService.inTransaction(() -> service.log(entity, CrudAction.UPDATE));
        });
        AuditLog<?> auditLogFirst = entityManager.find(AuditLog.class, 1L);
        AuditLog<?> auditLogNull = entityManager.find(AuditLog.class, 2L);

        check(auditLogFirst.isCurrent()).is(true);
        check(auditLogFirst.getAction()).is(CrudAction.CREATE);
        check(auditLogNull).isNull();
    }

    @Test
    public void nestedNewTransaction() {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);

        transactionalService.inTransaction(() -> {
            transactionalService.inNewTransaction(() -> service.log(entity, CrudAction.CREATE));
            service.log(entity, CrudAction.UPDATE);
        });

        checkCurrent();
    }

    @Test
    public void throughDataService() {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setId(null);

        TestEntity created = transactionalService.inTransaction(() -> dataService.create(entity));

        transactionalService.inTransaction(() -> {
            created.setMyField("updated1");
            TestEntity updated = dataService.update(created);
            updated.setMyField("updated2");
            dataService.update(updated);
        });

        checkCurrent();
    }

    private void checkAuditLog(CrudAction action) {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);

        transactionalService.inTransaction(() -> service.log(entity, action));
        AuditLog<?> auditLog = entityManager.find(AuditLog.class, 1L);
        AuditLog<?> auditLogNull = entityManager.find(AuditLog.class, 2L);

        check(auditLog.getAction()).is(action);
        check(auditLog.getDevice()).is("TestDevice");
        check(auditLog.getUsername()).is("TestUser");
        check(auditLog.getTenant()).is("Some tenant");
        check(auditLog.getTransactionId()).is(transactionId);
        check(auditLogNull).isNull();
    }

    private void checkCurrent() {
        AuditLog<?> auditLogFirst = entityManager.find(AuditLog.class, 1L);
        AuditLog<?> auditLogSecond = entityManager.find(AuditLog.class, 2L);

        check(auditLogFirst.isCurrent()).is(false);
        check(auditLogFirst.getAction()).is(CrudAction.CREATE);
        check(auditLogSecond.isCurrent()).is(true);
        check(auditLogSecond.getAction()).is(CrudAction.UPDATE);
    }
}