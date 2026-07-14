package software.plusminus.audit.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import software.plusminus.audit.fixtures.TestEntity;
import software.plusminus.audit.fixtures.TransactionalService;
import software.plusminus.audit.model.AuditLog;
import software.plusminus.check.util.JsonUtil;
import software.plusminus.test.IntegrationTest;
import software.plusminus.test.helper.data.TestEntityManager;

import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import static software.plusminus.check.Checks.check;

public class AuditLogRepositoryTest extends IntegrationTest {

    @Autowired
    private TransactionalService transactionalService;
    @Autowired
    private AuditLogRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    private List<TestEntity> entities;
    private List<AuditLog> auditLogs;

    @Before
    public void before() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        entities = JsonUtil.fromJsonList("/json/test-entities.json", TestEntity[].class);
        auditLogs = JsonUtil.fromJsonList("/json/auditlogs.json", AuditLog[].class);
        entities.forEach(this::prepareEntityAndCommits);

        entities.forEach(entityManager::persist);
        transactionalService.inTransaction(
                () -> entityManager.createQuery("delete from AuditLog").executeUpdate()
        );
        auditLogs.forEach(entityManager::persist);
    }

    @Test
    public void setCurrentFalseOnAllOldAuditLogs() {
        int updated = data().transaction()
                .call(() -> repository.setCurrentFalseOnAllOldAuditLogs(TestEntity.class.getName(), 2L, 10L));
        List<AuditLog> auditLogsInDb = data().transaction().call(() -> entityManager
                .createQuery("select a from AuditLog a where a.entityType = ?1 and a.entityId = ?2",
                        AuditLog.class)
                .setParameter(1, TestEntity.class.getName())
                .setParameter(2, 2L)
                .getResultList());

        check(updated).is(1);
        check(auditLogsInDb.stream().noneMatch(AuditLog::isCurrent)).isTrue();
    }

    @Test
    public void setCurrentFalseOnAllOldAuditLogsIgnoresLastAuditLog() {
        int updated = data().transaction()
                .call(() -> repository.setCurrentFalseOnAllOldAuditLogs(TestEntity.class.getName(), 2L, 6L));
        List<AuditLog> auditLogsInDb = data().transaction().call(() -> entityManager
                .createQuery("select a from AuditLog a where a.entityType = ?1 and a.entityId = ?2",
                        AuditLog.class)
                .setParameter(1, TestEntity.class.getName())
                .setParameter(2, 2L)
                .getResultList());

        check(updated).is(0);
        check(auditLogsInDb).hasSize(3);
        check(auditLogsInDb.get(0).getNumber()).is(4L);
        check(auditLogsInDb.get(1).getNumber()).is(5L);
        check(auditLogsInDb.get(2).getNumber()).is(6L);
        check(auditLogsInDb.get(0).isCurrent()).isFalse();
        check(auditLogsInDb.get(1).isCurrent()).isFalse();
        check(auditLogsInDb.get(2).isCurrent()).isTrue();
    }

    @Test
    public void findIgnoringDevice() {
        List<AuditLog<?>> result = repository.findByEntityTypeInAndDeviceIsNotAndNumberGreaterThanAndCurrentTrue(
                        Collections.singletonList(TestEntity.class.getName()),
                        "Device 2",
                        2L,
                        Pageable.unpaged())
                .getContent();

        check(result).hasSize(2);
        check(result.get(0)).is(auditLogs.get(2));
        check(result.get(1)).is(auditLogs.get(8));
    }

    @Test
    public void findWithoutDevice() {
        List<AuditLog<?>> result = repository.findByEntityTypeInAndNumberGreaterThanAndCurrentTrue(
                        Collections.singletonList(TestEntity.class.getName()),
                        0L,
                        Pageable.unpaged())
                .getContent();

        check(result).is(auditLogs.get(2), auditLogs.get(5), auditLogs.get(8));
    }

    private void prepareEntityAndCommits(TestEntity entity) {
        int index = entities.indexOf(entity);
        auditLogs.subList(index * 3, index * 3 + 3)
                .forEach(auditLog -> {
                    auditLog.setNumber(null);
                    auditLog.setEntity(entity);
                });
        entity.setId(null);
    }
}
