package software.plusminus.crud.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.check.util.JsonUtils;
import software.plusminus.fixtures.TestCrudRepository;
import software.plusminus.fixtures.TestEntity;
import software.plusminus.fixtures.TestUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static software.plusminus.check.Checks.check;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class CrudRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TestCrudRepository repository;

    @Test
    public void save() {
        TestEntity entity = JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setId(null);

        TestEntity result = repository.save(entity);

        TestEntity inDb = entityManager.find(TestEntity.class, 1L);
        check(result).is(entity);
        check(result).is(inDb);
    }

    @Test
    public void getById() {
        List<TestEntity> entities = readEntities();
        entities.stream()
                .peek(entity -> entity.setId(null))
                .forEach(entityManager::persist);

        TestEntity result = repository.getById(2L);

        check(result).is(entities.get(1));
    }

    @Test
    public void getById_ForMissed() {
        List<TestEntity> entities = readEntities();
        entities.stream()
                .peek(entity -> entity.setId(null))
                .forEach(entityManager::persist);

        TestEntity result = repository.getById(321L);

        assertThat(result).isNull();
    }

    @Test
    public void findAll() {
        List<TestEntity> entities = readEntities();
        entities.stream()
                .peek(entity -> entity.setId(null))
                .forEach(entityManager::persist);

        Page<TestEntity> page1 = repository.findAll(PageRequest.of(0, 2,
                Sort.by(Sort.Direction.DESC, "id")));
        Page<TestEntity> page2 = repository.findAll(PageRequest.of(1, 2,
                Sort.by(Sort.Direction.DESC, "id")));
        Page<TestEntity> page3 = repository.findAll(PageRequest.of(2, 2,
                Sort.by(Sort.Direction.DESC, "id")));

        TestUtil.checkPages(page1, page2, page3);
    }

    @Test
    public void findAll_Unpaged() {
        List<TestEntity> entities = readEntities();
        entities.stream()
                .peek(entity -> entity.setId(null))
                .forEach(entityManager::persist);

        Page<TestEntity> result = repository.findAll(Pageable.unpaged());

        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(5);
        check(result.getContent().get(0)).is(entities.get(0));
        check(result.getContent().get(1)).is(entities.get(1));
        check(result.getContent().get(2)).is(entities.get(2));
        check(result.getContent().get(3)).is(entities.get(3));
        check(result.getContent().get(4)).is(entities.get(4));
    }

    @Test
    public void delete() {
        List<TestEntity> entities = readEntities();
        entities.stream()
                .peek(entity -> entity.setId(null))
                .forEach(entityManager::persist);

        repository.delete(entities.get(1));

        assertThat(entityManager.find(TestEntity.class, 1L)).isNotNull();
        assertThat(entityManager.find(TestEntity.class, 2L)).isNull();
        assertThat(entityManager.find(TestEntity.class, 3L)).isNotNull();
    }
    
    private List<TestEntity> readEntities() {
        return JsonUtils.fromJsonList("/json/test-entities.json", TestEntity[].class);
    }
}