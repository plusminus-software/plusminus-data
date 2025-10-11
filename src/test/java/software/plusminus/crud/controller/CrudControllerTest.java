package software.plusminus.crud.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import software.plusminus.check.util.JsonUtils;
import software.plusminus.crud.service.CrudService;
import software.plusminus.fixtures.TestEntity;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CrudControllerTest {

    @Mock
    private CrudService<TestEntity, Long> service;
    @InjectMocks
    private CrudController<TestEntity, Long> controller = mock(CrudController.class, CALLS_REAL_METHODS);
    
    @Test
    public void getById() {
        controller.getById(42L);
        verify(service).getById(42L);
    }

    @Test
    public void getPage() {
        Pageable pageable = PageRequest.of(2, 20);
        controller.getPage(pageable);
        verify(service).getPage(pageable);
    }

    @Test
    public void create() {
        TestEntity testEntity = readEntity();
        controller.create(testEntity);
        verify(service).create(testEntity);
    }

    @Test
    public void update() {
        TestEntity testEntity = readEntity();
        controller.update(testEntity);
        verify(service).update(testEntity);
    }

    @Test
    public void patch() {
        TestEntity testEntity = readEntity();
        controller.patch(testEntity);
        verify(service).patch(testEntity);
    }

    @Test
    public void delete() {
        TestEntity testEntity = readEntity();
        controller.delete(testEntity);
        verify(service).delete(testEntity);
    }
    
    private TestEntity readEntity() {
        return JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
    }
}