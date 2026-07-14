package software.plusminus.json.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import software.plusminus.json.model.Classable;
import software.plusminus.metadata.MetadataContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanIdResolverTest {

    static {
        MetadataContext.addClass(BeanIdBaseEntity.class);
        MetadataContext.addClass(BeanIdEntity.class);
    }

    private ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void marshalling() throws IOException {
        BeanIdEntity testEntity = new BeanIdEntity();
        String json = mapper.writeValueAsString(testEntity);
        assertThat(json).isEqualTo(getJson("BeanIdEntity"));
    }
    
    @Test
    public void unmarshalling() throws IOException {
        String json = getJson("BeanIdEntity");
        BeanIdBaseEntity entity = mapper.readValue(json, BeanIdBaseEntity.class);
        assertThat(entity.getClass()).isEqualTo(BeanIdEntity.class);
    }

    @Test
    public void unmarshallingOfSuperClass() throws IOException {
        String json = "{\"class\":\"BeanIdBaseEntity\"}";
        BeanIdBaseEntity entity = mapper.readValue(json, BeanIdBaseEntity.class);
        assertThat(entity.getClass()).isEqualTo(BeanIdBaseEntity.class);
    }
    
    private String getJson(String className) {
        return "{\"class\":\"" + className + "\",\"field\":\"value\"}";
    }

    static class BeanIdBaseEntity implements Classable {
    }
    
    static class BeanIdEntity extends BeanIdBaseEntity {
        
        private String field = "value";

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
