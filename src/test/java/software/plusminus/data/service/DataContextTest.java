package software.plusminus.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.data.fixtures.TestCrudRepository;
import software.plusminus.data.fixtures.TestEntity;
import software.plusminus.data.repository.CrudRepository;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class DataContextTest {

    @Autowired
    private DataContext dataContext;

    @Test
    public void findCrudRepository() {
        CrudRepository<TestEntity, ?> repository = dataContext.findRepository(TestEntity.class);
        assertThat(repository).isNotNull()
                .isInstanceOf(TestCrudRepository.class);
    }
}
