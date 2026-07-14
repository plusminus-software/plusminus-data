package software.plusminus.data.event.aspect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.data.event.fixtures.RecordingEventListener;
import software.plusminus.data.event.fixtures.TestEntity;
import software.plusminus.data.event.fixtures.TestEntityRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class RepositoryEventAspectIntegrationTest {

    @Autowired
    private TestEntityRepository repository;
    @Autowired
    private RecordingEventListener listener;

    @Before
    public void setUp() {
        repository.deleteAll();
        listener.clear();
    }

    @Test
    public void firesCreateEventOnSaveOfNewEntity() {
        TestEntity saved = repository.save(entity("first"));

        assertThat(listener.getCreated()).hasSize(1);
        assertThat(listener.getCreated().get(0).getId()).isEqualTo(saved.getId());
        assertThat(listener.getUpdated()).isEmpty();
    }

    @Test
    public void firesUpdateEventOnSaveOfExistingEntity() {
        TestEntity saved = repository.save(entity("first"));
        listener.clear();

        saved.setName("changed");
        repository.save(saved);

        assertThat(listener.getUpdated()).hasSize(1);
        assertThat(listener.getUpdated().get(0).getName()).isEqualTo("changed");
        assertThat(listener.getCreated()).isEmpty();
    }

    @Test
    public void firesDeleteEventOnDelete() {
        TestEntity saved = repository.save(entity("first"));
        listener.clear();

        repository.delete(saved);

        assertThat(listener.getDeleted()).hasSize(1);
        assertThat(listener.getDeleted().get(0).getId()).isEqualTo(saved.getId());
    }

    @Test
    public void firesReadEventOnFindById() {
        TestEntity saved = repository.save(entity("first"));
        listener.clear();

        Optional<TestEntity> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(listener.getRead()).hasSize(1);
        assertThat(listener.getRead().get(0).getId()).isEqualTo(saved.getId());
    }

    @Test
    public void firesNoReadEventWhenEntityIsMissing() {
        Optional<TestEntity> found = repository.findById(-1L);

        assertThat(found).isNotPresent();
        assertThat(listener.getRead()).isEmpty();
    }

    @Test
    public void firesReadEventPerPageElement() {
        repository.saveAll(Arrays.asList(entity("a"), entity("b"), entity("c")));
        listener.clear();

        Page<TestEntity> page = repository.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(3);
        assertThat(listener.getRead()).hasSize(3);
    }

    @Test
    public void firesCreateEventPerSavedElement() {
        List<TestEntity> saved = repository.saveAll(Arrays.asList(entity("a"), entity("b")));

        assertThat(saved).hasSize(2);
        assertThat(listener.getCreated()).hasSize(2);
        assertThat(listener.getUpdated()).isEmpty();
    }

    private TestEntity entity(String name) {
        TestEntity entity = new TestEntity();
        entity.setName(name);
        return entity;
    }
}
