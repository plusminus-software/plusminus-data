package software.plusminus.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.plusminus.listener.fixtures.ChildEntity;
import software.plusminus.listener.fixtures.TestCreateListener;
import software.plusminus.listener.fixtures.TestEntity;
import software.plusminus.listener.fixtures.TestReadListener;
import software.plusminus.listener.fixtures.TestWriteListener;
import software.plusminus.listener.service.ListenerService;

import java.util.stream.Stream;

import static software.plusminus.check.Checks.check;

@SpringBootTest
class ListenerServiceTest {

    @Autowired
    private ListenerService listenerService;
    @Autowired
    private TestReadListener testReadListener;
    @Autowired
    private TestCreateListener testCreateListener;
    @Autowired
    private TestWriteListener testWriteListener;

    @BeforeEach
    void beforeEach() {
        testReadListener.reset();
        testCreateListener.reset();
        testWriteListener.reset();
    }

    @ParameterizedTest
    @MethodSource
    void onRead(Object object,
                boolean readListenerTriggered,
                boolean createListenerTriggered,
                DataAction writeListenerTriggered) {
        listenerService.afterRead(object);
        check(testReadListener.isTriggered()).is(readListenerTriggered);
        check(testCreateListener.isTriggered()).is(createListenerTriggered);
        check(testWriteListener.getLastAction()).is(writeListenerTriggered);
    }

    static Stream<Arguments> onRead() {
        return Stream.of(
                Arguments.of(new Object(), false, false, null),
                Arguments.of(new TestEntity(), true, false, null),
                Arguments.of(new ChildEntity(), true, false, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void onCreate(Object object,
                  boolean readListenerTriggered,
                  boolean createListenerTriggered,
                  DataAction writeListenerTriggered) {
        listenerService.beforeCreate(object);
        check(testReadListener.isTriggered()).is(readListenerTriggered);
        check(testCreateListener.isTriggered()).is(createListenerTriggered);
        check(testWriteListener.getLastAction()).is(writeListenerTriggered);
    }

    static Stream<Arguments> onCreate() {
        return Stream.of(
                Arguments.of(new Object(), false, false, null),
                Arguments.of(new TestEntity(), false, true, DataAction.CREATE),
                Arguments.of(new ChildEntity(), false, true, DataAction.CREATE)
        );
    }
}