package software.plusminus.data.service.entity;

import org.junit.Test;
import software.plusminus.data.service.entity.test.EmbeddableClass;
import software.plusminus.data.service.entity.test.MultiEntity;
import software.plusminus.data.service.entity.test.SpringIdAnnotationEntity;
import software.plusminus.fixtures.TestEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JpaEntityServiceTest {

    private JpaEntityService service = new JpaEntityService();

    @Test
    public void findSubentities() {
        MultiEntity root = new MultiEntity();
        root.setName("root");
        MultiEntity left = new MultiEntity();
        left.setName("left");
        MultiEntity right = new MultiEntity();
        right.setName("right");
        MultiEntity subleft = new MultiEntity();
        subleft.setName("subleft");
        MultiEntity inEmbeddable = new MultiEntity();
        inEmbeddable.setName("inEmbeddable");

        EmbeddableClass embeddableClass = new EmbeddableClass();
        embeddableClass.setEntity(inEmbeddable);
        left.setEmbeddable(embeddableClass);
        root.setLeft(left);
        root.setRight(right);
        left.setLeft(subleft);

        Set subentities = service.findSubentities(root);

        assertThat(subentities).containsExactlyInAnyOrder(left, right, subleft, inEmbeddable);
    }

    @Test
    public void findSubentitiesInCollection() {
        MultiEntity root = new MultiEntity();
        root.setName("root");
        MultiEntity left = new MultiEntity();
        left.setName("left");
        MultiEntity right = new MultiEntity();
        right.setName("right");
        MultiEntity sub = new MultiEntity();
        sub.setName("sub");
        MultiEntity subsub = new MultiEntity();
        subsub.setName("subsub");

        root.setLeft(left);
        root.setRight(right);
        root.setList(Arrays.asList(left, sub));
        right.setList(Collections.singletonList(subsub));

        Set subentities = service.findSubentities(root);

        assertThat(subentities).containsExactlyInAnyOrder(left, right, sub, subsub);
    }

    @Test
    public void findIdType() {
        Class<?> idType = service.findIdType(TestEntity.class);
        assertThat(idType).isEqualTo(Long.class);
    }

    @Test
    public void findIdType_SpringsIdAnnotation() {
        Class<?> idType = service.findIdType(SpringIdAnnotationEntity.class);
        assertThat(idType).isEqualTo(Integer.class);
    }
}