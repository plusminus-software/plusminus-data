package software.plusminus.hibernate;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.scope.events.InvocationFinalizedEvent;
import software.plusminus.scope.events.InvocationStartedEvent;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HibernateFilterListenerTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Session session;
    @Mock
    private HibernateFilterService filterService;
    @Mock
    private InvocationStartedEvent<?> startedEvent;
    @Mock
    private InvocationFinalizedEvent<?> finalizedEvent;

    private HibernateFilterListener listener;

    @Before
    public void before() {
        listener = new HibernateFilterListener(entityManager, filterService);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
    }

    @Test
    public void started() {
        listener.onInvocationStarted(startedEvent);

        verify(filterService).enableFilters(session);
    }

    @Test
    public void finalized() {
        listener.onInvocationFinalized(finalizedEvent);

        verify(filterService).disableFilters(session);
    }
}
