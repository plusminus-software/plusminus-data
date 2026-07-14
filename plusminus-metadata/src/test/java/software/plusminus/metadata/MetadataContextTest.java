package software.plusminus.metadata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.metadata.fixtures.TestEntity1;
import software.plusminus.metadata.fixtures.TestEntity2;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootApplication
public class MetadataContextTest {

    @Test
    public void get() {
        Class<?> class1 = MetadataContext.getClass("TestEntity1");
        Class<?> class2 = MetadataContext.getClass("TestEntity2");

        assertThat(class1).isSameAs(TestEntity1.class);
        assertThat(class2).isSameAs(TestEntity2.class);
    }
  
}