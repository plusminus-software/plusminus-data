package software.plusminus.hibernate;

import lombok.AllArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.persistence.EntityManager;

@AllArgsConstructor
@Service
public class HibernateFilterService {

    private EntityManager entityManager;
    private List<HibernateFilter> filters;

    public void enableFilters() {
        enableFilters(getSession());
    }
    
    public void enableFilters(Session session) {
        filters.stream()
                .filter(f -> isDefined(session, f.filterName()))
                .forEach(f -> {
                    Filter filter = session.enableFilter(f.filterName());
                    f.parameters().forEach(filter::setParameter);
                });
    }

    public void disableFilters() {
        disableFilters(getSession());
    }

    public void disableFilters(Session session) {
        filters.stream()
                .filter(f -> isDefined(session, f.filterName()))
                .forEach(f -> session.disableFilter(f.filterName()));
    }

    private boolean isDefined(Session session, String filterName) {
        return session.getSessionFactory()
                .getDefinedFilterNames()
                .contains(filterName);
    }

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }
    
}
