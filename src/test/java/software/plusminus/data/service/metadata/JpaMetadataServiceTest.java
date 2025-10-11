package software.plusminus.data.service.metadata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import software.plusminus.fixtures.TestEntity;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(JpaMetadataService.class)
public class JpaMetadataServiceTest {

    @Autowired
    private JpaMetadataService metadataService;

    @Test
    public void findType() {
        Class<?> type = metadataService.findType("TestEntity");
        assertThat(type).isEqualTo(TestEntity.class);
    }
}