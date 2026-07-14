package software.plusminus.tenant.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.plusminus.tenant.fixtures.TestEntity;
import software.plusminus.tenant.fixtures.TestRepository;
import software.plusminus.test.IntegrationTest;

import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

class TenantFilterTest extends IntegrationTest {

    @Autowired
    private TenantProvider firstProvider;
    @Autowired
    private TestRepository repository;
    @Autowired
    private TenantListener tenantListener;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void page() {
        TestEntity entity1 = new TestEntity();
        entity1.setMyField("first");
        entity1.setTenant("firstTenant");
        TestEntity entity2 = new TestEntity();
        entity2.setMyField("second");
        entity2.setTenant("secondTenant");
        tenantListener.runWithoutTenantCheck(() -> {
            repository.save(entity1);
            repository.save(entity2);
        });
        when(firstProvider.currentTenant()).thenReturn("firstTenant");

        Page<TestEntity> page = web().pageTemplate().getForGenericObject(web().url() + "/test",
                Page.class,
                TestEntity.class);

        check(page).isNotNull();
        check(page.getTotalElements()).is(1L);
        check(page.getContent().get(0).getMyField()).is("first");
    }

    @Test
    void byId() {
        TestEntity entity = new TestEntity();
        entity.setMyField("first");
        entity.setTenant("firstTenant");
        tenantListener.runWithoutTenantCheck(() -> repository.save(entity));
        when(firstProvider.currentTenant()).thenReturn("firstTenant");

        TestEntity response = restTemplate.getForObject(
                web().url() + "/test/" + entity.getId(),
                TestEntity.class
        );

        check(response).isNotNull();
        check(response.getId()).is(1L);
        check(response.getTenant()).is("firstTenant");
        check(response.getMyField()).is("first");
    }

    @Test
    void notFound() {
        TestEntity entity = new TestEntity();
        entity.setMyField("first");
        entity.setTenant("firstTenant");
        tenantListener.runWithoutTenantCheck(() -> repository.save(entity));
        when(firstProvider.currentTenant()).thenReturn("secondTenant");

        ResponseEntity<TestEntity> response = restTemplate.getForEntity(
                web().url() + "/test/" + entity.getId(),
                TestEntity.class
        );

        check(response.getStatusCode()).is(HttpStatus.NOT_FOUND);
    }
}
