package software.plusminus.patch.service.patcher;

import lombok.Data;
import org.junit.Test;
import software.plusminus.patch.annotation.StringCollectionPatch;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class StringCollectionElementPatcherTest {

    private StringCollectionElementPatcher converter = new StringCollectionElementPatcher();

    @Test
    public void toKey() throws NoSuchFieldException {
        String element = "value:key";
        Object key = converter.toKey(StringKeyEntity.class.getDeclaredField("strings"), null, element);
        assertThat(key).isEqualTo("key");
    }

    @Test
    public void toValue() throws NoSuchFieldException {
        String element = "value:key";
        Object key = converter.toValue(StringKeyEntity.class.getDeclaredField("strings"), null, element);
        assertThat(key).isEqualTo("value:key");
    }

    @Test
    public void toValue_NullCase() throws NoSuchFieldException {
        String element = "value:";
        Object key = converter.toValue(StringKeyEntity.class.getDeclaredField("strings"), null, element);
        assertThat(key).isNull();
    }

    @Data
    private static class StringKeyEntity {

        @StringCollectionPatch(splitter = ":", index = 1)
        private List<String> strings;

    }
}