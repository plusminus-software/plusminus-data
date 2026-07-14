package software.plusminus.data.fixtures;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UtilityClass
public class TestUtil {

    public void checkPages(Page<TestEntity> page1, Page<TestEntity> page2, Page<TestEntity> page3) {
        List<Page<TestEntity>> pages = Arrays.asList(page1, page2, page3);

        assertThat(pages).extracting(Page::getTotalPages).containsExactly(3, 3, 3);
        assertThat(pages).extracting(Page::getTotalElements).containsExactly(5L, 5L, 5L);
        assertThat(pages).extracting(Page::getNumber).containsExactly(0, 1, 2);
        assertThat(pages).extracting(Page::getSize).containsExactly(2, 2, 2);
        assertThat(page1.getContent()).extracting(TestEntity::getMyField)
                .containsExactly("Some value 5", "Some value 4");
        assertThat(page1.getContent()).extracting(TestEntity::getId)
                .containsExactly(5L, 4L);
        assertThat(page2.getContent()).extracting(TestEntity::getMyField)
                .containsExactly("Some value 3", "Some value 2");
        assertThat(page2.getContent()).extracting(TestEntity::getId)
                .containsExactly(3L, 2L);
        assertThat(page3.getContent()).extracting(TestEntity::getMyField)
                .containsExactly("Some value 1");
        assertThat(page3.getContent()).extracting(TestEntity::getId)
                .containsExactly(1L);
    }
}
