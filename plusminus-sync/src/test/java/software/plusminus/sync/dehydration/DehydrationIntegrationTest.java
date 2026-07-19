package software.plusminus.sync.dehydration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import software.plusminus.security.Security;
import software.plusminus.sync.SyncIntegrationTest;
import software.plusminus.util.ResourceUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static software.plusminus.check.Checks.check;

class DehydrationIntegrationTest extends SyncIntegrationTest {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private A entityA;
    private B entityB;
    private C entityC;

    @Override
    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
        doReturn("localhost").when(issuerContext()).get();
        entityA = new A();
        entityA.setName("a");
        entityA.setUuid(UUID.randomUUID());
        entityB = new B();
        entityB.setName("b");
        entityB.setUuid(UUID.randomUUID());
        entityC = new C();
        entityC.setName("c");
        entityC.setUuid(UUID.randomUUID());

        entityA.setEntityC(entityC);
        entityB.setEntityA(entityA);

        doReturn("localhost").when(tenantContext()).get();
        data().transaction().run(() ->
                Stream.of(entityA, entityB, entityC).forEach(dataService()::create));
        doCallRealMethod().when(tenantContext()).get();
    }

    @Test
    void dehydratedResponse() {
        TestRestTemplate testRestTemplate = new TestRestTemplate(restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(60))
                .setReadTimeout(Duration.ofSeconds(60)));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + generateToken());

        String body = testRestTemplate.exchange(
                "http://localhost:" + web().port() + "/sync?types=A&types=B&types=C"
                        + "&excludeCurrentDevice=false"
                        + "&dehydrate=true", HttpMethod.GET, new HttpEntity<>(headers),
                String.class).getBody();

        String expected = String.format(
                ResourceUtils.toString("/json/dehydrated.json"),
                entityA.getUuid(),
                entityB.getUuid(),
                entityC.getUuid(),
                entityB.getUuid(),
                entityA.getUuid(),
                entityC.getUuid(),
                entityA.getUuid()
        );
        check(body).isJson().is(expected);
    }

    private String generateToken() {
        return security().generateToken(Security.builder()
                .parameters(Collections.singletonMap("tenant", "localhost"))
                .build());
    }
}
