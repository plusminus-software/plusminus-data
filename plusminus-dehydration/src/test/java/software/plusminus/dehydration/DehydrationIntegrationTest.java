package software.plusminus.dehydration;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import software.plusminus.dehydration.fixtures.A;
import software.plusminus.dehydration.fixtures.B;
import software.plusminus.dehydration.fixtures.C;
import software.plusminus.test.IntegrationTest;
import software.plusminus.test.helper.data.TestEntityManager;
import software.plusminus.test.helper.data.TestTransaction;
import software.plusminus.util.ResourceUtils;

import java.util.UUID;

import static software.plusminus.check.Checks.check;

public class DehydrationIntegrationTest extends IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestEntityManager entityManager;

    private A entityA;
    private B entityB;
    private C entityC;

    @Before
    public void before() {
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

        TestTransaction tx = data().transaction();

        tx.run(() -> {
            entityManager.persist(entityA);
            entityManager.persist(entityB);
            entityManager.persist(entityC);
        });
    }

    @Test
    public void dehydratedResponse() {
        String body = restTemplate.getForObject("http://localhost:" + web().port() + "/test?dehydrate=true", String.class);

        String expected = String.format(
                ResourceUtils.toString("/dehydrated.json"),
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
}
