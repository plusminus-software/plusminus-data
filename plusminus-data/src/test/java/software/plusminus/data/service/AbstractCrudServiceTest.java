package software.plusminus.data.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import software.plusminus.check.util.JsonUtil;
import software.plusminus.data.exception.ClientDataException;
import software.plusminus.data.fixtures.TestEntity;
import software.plusminus.data.model.Update;
import software.plusminus.data.repository.CrudRepository;
import software.plusminus.patch.service.PatchService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCrudServiceTest {

    private static final String ENTITY = "entity";
    private static final String SAVED = "saved";

    @Mock
    private DataValidator dataValidator;
    @Mock
    private PatchService patchService;
    @Mock
    private CrudRepository<TestEntity, Long> repository;
    @InjectMocks
    private AbstractCrudService<TestEntity, Long> crudService =
            mock(AbstractCrudService.class, Answers.CALLS_REAL_METHODS);
    @Captor
    private ArgumentCaptor<TestEntity> captor;

    @Before
    public void beforeEach() {
        ReflectionTestUtils.setField(crudService, "self", null);
    }

    @Test
    public void getById() {
        TestEntity entity = readEntity();
        when(repository.getById(2L)).thenReturn(entity);

        TestEntity result = crudService.getById(2L);

        assertThat(result).isSameAs(entity);
    }

    @Test
    public void readPage() {
        TestEntity entity = readEntity();
        Pageable pageable = PageRequest.of(2, 3);
        Page<TestEntity> page = new PageImpl<>(Collections.singletonList(entity), pageable, 100);
        when(repository.findAll(pageable)).thenReturn(page);

        Page<TestEntity> result = crudService.getPage(pageable);

        check(result).is(page);
    }

    @Test
    public void create() {
        TestEntity entity = readEntity(ENTITY);
        entity.setId(null);
        TestEntity saved = readEntity(SAVED);
        when(repository.save(entity)).thenReturn(saved);

        TestEntity result = crudService.create(entity);

        assertThat(result).isSameAs(saved);
    }

    @Test(expected = ClientDataException.class)
    public void create_WithId() {
        TestEntity entity = readEntity();
        crudService.create(entity);
    }

    @Test
    public void update() {
        TestEntity entity = readEntity(ENTITY);
        TestEntity saved = readEntity(SAVED);
        when(repository.save(entity)).thenReturn(saved);

        TestEntity result = crudService.update(entity);

        assertThat(result).isSameAs(saved);
    }

    @Test(expected = ClientDataException.class)
    public void update_WithoutId() {
        TestEntity entity = readEntity();
        entity.setId(null);

        crudService.update(entity);
    }

    @Test
    public void patch() {
        TestEntity patch = readEntity("patch");
        TestEntity entity = readEntity(ENTITY);
        TestEntity saved = readEntity(SAVED);
        when(repository.getById(2L)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);

        TestEntity result = crudService.patch(patch);

        verify(patchService).patch(patch, entity);
        verify(dataValidator).validate(entity, Update.class);
        assertThat(result).isSameAs(saved);
    }

    @Test(expected = ClientDataException.class)
    public void patch_WithoutId() {
        TestEntity entity = readEntity();
        entity.setId(null);
        crudService.patch(entity);
    }

    @Test(expected = ClientDataException.class)
    public void patch_WithInvalidTarget() {
        TestEntity patch = readEntity("patch");
        TestEntity entity = readEntity(ENTITY);
        when(repository.getById(2L)).thenReturn(entity);
        doThrow(new ClientDataException("Validation failed"))
                .when(dataValidator).validate(entity, Update.class);

        crudService.patch(patch);
    }

    @Test
    public void delete() {
        TestEntity entity = readEntity();

        crudService.delete(entity);

        verify(repository).delete(captor.capture());
        assertThat(captor.getValue()).isSameAs(entity);
    }

    private TestEntity readEntity() {
        return JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);
    }

    private TestEntity readEntity(String myField) {
        TestEntity entity = readEntity();
        entity.setMyField(myField);
        return entity;
    }
}