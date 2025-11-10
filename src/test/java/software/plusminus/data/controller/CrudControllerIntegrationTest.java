package software.plusminus.data.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import software.plusminus.check.util.JsonUtils;
import software.plusminus.data.service.CrudService;
import software.plusminus.data.service.DataService;
import software.plusminus.fixtures.TestController;
import software.plusminus.fixtures.TestEntity;
import software.plusminus.util.ResourceUtils;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static software.plusminus.check.Checks.check;

@RunWith(SpringRunner.class)
@WebMvcTest(TestController.class)
@ActiveProfiles("test")
public class CrudControllerIntegrationTest {

    private static final String PATH = "/test";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CrudService<TestEntity, Long> crudService;
    @MockBean
    private DataService dataService;
    @Captor
    private ArgumentCaptor<TestEntity> captor;

    @Test
    public void getByIdReturnsCorrectResponse() throws Exception {
        String json = readEntity();
        TestEntity entity = JsonUtils.fromJson(json, TestEntity.class);
        when(crudService.getById(2L)).thenReturn(entity);

        String body = mvc.perform(get("/test/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        check(body).is(json);
    }

    @Test
    public void getWithPagingReturnsPage() throws Exception {
        String json = readEntity();
        Pageable pageable = PageRequest.of(2, 5);
        TestEntity entity = JsonUtils.fromJson(json, TestEntity.class);
        when(crudService.getPage(pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(entity), pageable, 100));

        String body = mvc.perform(get("/test?page=2&size=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        check(body).isJson().ignoringFieldsOrder().is("/json/test-entity-page.json");
    }

    @Test
    public void postReturnsCreatedEntity() throws Exception {
        String json = readEntity();
        json = json.replaceAll("\"id\": 2,", "");
        TestEntity entity = JsonUtils.fromJson(json, TestEntity.class);
        when(crudService.create(entity)).thenReturn(entity);

        String body = mvc.perform(post(PATH)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        check(body).is(json);
        verify(crudService).create(captor.capture());
        check(captor.getValue()).is(json);
    }

    @Test
    public void putReturnsUpdatedEntity() throws Exception {
        String json = readEntity();
        TestEntity entity = JsonUtils.fromJson(json, TestEntity.class);
        when(crudService.update(entity)).thenReturn(entity);

        String body = mvc.perform(put(PATH)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        check(body).is(json);
        verify(crudService).update(captor.capture());
        check(captor.getValue()).is(json);
    }

    @Test
    public void patchReturnsPatchedEntity() throws Exception {
        String json = readEntity();
        TestEntity entity = JsonUtils.fromJson(json, TestEntity.class);
        when(crudService.patch(entity)).thenReturn(entity);

        String body = mvc.perform(patch(PATH)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        check(body).is(json);
        verify(crudService).patch(captor.capture());
        check(captor.getValue()).is(json);
    }

    @Test
    public void deleteRemovesEntity() throws Exception {
        String json = readEntity();

        mvc.perform(delete(PATH)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(crudService).delete(captor.capture());
        check(captor.getValue()).is(json);
    }
    
    private String readEntity() {
        return ResourceUtils.toString("/json/test-entity.json");
    }
}