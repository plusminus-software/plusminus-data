package software.plusminus.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.check.util.JsonUtils;
import software.plusminus.data.TestEntity;
import software.plusminus.data.service.entity.JpaEntityService;

import static software.plusminus.check.Checks.check;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({JpaDataRepository.class, JpaEntityService.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DataRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DataRepository repository;

    @Test
    public void save() throws Exception {
        TestEntity entity = JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);

        TestEntity result = repository.save(entity);

        TestEntity inDb = entityManager.find(TestEntity.class, 1L);
        check(inDb).is(entity);
        check(result).is(entity);
    }

    @Test
    public void delete() throws Exception {
        TestEntity entity = JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setId(null);
        entityManager.persist(entity);

        repository.delete(entity);

        TestEntity inDb = entityManager.find(TestEntity.class, 1L);
        check(inDb).isNull();
    }
}