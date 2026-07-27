package software.plusminus.hibernate;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import javax.persistence.EntityManager;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class HibernateFilterServiceTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Session session;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Filter filter;

    private HibernateFilterService service;

    @Before
    public void before() {
        service = new HibernateFilterService(entityManager, singletonList(new TestFilter()));
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getDefinedFilterNames()).thenReturn(singleton("testFilter"));
        when(session.enableFilter("testFilter")).thenReturn(filter);
    }

    @Test
    public void enable() {
        service.enableFilters();

        verify(session).enableFilter("testFilter");
        verify(filter).setParameter("tenant", "localhost");
    }

    @Test
    public void disable() {
        service.disableFilters();

        verify(session).disableFilter("testFilter");
    }

    @Test
    public void enable_SkipsFilterNoEntityDeclares() {
        when(sessionFactory.getDefinedFilterNames()).thenReturn(emptySet());

        service.enableFilters();

        verify(session, never()).enableFilter(anyString());
    }

    @Test
    public void disable_SkipsFilterNoEntityDeclares() {
        when(sessionFactory.getDefinedFilterNames()).thenReturn(emptySet());

        service.disableFilters();

        verify(session, never()).disableFilter(anyString());
    }

    private static class TestFilter implements HibernateFilter {

        @Override
        public String filterName() {
            return "testFilter";
        }

        @Override
        public Map<String, Object> parameters() {
            return singletonMap("tenant", "localhost");
        }
    }
}
