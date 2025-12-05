package software.plusminus.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.data.fixtures.TestCrudRepository;
import software.plusminus.data.fixtures.TestEntity;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RepositoryContextTest {

    @Autowired
    private RepositoryContext repositoryContext;

    @Test
    public void findCrudRepository() {
        CrudRepository<TestEntity, ?> repository = repositoryContext.findRepository(TestEntity.class);
        assertThat(repository).isNotNull()
                .isInstanceOf(TestCrudRepository.class);
    }
}
