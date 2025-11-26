package software.plusminus.data.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import software.plusminus.check.util.JsonUtils;
import software.plusminus.data.fixtures.TestDto;
import software.plusminus.data.fixtures.TestEntity;
import software.plusminus.data.fixtures.TestUtil;
import software.plusminus.data.fixtures.TransactionService;
import software.plusminus.test.IntegrationTest;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.verify;
import static software.plusminus.check.Checks.check;

public class DataServiceIntegrationTest extends IntegrationTest {

    @MockBean
    private CrudService<TestDto, Long> dtoService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TransactionService tx;
    @Autowired
    private DataService dataService;

    @Test
    public void getById() {
        TestEntity entity = JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setId(1L);
        tx.run(() -> entityManager.merge(entity));

        TestEntity result = dataService.getById(TestEntity.class, 1L);

        check(result.getMyField()).is(entity.getMyField());
    }

    @Test
    public void findAll() {
        JsonUtils.fromJsonList("/json/test-entities.json", TestEntity[].class)
                .forEach(entity -> tx.run(() -> entityManager.merge(entity)));

        Page<TestEntity> page1 = dataService.getPage(TestEntity.class, PageRequest.of(0, 2,
                Sort.by(Sort.Direction.DESC, "id")));
        Page<TestEntity> page2 = dataService.getPage(TestEntity.class, PageRequest.of(1, 2,
                Sort.by(Sort.Direction.DESC, "id")));
        Page<TestEntity> page3 = dataService.getPage(TestEntity.class, PageRequest.of(2, 2,
                Sort.by(Sort.Direction.DESC, "id")));

        TestUtil.checkPages(page1, page2, page3);
    }

    @Test
    public void create() {
        TestEntity entity = JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setId(null);

        TestEntity result = dataService.create(entity);

        TestEntity inDb = tx.run(() -> entityManager.find(TestEntity.class, 1L));
        check(inDb.getMyField()).is(entity.getMyField());
        check(result.getMyField()).is(entity.getMyField());
    }

    @Test
    public void update() {
        TestEntity entity = JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setId(1L);
        tx.run(() -> entityManager.merge(entity));
        entity.setMyField("updated");

        TestEntity result = dataService.update(entity);

        TestEntity inDb = tx.run(() -> entityManager.find(TestEntity.class, 1L));
        check(inDb.getMyField()).is("updated");
        check(result.getMyField()).is("updated");
    }

    @Test
    public void delete() {
        TestEntity entity = JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
        entity.setId(null);
        tx.run(() -> entityManager.persist(entity));

        dataService.delete(entity);

        TestEntity inDb = entityManager.find(TestEntity.class, 1L);
        check(inDb).isNull();
    }

    @Test
    public void deleteDto() {
        TestDto testDto = new TestDto();
        testDto.setId(1L);
        testDto.setMyField("Some value");

        dataService.delete(testDto);

        verify(dtoService).delete(testDto);
    }
}