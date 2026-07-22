package software.plusminus.tenant.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.plusminus.tenant.fixtures.TestEntity;
import software.plusminus.tenant.fixtures.TestRepository;
import software.plusminus.test.IntegrationTest;

import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

class TenantListenerTest extends IntegrationTest {

    @Autowired
    private TenantProvider firstProvider;
    @Autowired
    private TestRepository repository;
    @Autowired
    private TenantListener tenantListener;
    @Autowired
    private TestRestTemplate restTemplate;

    @ParameterizedTest
    @CsvSource({
        "firstTenant,firstTenant,firstTenant,false",
        "secondTenant,firstTenant,,true",
        ",firstTenant,firstTenant,false",
        ",,,false"
    })
    void create(String objectTenant, String contextTenant, String expectedTenant, boolean error) {
        TestEntity entity = new TestEntity();
        entity.setMyField("first");
        entity.setTenant(objectTenant);
        when(firstProvider.currentTenant()).thenReturn(contextTenant);

        ResponseEntity<TestEntity> response = restTemplate
                .postForEntity(web().url() + "/test", entity, TestEntity.class);

        if (error) {
            check(response.getStatusCode()).is(HttpStatus.BAD_REQUEST);
            check(repository.count()).is(0);
            return;
        }
        check(response.getStatusCode()).is(HttpStatus.CREATED);
        check(repository.count()).is(1);
        TestEntity saved = repository.findAll().iterator().next();
        check(saved.getMyField()).is("first");
        check(saved.getTenant()).is(expectedTenant);
        check(response.getBody()).isNotNull();
        check(response.getBody().getMyField()).is("first");
        check(response.getBody().getTenant()).is(expectedTenant);
    }

    @ParameterizedTest
    @CsvSource({
        "firstTenant,firstTenant,true",
        "secondTenant,firstTenant,false",
        ",firstTenant,false",
        "firstTenant,,false",
        ",,true",
        "'','',true",
        "'',,true",
        ",'',true"
    })
    void readPage(String objectTenant, String contextTenant, boolean present) {
        TestEntity entity = new TestEntity();
        entity.setMyField("first");
        entity.setTenant(objectTenant);
        tenantListener.runWithoutTenantCheck(() -> repository.save(entity));
        when(firstProvider.currentTenant()).thenReturn(contextTenant);

        Page<TestEntity> page = web().pageTemplate().getForGenericObject(
                web().url() + "/test",
                Page.class,
                TestEntity.class
        );

        check(page.getTotalElements()).is(present ? 1 : 0);
        if (present) {
            check(page.getContent().get(0).getMyField()).is("first");
            check(page.getContent().get(0).getTenant()).is(objectTenant);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "firstTenant,firstTenant,true",
        "secondTenant,firstTenant,false",
        ",firstTenant,false",
        "firstTenant,,false",
        ",,true",
        "'','',true",
        "'',,true",
        ",'',true"
    })
    void readSingle(String objectTenant, String contextTenant, boolean present) {
        TestEntity entity = new TestEntity();
        entity.setMyField("first");
        entity.setTenant(objectTenant);
        tenantListener.runWithoutTenantCheck(() -> repository.save(entity));
        when(firstProvider.currentTenant()).thenReturn(contextTenant);

        ResponseEntity<TestEntity> response = restTemplate.getForEntity(
                web().url() + "/test/" + entity.getId(),
                TestEntity.class
        );

        if (!present) {
            check(response.getStatusCode()).is(HttpStatus.NOT_FOUND);
            return;
        }
        check(response.getStatusCode()).is(HttpStatus.OK);
        check(response.getBody().getId()).is(entity.getId());
        check(response.getBody().getTenant()).is(objectTenant);
        check(response.getBody().getMyField()).is("first");
    }

    @ParameterizedTest
    @CsvSource({
        "firstTenant,firstTenant,firstTenant,false",
        "secondTenant,firstTenant,,true",
        ",firstTenant,,false", //TODO check that entity in DB has the same tenant
        ",,,false"
    })
    void update(String objectTenant, String contextTenant, String expectedTenant, boolean error) {
        TestEntity entity = new TestEntity();
        entity.setMyField("first");
        entity.setTenant(objectTenant);
        tenantListener.runWithoutTenantCheck(() -> repository.save(entity));
        entity.setMyField("updated");
        when(firstProvider.currentTenant()).thenReturn(contextTenant);

        ResponseEntity<TestEntity> response = restTemplate.exchange(
                web().url() + "/test",
                HttpMethod.PUT,
                new HttpEntity<>(entity),
                TestEntity.class
        );

        if (error) {
            check(response.getStatusCode()).is(HttpStatus.BAD_REQUEST);
            check(repository.count()).is(1);
            return;
        }
        check(response.getStatusCode()).is(HttpStatus.OK);
        check(repository.count()).is(1);
        TestEntity saved = tenantListener.callWithoutTenantCheck(
                () -> repository.findAll().iterator().next());
        check(saved.getMyField()).is("updated");
        check(saved.getTenant()).is(expectedTenant);
        check(response.getBody()).isNotNull();
        check(response.getBody().getMyField()).is("updated");
        check(response.getBody().getTenant()).is(contextTenant);
    }

    @ParameterizedTest
    @CsvSource({
        "firstTenant,firstTenant,firstTenant,false",
        "secondTenant,firstTenant,,true",
        ",firstTenant,,true",
        ",,,false"
    })
    void patch(String objectTenant, String contextTenant, String expectedTenant, boolean error) {
        TestEntity entity = new TestEntity();
        entity.setMyField("first");
        entity.setTenant(objectTenant);
        tenantListener.runWithoutTenantCheck(() -> repository.save(entity));
        TestEntity patch = new TestEntity();
        patch.setId(entity.getId());
        patch.setTenant(objectTenant);
        patch.setMyField("patched");
        when(firstProvider.currentTenant()).thenReturn(contextTenant);

        ResponseEntity<TestEntity> response = restTemplate.exchange(
                web().url() + "/test",
                HttpMethod.PATCH,
                new HttpEntity<>(patch),
                TestEntity.class
        );

        if (error) {
            check(response.getStatusCode()).is(HttpStatus.NOT_FOUND);
            check(repository.count()).is(1);
            return;
        }
        check(response.getStatusCode()).is(HttpStatus.OK);
        check(repository.count()).is(1);
        TestEntity saved = repository.findAll().iterator().next();
        check(saved.getMyField()).is("patched");
        check(saved.getTenant()).is(expectedTenant);
        check(response.getBody()).isNotNull();
        check(response.getBody().getMyField()).is("patched");
        check(response.getBody().getTenant()).is(expectedTenant);
    }
}
