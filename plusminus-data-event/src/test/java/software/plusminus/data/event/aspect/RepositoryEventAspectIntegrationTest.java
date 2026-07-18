package software.plusminus.data.event.aspect;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import software.plusminus.data.event.fixtures.RecordingEventListener;
import software.plusminus.data.event.fixtures.TestEntity;
import software.plusminus.data.event.fixtures.TestEntityRepository;
import software.plusminus.test.IntegrationTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static software.plusminus.check.Checks.check;

public class RepositoryEventAspectIntegrationTest extends IntegrationTest {

    @Autowired
    private TestEntityRepository repository;
    @Autowired
    private RecordingEventListener listener;

    @Before
    public void clear() {
        listener.clear();
    }

    @Test
    public void firesCreateEventOnSaveOfNewEntity() {
        TestEntity saved = repository.save(entity("first"));

        check(listener.getBeforeCreate()).is(saved);
        check(listener.getCreated()).is(saved);
        check(listener.getUpdated()).isEmpty();
    }

    @Test
    public void firesUpdateEventOnSaveOfExistingEntity() {
        TestEntity saved = repository.save(entity("first"));
        listener.clear();

        saved.setName("changed");
        repository.save(saved);

        check(listener.getBeforeUpdate()).is(saved);
        check(listener.getUpdated()).is(saved);
        check(listener.getCreated()).isEmpty();
    }

    @Test
    public void firesDeleteEventOnDelete() {
        TestEntity saved = repository.save(entity("first"));
        listener.clear();

        repository.delete(saved);

        check(listener.getBeforeDelete()).is(saved);
        check(listener.getDeleted()).is(saved);
        check(listener.getUpdated()).isEmpty();
    }

    @Test
    public void firesDeleteEventsOnDeleteById() {
        TestEntity saved = repository.save(entity("first"));
        listener.clear();

        repository.deleteById(saved.getId());

        check(listener.getBeforeDelete()).is(saved);
        check(listener.getDeleted()).is(saved);
        check(listener.getRead()).isEmpty();
    }

    @Test
    public void firesReadEventOnFindById() {
        TestEntity saved = repository.save(entity("first"));
        listener.clear();

        Optional<TestEntity> found = repository.findById(saved.getId());

        check(found).isNotEmpty().is(saved);
        check(listener.getRead()).is(saved);
    }

    @Test
    public void firesNoReadEventWhenEntityIsMissing() {
        Optional<TestEntity> found = repository.findById(-1L);

        check(found).isEmpty();
        check(listener.getRead()).isEmpty();
    }

    @Test
    public void firesReadEventPerPageElement() {
        repository.saveAll(Arrays.asList(entity("a"), entity("b"), entity("c")));
        listener.clear();

        Page<TestEntity> page = repository.findAll(PageRequest.of(0, 10));

        check(page.getContent()).hasSize(3);
        check(listener.getRead()).hasSize(3);
    }

    @Test
    public void firesCreateEventPerSavedElement() {
        List<TestEntity> saved = repository.saveAll(Arrays.asList(entity("a"), entity("b")));

        check(saved).hasSize(2);
        check(listener.getCreated()).hasSize(2);
        check(listener.getUpdated()).isEmpty();
    }

    private TestEntity entity(String name) {
        TestEntity entity = new TestEntity();
        entity.setName(name);
        return entity;
    }
}
