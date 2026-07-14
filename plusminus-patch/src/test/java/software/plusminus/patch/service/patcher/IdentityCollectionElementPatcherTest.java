package software.plusminus.patch.service.patcher;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentityCollectionElementPatcherTest {

    private IdentityCollectionElementPatcher converter = new IdentityCollectionElementPatcher();

    @Test
    public void toKey() {
        String element = "someValue";
        Object result = converter.toKey(null, null, element);
        assertThat(result).isEqualTo(element);
    }

}