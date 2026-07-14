package software.plusminus.patch.service.patcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CollectionElementPatcherTest {

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private CollectionElementPatcher patcher;

    @Test
    public void toValue() {
        String element = "someElement";
        String result = patcher.toValue(null, null, element);
        assertThat(result).isEqualTo(element);
    }

}