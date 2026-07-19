package software.plusminus.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
import software.plusminus.check.util.JsonUtil;
import software.plusminus.data.event.DataEventPublisher;
import software.plusminus.data.fixtures.TestEntity;
import software.plusminus.data.fixtures.TestUtil;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static software.plusminus.check.Checks.check;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({JpaDataRepository.class, DataEventPublisher.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class JpaDataRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private JpaDataRepository repository;

    @Test
    public void getById() {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setId(1L);
        TestEntity saved = entityManager.merge(entity);

        TestEntity result = repository.getById(TestEntity.class, 1L);

        check(result).is(saved);
        check(result).is(entity);
    }

    @Test
    public void findAll() {
        JsonUtil.fromJsonList("/json/test-entities.json", TestEntity[].class)
                .forEach(entityManager::merge);

        Page<TestEntity> page1 = repository.findAll(TestEntity.class, PageRequest.of(0, 2,
                Sort.by(Sort.Direction.DESC, "id")));
        Page<TestEntity> page2 = repository.findAll(TestEntity.class, PageRequest.of(1, 2,
                Sort.by(Sort.Direction.DESC, "id")));
        Page<TestEntity> page3 = repository.findAll(TestEntity.class, PageRequest.of(2, 2,
                Sort.by(Sort.Direction.DESC, "id")));

        TestUtil.checkPages(page1, page2, page3);
    }

    @Test
    public void findAllWithUnknownSortPropertyThrowsBadRequest() {
        assertThatThrownBy(() -> repository.findAll(TestEntity.class,
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "unknownProperty"))))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void save() {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setId(1L);

        TestEntity result = repository.save(entity);

        TestEntity inDb = entityManager.find(TestEntity.class, 1L);
        check(inDb).is(entity);
        check(result).is(entity);
    }

    @Test
    public void delete() {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setId(null);
        entityManager.persist(entity);

        repository.delete(entity);

        TestEntity inDb = entityManager.find(TestEntity.class, 1L);
        check(inDb).isNull();
    }
}