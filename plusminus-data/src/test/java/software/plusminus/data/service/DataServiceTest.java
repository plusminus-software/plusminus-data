package software.plusminus.data.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import software.plusminus.data.exception.NotFoundException;
import software.plusminus.data.fixtures.TestEntity;
import software.plusminus.data.repository.CrudRepository;
import software.plusminus.data.repository.DataRepository;
import software.plusminus.data.repository.RepositoryContext;
import software.plusminus.patch.service.PatchService;

import java.util.Collections;
import javax.validation.Validator;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DataServiceTest {

    @Mock
    private Validator validator;
    @Mock
    private PatchService patchService;
    @Mock
    private CrudServiceContext crudServiceContext;
    @Mock
    private RepositoryContext repositoryContext;
    @Mock
    private DataRepository dataRepository;
    @Mock
    private CrudService<TestEntity, Long> crudService;
    @Mock
    private CrudRepository<TestEntity, Long> crudRepository;

    private DataService dataService;

    @Before
    public void before() {
        dataService = withDataRepository(dataRepository);
    }

    private DataService withDataRepository(DataRepository repository) {
        return new DataService(validator, patchService,
                crudServiceContext, repositoryContext, repository);
    }

    private TestEntity entity(Long id) {
        TestEntity entity = new TestEntity();
        entity.setId(id);
        entity.setMyField("value");
        return entity;
    }

    // ---------- getById ----------

    @Test
    public void getByIdViaCrudService() {
        TestEntity entity = entity(1L);
        doReturn(crudService).when(crudServiceContext).findService(TestEntity.class);
        when(crudService.getById(1L)).thenReturn(entity);
        check(dataService.getById(TestEntity.class, 1L)).isSame(entity);
    }

    @Test
    public void getByIdViaCrudRepository() {
        TestEntity entity = entity(1L);
        doReturn(crudRepository).when(repositoryContext).findRepository(TestEntity.class);
        when(crudRepository.getById(1L)).thenReturn(entity);
        check(dataService.getById(TestEntity.class, 1L)).isSame(entity);
    }

    @Test
    public void getByIdViaDataRepository() {
        TestEntity entity = entity(1L);
        when(dataRepository.getById(TestEntity.class, 1L)).thenReturn(entity);
        check(dataService.getById(TestEntity.class, 1L)).isSame(entity);
    }

    @Test(expected = NotFoundException.class)
    public void getByIdThrowsWhenNoRepository() {
        withDataRepository(null).getById(TestEntity.class, 1L);
    }

    @Test(expected = NotFoundException.class)
    public void getByIdThrowsWhenObjectNotFound() {
        when(dataRepository.getById(TestEntity.class, 1L)).thenReturn(null);
        dataService.getById(TestEntity.class, 1L);
    }

    // ---------- getPage ----------

    @Test
    public void getPageViaCrudService() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TestEntity> page = new PageImpl<>(Collections.singletonList(entity(1L)));
        doReturn(crudService).when(crudServiceContext).findService(TestEntity.class);
        when(crudService.getPage(pageable)).thenReturn(page);
        check(dataService.getPage(TestEntity.class, pageable)).isSame(page);
    }

    @Test
    public void getPageViaCrudRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TestEntity> page = new PageImpl<>(Collections.singletonList(entity(1L)));
        doReturn(crudRepository).when(repositoryContext).findRepository(TestEntity.class);
        when(crudRepository.findAll(pageable)).thenReturn(page);
        check(dataService.getPage(TestEntity.class, pageable)).isSame(page);
    }

    @Test
    public void getPageViaDataRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TestEntity> page = new PageImpl<>(Collections.singletonList(entity(1L)));
        when(dataRepository.findAll(TestEntity.class, pageable)).thenReturn(page);
        check(dataService.getPage(TestEntity.class, pageable)).isSame(page);
    }

    @Test(expected = NotFoundException.class)
    public void getPageThrowsWhenNoRepository() {
        withDataRepository(null).getPage(TestEntity.class, PageRequest.of(0, 10));
    }

    // ---------- create ----------

    @Test
    public void createViaCrudService() {
        TestEntity entity = entity(null);
        doReturn(crudService).when(crudServiceContext).findService(TestEntity.class);
        when(crudService.create(entity)).thenReturn(entity);
        check(dataService.create(entity)).isSame(entity);
    }

    @Test
    public void createViaCrudRepository() {
        TestEntity entity = entity(null);
        TestEntity saved = entity(1L);
        doReturn(crudRepository).when(repositoryContext).findRepository(TestEntity.class);
        when(crudRepository.save(entity)).thenReturn(saved);
        check(dataService.create(entity)).isSame(saved);
    }

    @Test
    public void createViaDataRepository() {
        TestEntity entity = entity(null);
        TestEntity saved = entity(1L);
        when(dataRepository.save(entity)).thenReturn(saved);
        check(dataService.create(entity)).isSame(saved);
    }

    @Test(expected = NotFoundException.class)
    public void createThrowsWhenNoRepository() {
        withDataRepository(null).create(entity(null));
    }

    // ---------- update ----------

    @Test
    public void updateViaCrudService() {
        TestEntity entity = entity(1L);
        doReturn(crudService).when(crudServiceContext).findService(TestEntity.class);
        when(crudService.update(entity)).thenReturn(entity);
        check(dataService.update(entity)).isSame(entity);
    }

    @Test
    public void updateViaCrudRepository() {
        TestEntity entity = entity(1L);
        doReturn(crudRepository).when(repositoryContext).findRepository(TestEntity.class);
        when(crudRepository.save(entity)).thenReturn(entity);
        check(dataService.update(entity)).isSame(entity);
    }

    @Test
    public void updateViaDataRepository() {
        TestEntity entity = entity(1L);
        when(dataRepository.save(entity)).thenReturn(entity);
        check(dataService.update(entity)).isSame(entity);
    }

    @Test(expected = NotFoundException.class)
    public void updateThrowsWhenNoRepository() {
        withDataRepository(null).update(entity(1L));
    }

    // ---------- patch ----------

    @Test
    public void patchViaCrudService() {
        TestEntity patch = entity(1L);
        doReturn(crudService).when(crudServiceContext).findService(TestEntity.class);
        when(crudService.patch(patch)).thenReturn(patch);
        check(dataService.patch(patch)).isSame(patch);
    }

    @Test
    public void patchViaCrudRepository() {
        TestEntity patch = entity(1L);
        TestEntity target = entity(1L);
        doReturn(crudRepository).when(repositoryContext).findRepository(TestEntity.class);
        when(crudRepository.getById(1L)).thenReturn(target);
        when(crudRepository.save(target)).thenReturn(target);
        check(dataService.patch(patch)).isSame(target);
        verify(patchService).patch(patch, target);
    }

    @Test
    public void patchViaDataRepository() {
        TestEntity patch = entity(1L);
        TestEntity target = entity(1L);
        when(dataRepository.getById(TestEntity.class, 1L)).thenReturn(target);
        when(dataRepository.save(target)).thenReturn(target);
        check(dataService.patch(patch)).isSame(target);
    }

    // ---------- delete ----------

    @Test
    public void deleteViaCrudService() {
        TestEntity entity = entity(1L);
        doReturn(crudService).when(crudServiceContext).findService(TestEntity.class);
        dataService.delete(entity);
        verify(crudService).delete(entity);
    }

    @Test
    public void deleteViaCrudRepository() {
        TestEntity entity = entity(1L);
        doReturn(crudRepository).when(repositoryContext).findRepository(TestEntity.class);
        dataService.delete(entity);
        verify(crudRepository).delete(entity);
    }

    @Test
    public void deleteViaDataRepository() {
        TestEntity entity = entity(1L);
        dataService.delete(entity);
        verify(dataRepository).delete(entity);
    }

    @Test(expected = NotFoundException.class)
    public void deleteThrowsWhenNoRepository() {
        withDataRepository(null).delete(entity(1L));
    }

    // ---------- save ----------

    @Test
    public void saveCreatesWhenIdIsNull() {
        TestEntity entity = entity(null);
        TestEntity saved = entity(1L);
        when(dataRepository.save(entity)).thenReturn(saved);
        check(dataService.save(entity)).isSame(saved);
    }

    @Test
    public void saveUpdatesWhenIdIsPresent() {
        TestEntity entity = entity(1L);
        when(dataRepository.save(entity)).thenReturn(entity);
        check(dataService.save(entity)).isSame(entity);
    }
}
