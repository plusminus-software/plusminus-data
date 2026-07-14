package software.plusminus.softdelete.interceptor;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.check.util.JsonUtil;
import software.plusminus.softdelete.TestEntity;

import java.util.stream.Stream;
import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SoftDeleteFilterIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private EntityManager entityManager;

    private TestEntity entity1;
    private TestEntity entity2;
    private TestEntity entityWithDeletedTrue;
    private TestEntity entityWithDeletedFalse;

    @Before
    public void before() {
        entity1 = readTestEntity();
        entity1.setId(null);

        entity2 = readTestEntity();
        entity2.setId(null);

        entityWithDeletedTrue = readTestEntity();
        entityWithDeletedTrue.setId(null);
        entityWithDeletedTrue.setDeleted(true);

        entityWithDeletedFalse = readTestEntity();
        entityWithDeletedFalse.setId(null);
        entityWithDeletedFalse.setDeleted(false);

        Stream.of(entity1, entity2, entityWithDeletedTrue, entityWithDeletedFalse)
                .forEach(entityManager::persist);
    }

    @Test
    public void filteredByTest() throws Exception {
        mvc.perform(get("/test?page=0&size=100"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", Is.is(3)))
                .andExpect(jsonPath("$.content[0].id", Is.is(1)))
                .andExpect(jsonPath("$.content[1].id", Is.is(2)))
                .andExpect(jsonPath("$.content[2].id", Is.is(4)));
    }

    private TestEntity readTestEntity() {
        return JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);
    }

}