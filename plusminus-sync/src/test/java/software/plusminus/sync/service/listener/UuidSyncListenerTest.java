package software.plusminus.sync.service.listener;

import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.data.repository.DataRepository;
import software.plusminus.json.annotation.Uuid;
import software.plusminus.json.model.ApiObject;
import software.plusminus.sync.EntityWithUuid;
import software.plusminus.sync.dto.Sync;
import software.plusminus.sync.dto.SyncType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class UuidSyncListenerTest {

    @Mock
    private DataRepository repository;
    @InjectMocks
    private UuidSyncListener listener;

    @Test
    public void onRead_GeneratesUuidForEntity() {
        EntityWithUuid entity = new EntityWithUuid();
        Sync<EntityWithUuid> sync = Sync.of(entity, SyncType.CREATE, null, null);

        listener.onRead(sync);

        assertThat(entity.getUuid()).isNotNull();
        verify(repository).save(entity);
    }

    @Test
    public void onRead_GeneratesUuidsForCollectionChildren() {
        ParentWithChildren parent = new ParentWithChildren();
        EntityWithUuid child = new EntityWithUuid();
        parent.getChildren().add(child);
        Sync<ParentWithChildren> sync = Sync.of(parent, SyncType.CREATE, null, null);

        listener.onRead(sync);

        assertThat(parent.getUuid()).isNotNull();
        assertThat(child.getUuid()).isNotNull();
        verify(repository).save(parent);
        verify(repository).save(child);
    }

    @Test
    public void onRead_SkipsDelete() {
        EntityWithUuid entity = new EntityWithUuid();
        Sync<EntityWithUuid> sync = Sync.of(entity, SyncType.DELETE, null, null);

        listener.onRead(sync);

        assertThat(entity.getUuid()).isNull();
        verifyZeroInteractions(repository);
    }

    @Data
    private static class ParentWithChildren implements ApiObject {

        @Uuid
        private UUID uuid;
        private List<EntityWithUuid> children = new ArrayList<>();

    }
}
