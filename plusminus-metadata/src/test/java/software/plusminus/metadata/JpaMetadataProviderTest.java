package software.plusminus.metadata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.metadata.fixtures.TestEntity1;
import software.plusminus.metadata.fixtures.TestEntity2;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootApplication
public class JpaMetadataProviderTest {

    @Autowired
    private JpaMetadataProvider metadataService;

    @Test
    public void findType() {
        List<Class<?>> classes = metadataService.provideClasses();
        assertThat(classes).containsExactlyInAnyOrder(TestEntity1.class, TestEntity2.class);
    }
}