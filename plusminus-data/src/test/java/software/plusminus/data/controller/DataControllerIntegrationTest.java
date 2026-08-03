package software.plusminus.data.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import software.plusminus.data.fixtures.DataEntity;
import software.plusminus.data.service.DataService;
import software.plusminus.metadata.MetadataContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = DataController.class, properties = "data.controller=true")
public class DataControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private DataService service;
    @Captor
    private ArgumentCaptor<Object> captor;

    @Before
    public void before() {
        MetadataContext.addClass(DataEntity.class);
    }

    @Test
    public void postDeserializesBodyIntoResolvedEntityType() throws Exception {
        DataEntity created = new DataEntity();
        created.setMyField("Some value");
        when(service.create(any())).thenReturn(created);

        mvc.perform(post("/data")
                .content("{\"class\":\"DataEntity\",\"myField\":\"Some value\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(service).create(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(DataEntity.class);
        assertThat(((DataEntity) captor.getValue()).getMyField()).isEqualTo("Some value");
    }

    @Test
    public void putDeserializesBodyIntoResolvedEntityType() throws Exception {
        DataEntity updated = new DataEntity();
        updated.setId(5L);
        updated.setMyField("Updated");
        when(service.update(any())).thenReturn(updated);

        mvc.perform(put("/data")
                .content("{\"class\":\"DataEntity\",\"id\":5,\"myField\":\"Updated\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).update(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(DataEntity.class);
        assertThat(((DataEntity) captor.getValue()).getId()).isEqualTo(5L);
    }

    @Test
    public void unknownTypeReturnsBadRequest() throws Exception {
        mvc.perform(post("/data")
                .content("{\"class\":\"MissingType\",\"myField\":\"x\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
