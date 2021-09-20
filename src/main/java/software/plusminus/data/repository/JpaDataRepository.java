package software.plusminus.data.repository;

import company.plusminus.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.data.service.entity.EntityService;

import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@SuppressWarnings("squid:S00119")
public class JpaDataRepository implements DataRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EntityService entityService;

    @Override
    @Transactional(readOnly = true)
    public <T, ID> T findById(Class<T> type, ID id) {
        return entityManager.find(type, id);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> Page<T> findAll(Class<T> type, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional
    public <T> T save(T entity) {
        if (EntityUtils.findId(entity) == null) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public <T> T deepSave(T entity) {
        Set<?> subentities = entityService.findSubentities(entity);
        subentities.forEach(this::save);
        return save(entity);
    }

    @Override
    @Transactional
    public <T> void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

}
