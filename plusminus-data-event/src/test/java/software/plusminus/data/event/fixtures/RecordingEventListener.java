package software.plusminus.data.event.fixtures;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.plusminus.data.event.CreateEvent;
import software.plusminus.data.event.DeleteEvent;
import software.plusminus.data.event.ReadEvent;
import software.plusminus.data.event.UpdateEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Captures events for a concrete entity type, proving that {@code @EventListener} resolves
 * the generic entity type of each event.
 */
@Component
public class RecordingEventListener {

    private final List<TestEntity> created = new ArrayList<>();
    private final List<TestEntity> updated = new ArrayList<>();
    private final List<TestEntity> deleted = new ArrayList<>();
    private final List<TestEntity> read = new ArrayList<>();

    @EventListener
    public void onCreate(CreateEvent<TestEntity> event) {
        created.add(event.getEntity());
    }

    @EventListener
    public void onUpdate(UpdateEvent<TestEntity> event) {
        updated.add(event.getEntity());
    }

    @EventListener
    public void onDelete(DeleteEvent<TestEntity> event) {
        deleted.add(event.getEntity());
    }

    @EventListener
    public void onRead(ReadEvent<TestEntity> event) {
        read.add(event.getEntity());
    }

    public List<TestEntity> getCreated() {
        return created;
    }

    public List<TestEntity> getUpdated() {
        return updated;
    }

    public List<TestEntity> getDeleted() {
        return deleted;
    }

    public List<TestEntity> getRead() {
        return read;
    }

    public void clear() {
        created.clear();
        updated.clear();
        deleted.clear();
        read.clear();
    }
}
