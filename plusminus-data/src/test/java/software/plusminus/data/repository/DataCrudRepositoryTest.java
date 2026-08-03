package software.plusminus.data.repository;

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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

@RunWith(MockitoJUnitRunner.class)
public class DataCrudRepositoryTest {

    @Mock
    private DataRepository dataRepository;
    private DataCrudRepository<TestEntity, Long> repository;

    @Before
    public void before() {
        repository = new DataCrudRepository<>(TestEntity.class, dataRepository);
    }

    @Test
    public void findById() {
        TestEntity entity = entity();
        when(dataRepository.findById(TestEntity.class, 1L)).thenReturn(Optional.of(entity));

        Optional<TestEntity> result = repository.findById(1L);

        check(result).isPresent().isSame(entity);
    }

    @Test
    public void findByIdMissed() {
        when(dataRepository.findById(TestEntity.class, 1L)).thenReturn(Optional.empty());

        check(repository.findById(1L)).isEmpty();
    }

    @Test(expected = NotFoundException.class)
    public void getById_ForMissed() {
        when(dataRepository.findById(TestEntity.class, 1L)).thenReturn(Optional.empty());

        repository.getById(1L);
    }

    @Test
    public void findAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TestEntity> page = new PageImpl<>(Collections.singletonList(entity()));
        when(dataRepository.findAll(TestEntity.class, pageable)).thenReturn(page);

        check(repository.findAll(pageable)).isSame(page);
    }

    @Test
    public void save() {
        TestEntity entity = entity();
        when(dataRepository.save(entity)).thenReturn(entity);

        check(repository.save(entity)).isSame(entity);
    }

    @Test
    public void delete() {
        TestEntity entity = entity();

        repository.delete(entity);

        verify(dataRepository).delete(entity);
    }

    private TestEntity entity() {
        TestEntity entity = new TestEntity();
        entity.setId(1L);
        entity.setMyField("value");
        return entity;
    }
}
