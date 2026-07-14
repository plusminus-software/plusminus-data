package software.plusminus.data.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.util.EntityUtils;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

@SuppressWarnings("java:S119")
@Repository
@ConditionalOnClass(JpaRepository.class)
public class JpaDataRepository implements DataRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public <T, ID> T getById(Class<T> type, ID id) {
        return entityManager.find(type, id);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> Page<T> findAll(Class<T> type, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(type);
        Root<T> root = query.from(type);
        query.select(root);
        applySorting(builder, query, root, pageable);
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        applyPaging(typedQuery, pageable);

        List<T> content = typedQuery.getResultList();
        long total = countTotalElements(builder, type);

        return new PageImpl<>(content, pageable, total);
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
    public <T> void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    private <T> void applySorting(CriteriaBuilder builder, CriteriaQuery<T> query,
                                  Root<T> root, Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            return;
        }
        List<Order> orders = pageable.getSort().stream()
                .map(order -> order.isAscending()
                        ? builder.asc(root.get(order.getProperty()))
                        : builder.desc(root.get(order.getProperty())))
                .collect(Collectors.toList());
        query.orderBy(orders);
    }

    private <T> void applyPaging(TypedQuery<T> typedQuery, Pageable pageable) {
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
    }

    private <T> long countTotalElements(CriteriaBuilder builder, Class<T> type) {
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(type);
        countQuery.select(builder.count(countRoot));
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
