package software.plusminus.sync.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import software.plusminus.check.util.JsonUtil;
import software.plusminus.data.service.DataService;
import software.plusminus.security.Security;
import software.plusminus.sync.SyncIntegrationTest;
import software.plusminus.sync.TestEntity;
import software.plusminus.sync.dto.Sync;
import software.plusminus.sync.dto.SyncType;
import software.plusminus.sync.models.Product;
import software.plusminus.sync.models.ProductOutcome;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static software.plusminus.check.Checks.check;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class SyncControllerIntegrationTest extends SyncIntegrationTest {

    private static final String TENANT = "localhost";
    private static final String CURRENT_DEVICE = "CurrentDevice";
    private static final String OTHER_DEVICE = "OtherDevice";
    private static final UUID TRANSACTION_ID = UUID.fromString("3a37e67d-a8b2-4c35-9e6f-a4e4b686ffb5");

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataService dataService;

    private TestEntity entity1;
    private TestEntity entity2;
    @SuppressWarnings("squid:S1450")
    private TestEntity entityWithUnknownTenant;
    @SuppressWarnings("squid:S1450")
    private TestEntity entityWithoutTenant;
    private TestEntity entitySoftlyDeleted;

    @Before
    public void before() {
        doReturn(TENANT).when(issuerContext()).get();

        entity1 = readTestEntity();
        entity1.setId(null);
        entity1.setVersion(null);
        entity1.setTenant(TENANT);
        create(OTHER_DEVICE, entity1);

        entity2 = readTestEntity();
        entity2.setId(null);
        entity2.setVersion(null);
        entity2.setTenant(TENANT);
        create(OTHER_DEVICE, entity2);

        entityWithUnknownTenant = readTestEntity();
        entityWithUnknownTenant.setId(null);
        entityWithUnknownTenant.setVersion(null);
        entityWithUnknownTenant.setTenant("Unknown tenant");
        create(OTHER_DEVICE, entityWithUnknownTenant);

        entityWithoutTenant = readTestEntity();
        entityWithoutTenant.setId(null);
        entityWithoutTenant.setVersion(null);
        entityWithoutTenant.setTenant("");
        create(OTHER_DEVICE, entityWithoutTenant);

        entitySoftlyDeleted = readTestEntity();
        entitySoftlyDeleted.setId(null);
        entitySoftlyDeleted.setVersion(null);
        entitySoftlyDeleted.setTenant(TENANT);
        entitySoftlyDeleted.setDeleted(Boolean.TRUE);
        create(OTHER_DEVICE, entitySoftlyDeleted);
    }

    @Test
    public void read() {
        List<Sync<TestEntity>> actions = Arrays.asList(
                Sync.of(entitySoftlyDeleted, SyncType.CREATE, 5L, null),
                Sync.of(entity2, SyncType.CREATE, 2L, null));

        ResponseEntity<String> response = httpGet(
                "/sync?types=TestEntity&excludeCurrentDevice=false&offset=1&size=10&direction=DESC");

        check(response.getStatusCodeValue()).is(200);
        check(response.getBody()).isJson().is(JsonUtil.toJson(actions));
    }

    @Test
    public void readWithDefaultParameters() {
        List<Sync<TestEntity>> actions = Arrays.asList(
                Sync.of(entity1, SyncType.CREATE, 1L, null),
                Sync.of(entity2, SyncType.CREATE, 2L, null),
                Sync.of(entitySoftlyDeleted, SyncType.CREATE, 5L, null));

        ResponseEntity<String> response = httpGet("/sync?types=TestEntity");

        check(response.getStatusCodeValue()).is(200);
        check(response.getBody()).isJson().is(JsonUtil.toJson(actions));
    }

    @Test
    public void readUpdated() {
        entity2.setMyField("updated");
        entity2 = update(OTHER_DEVICE, entity2);

        List<Sync<TestEntity>> actions = Collections.singletonList(
                Sync.of(entity2, SyncType.UPDATE, 6L, null));

        ResponseEntity<String> response = httpGet("/sync?types=TestEntity&offset=5");

        check(response.getStatusCodeValue()).is(200);
        check(response.getBody()).isJson().is(JsonUtil.toJson(actions));
    }

    @Test
    public void readDeleted() {
        remove(OTHER_DEVICE, entity2);

        ResponseEntity<String> response = httpGet("/sync?types=TestEntity&offset=5");

        check(response.getStatusCodeValue()).is(200);
        check(response.getBody()).isJson().is("/json/deleted.json");
    }

    @Test
    public void write() throws Exception {
        TestEntity entityOne = readTestEntity();
        entityOne.setId(1L);
        entityOne.setVersion(0L);
        entityOne.setTenant(TENANT);

        TestEntity entityTwo = readTestEntity();
        entityTwo.setId(2L);
        entityTwo.setVersion(0L);
        entityTwo.setTenant(TENANT);

        TestEntity newEntity = new TestEntity();
        newEntity.setMyField("new entity field");

        List<Sync<TestEntity>> items = Arrays.asList(
                Sync.of(entityOne, SyncType.UPDATE, null, null),
                Sync.of(entityTwo, SyncType.DELETE, null, null),
                Sync.of(newEntity, SyncType.CREATE, null, null));
        String json = objectMapper.writerFor(new TypeReference<List<Sync<TestEntity>>>() {})
                .writeValueAsString(items);

        ResponseEntity<String> response = httpPost("/sync?transaction=" + TRANSACTION_ID, json);

        check(response.getStatusCodeValue()).is(200);
        assertThat(response.getBody()).isNotEmpty();
        check(response.getBody()).isJson().is("/json/write-response.json");
    }

    @Test
    public void writeWithDependencies() {
        String json = JsonUtil.readJson("/json/write-request-with-inner-entity.json");

        ResponseEntity<String> response = httpPost("/sync", json);

        check(response.getStatusCodeValue()).is(200);
        assertThat(response.getBody()).isNotEmpty();
        check(response.getBody()).isJson().is("/json/write-response-with-dependencies.json");
    }

    @Test
    public void turnBackExistingObjectWithInnerEntityOnCreate() throws Exception {
        UUID uuid = UUID.randomUUID();
        Product productIndDb = new Product();
        productIndDb.setUuid(uuid);
        run(TENANT, CURRENT_DEVICE, () -> dataService.create(productIndDb));
        ProductOutcome productOutcomeInDb = new ProductOutcome();
        productOutcomeInDb.setUuid(uuid);
        productOutcomeInDb.setProduct(productIndDb);
        run(TENANT, CURRENT_DEVICE, () -> dataService.create(productOutcomeInDb));

        Product product = new Product();
        product.setUuid(uuid);
        product.setTenant(TENANT);

        String json = objectMapper.writeValueAsString(
                Collections.singletonList(Sync.of(product, SyncType.CREATE, null, null)));

        ResponseEntity<String> response = httpPost("/sync", json);

        check(response.getStatusCodeValue()).is(200);
        assertThat(response.getBody()).isNotEmpty();
    }

    private ResponseEntity<String> httpGet(String path) {
        return restTemplate().exchange(web().url() + path, HttpMethod.GET,
                new HttpEntity<>(authHeaders()), String.class);
    }

    private ResponseEntity<String> httpPost(String path, String content) {
        HttpHeaders headers = authHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate().exchange(web().url() + path, HttpMethod.POST,
                new HttpEntity<>(content, headers), String.class);
    }

    private TestRestTemplate restTemplate() {
        return new TestRestTemplate(restTemplateBuilder);
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + security().generateToken(security(TENANT, CURRENT_DEVICE)));
        return headers;
    }

    private TestEntity readTestEntity() {
        return JsonUtil.fromJson("/json/entity.json", TestEntity.class);
    }

    private Security security(String tenant, String device) {
        return Security.builder()
                .parameters(ImmutableMap.of("tenant", tenant, "device", device))
                .build();
    }

    private void create(String device, TestEntity entity) {
        run(entity.getTenant(), device, () -> dataService.create(entity));
    }

    private TestEntity update(String device, TestEntity entity) {
        AtomicReference<TestEntity> container = new AtomicReference<>();
        run(entity.getTenant(), device, () -> {
            TestEntity updated = dataService.update(entity);
            container.set(updated);
        });
        return container.get();
    }

    private void remove(String device, TestEntity entity) {
        run(entity.getTenant(), device, () -> dataService.delete(dataService.update(entity)));
    }

    private void run(String tenant, String device, Runnable runnable) {
        doReturn(device).when(deviceContext()).get();
        doReturn(tenant).when(tenantContext()).get();
        data().transaction().run(runnable);
        doCallRealMethod().when(tenantContext()).get();
        doCallRealMethod().when(deviceContext()).get();
    }
}
