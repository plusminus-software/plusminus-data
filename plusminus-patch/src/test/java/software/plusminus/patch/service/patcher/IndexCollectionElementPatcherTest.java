package software.plusminus.patch.service.patcher;

import org.assertj.core.util.Sets;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class IndexCollectionElementPatcherTest {

    private IndexCollectionElementPatcher converter = new IndexCollectionElementPatcher();

    @Test
    public void toKey() {
        List<String> elements = Arrays.asList("zero", "one", "two");
        Object key = converter.toKey(null, elements, "one");
        assertThat(key).isEqualTo(1);
    }

    @Test
    public void toKey_IfNotList() {
        Set<String> elements = Sets.newLinkedHashSet("zero", "one", "two");
        Object key = converter.toKey(null, elements, "one");
        assertThat(key).isNull();
    }

    @Test
    public void toKey_Positional_UsesProvidedIndex() {
        List<String> elements = Arrays.asList("same", "same", "same");
        Object key = converter.toKey(null, elements, "same", 2);
        assertThat(key).isEqualTo(2);
    }

    @Test
    public void toKey_Positional_IfNotList() {
        Set<String> elements = Sets.newLinkedHashSet("zero", "one", "two");
        Object key = converter.toKey(null, elements, "one", 1);
        assertThat(key).isNull();
    }

}
