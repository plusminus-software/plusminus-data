package software.plusminus.data.event.fixtures;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.plusminus.data.event.BeforeCreateEvent;
import software.plusminus.data.event.BeforeDeleteEvent;
import software.plusminus.data.event.BeforeUpdateEvent;
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

    private final List<TestEntity> beforeCreate = new ArrayList<>();
    private final List<TestEntity> beforeUpdate = new ArrayList<>();
    private final List<TestEntity> beforeDelete = new ArrayList<>();
    private final List<TestEntity> created = new ArrayList<>();
    private final List<TestEntity> updated = new ArrayList<>();
    private final List<TestEntity> deleted = new ArrayList<>();
    private final List<TestEntity> read = new ArrayList<>();

    @EventListener
    public void onBeforeCreate(BeforeCreateEvent<TestEntity> event) {
        beforeCreate.add(event.getEntity());
    }

    @EventListener
    public void onBeforeUpdate(BeforeUpdateEvent<TestEntity> event) {
        beforeUpdate.add(event.getEntity());
    }

    @EventListener
    public void onBeforeDelete(BeforeDeleteEvent<TestEntity> event) {
        beforeDelete.add(event.getEntity());
    }

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

    public List<TestEntity> getBeforeCreate() {
        return beforeCreate;
    }

    public List<TestEntity> getBeforeUpdate() {
        return beforeUpdate;
    }

    public List<TestEntity> getBeforeDelete() {
        return beforeDelete;
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
        beforeCreate.clear();
        beforeUpdate.clear();
        beforeDelete.clear();
        created.clear();
        updated.clear();
        deleted.clear();
        read.clear();
    }
}
