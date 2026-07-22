package software.plusminus.data.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.plusminus.data.fixtures.TimedEntity;
import software.plusminus.data.fixtures.TransactionService;
import software.plusminus.test.IntegrationTest;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;

import static software.plusminus.check.Checks.check;

public class ModificationTimeIntegrationTest extends IntegrationTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TransactionService tx;
    @Autowired
    private DataService dataService;

    @Test
    public void populatesModificationTimeOnCreate() {
        TimedEntity entity = new TimedEntity();
        entity.setMyField("first");

        TimedEntity result = dataService.create(entity);

        check(result.getModificationTime()).isNotNull();
        TimedEntity inDb = tx.run(() -> entityManager.find(TimedEntity.class, result.getId()));
        check(inDb.getModificationTime()).isRecent();
    }

    @Test
    public void refreshesModificationTimeOnUpdate() {
        TimedEntity entity = new TimedEntity();
        entity.setMyField("first");
        LocalDateTime previous = LocalDateTime.now().minusDays(1);
        entity.setModificationTime(previous);
        tx.run(() -> entityManager.persist(entity));
        entity.setMyField("updated");

        TimedEntity result = dataService.update(entity);

        check(result.getModificationTime().isAfter(previous)).is(true);
        TimedEntity inDb = tx.run(() -> entityManager.find(TimedEntity.class, entity.getId()));
        check(inDb.getModificationTime().isAfter(previous)).is(true);
    }
}
