package software.plusminus.data.service;

import lombok.Data;
import org.junit.Test;
import software.plusminus.data.annotation.ModificationTime;
import software.plusminus.data.event.BeforeCreateEvent;
import software.plusminus.data.event.BeforeUpdateEvent;

import java.time.LocalDateTime;

import static software.plusminus.check.Checks.check;

public class ModificationTimeListenerTest {

    private ModificationTimeListener listener = new ModificationTimeListener();

    @Test
    public void populatesModificationTimeOnCreate() {
        TimedObject object = new TimedObject();

        listener.onCreate(new BeforeCreateEvent<>(object));

        check(object.getModificationTime()).isRecent();
    }

    @Test
    public void overwritesModificationTimeOnUpdate() {
        TimedObject object = new TimedObject();
        LocalDateTime previous = LocalDateTime.now().minusDays(1);
        object.setModificationTime(previous);

        listener.onUpdate(new BeforeUpdateEvent<>(object));

        check(object.getModificationTime().isAfter(previous)).is(true);
    }

    @Test
    public void ignoresObjectWithoutModificationTimeField() {
        SimpleObject object = new SimpleObject();
        object.setMyField("first");

        listener.onCreate(new BeforeCreateEvent<>(object));

        check(object.getMyField()).is("first");
    }

    @Data
    private static class TimedObject {

        @ModificationTime
        private LocalDateTime modificationTime;

        private String myField;
    }

    @Data
    private static class SimpleObject {

        private String myField;
    }
}
