package software.plusminus.patch.service.patcher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.patch.helpers.TestEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionPatcherTest {

    private static final List<String> TARGET_LIST = Arrays.asList("key1:value1", "key2:value2", "key3:value3");
    private static final List<String> SOURCE_LIST = Arrays.asList("key1:updatedValue1", "key2:", "key4:addedValue4");

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private CollectionElementPatcher elementPatcher;
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private CollectionElementPatcher nullElementPatcher;
    @Spy
    private List<CollectionElementPatcher> elementPatchers = new ArrayList<>();

    @InjectMocks
    private CollectionPatcher patcher;

    private TestEntity source;
    private TestEntity target;

    @Before
    public void before() {
        elementPatchers.clear();
        elementPatchers.add(nullElementPatcher);
        elementPatchers.add(elementPatcher);

        target = new TestEntity();
        target.getList().addAll(TARGET_LIST);
        source = new TestEntity();
        source.getList().addAll(SOURCE_LIST);

        when(elementPatcher.toKey(any(), any(), eq("key1:value1"))).thenReturn("key1");
        when(elementPatcher.toKey(any(), any(), eq("key2:value2"))).thenReturn("key2");
        when(elementPatcher.toKey(any(), any(), eq("key3:value3"))).thenReturn("key3");
        when(elementPatcher.toKey(any(), any(), eq("key1:updatedValue1"))).thenReturn("key1");
        when(elementPatcher.toKey(any(), any(), eq("key2:"))).thenReturn("key2");
        when(elementPatcher.toKey(any(), any(), eq("key4:addedValue4"))).thenReturn("key4");
        when(elementPatcher.toValue(any(), any(), eq("key2:"))).thenReturn(null);
    }

    @Test
    public void patch() throws NoSuchFieldException {
        patcher.patch(source, target);

        assertThat(source.getList())
                .containsExactly("key1:updatedValue1", "key3:value3", "key4:addedValue4");
        verifyNullElementPatcher();
        verifyElementPatcher();
    }

    private void verifyElementPatcher() throws NoSuchFieldException {
        Field field = TestEntity.class.getDeclaredField("list");

        verify(elementPatcher).toKey(field, SOURCE_LIST, "key1:updatedValue1");
        verify(elementPatcher).toKey(field, SOURCE_LIST, "key2:");
        verify(elementPatcher).toKey(field, SOURCE_LIST, "key4:addedValue4");
        verify(elementPatcher).toKey(field, TARGET_LIST, "key1:value1");
        verify(elementPatcher).toKey(field, TARGET_LIST, "key2:value2");
        verify(elementPatcher).toKey(field, TARGET_LIST, "key3:value3");

        verify(elementPatcher).toValue(field, SOURCE_LIST, "key1:updatedValue1");
        verify(elementPatcher).toValue(field, SOURCE_LIST, "key2:");
        verify(elementPatcher).toValue(field, SOURCE_LIST, "key4:addedValue4");
        verify(elementPatcher).toValue(field, TARGET_LIST, "key1:value1");
        verify(elementPatcher).toValue(field, TARGET_LIST, "key2:value2");
        verify(elementPatcher).toValue(field, TARGET_LIST, "key3:value3");

        verifyNoMoreInteractions(elementPatcher);
    }

    private void verifyNullElementPatcher() throws NoSuchFieldException {
        Field field = TestEntity.class.getDeclaredField("list");

        verify(nullElementPatcher).toKey(field, SOURCE_LIST, "key1:updatedValue1");
        verify(nullElementPatcher).toKey(field, SOURCE_LIST, "key2:");
        verify(nullElementPatcher).toKey(field, SOURCE_LIST, "key4:addedValue4");
        verify(nullElementPatcher).toKey(field, TARGET_LIST, "key1:value1");
        verify(nullElementPatcher).toKey(field, TARGET_LIST, "key2:value2");
        verify(nullElementPatcher).toKey(field, TARGET_LIST, "key3:value3");

        verifyNoMoreInteractions(nullElementPatcher);
    }
}