package software.plusminus.sync.service.jsog;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;

import java.util.function.BiPredicate;

@Service
public class SyncJsonService {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.addMixIn(Object.class, DynamicFilterMixin.class);
    }

    public String toJson(Object object, BiPredicate<Object, PropertyWriter> filter) {
        FilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("DynamicFilter", new DynamicFilter(filter));
        try {
            return MAPPER.writer(filterProvider).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e);
        }
    }

    private static final class DynamicFilter extends SimpleBeanPropertyFilter {

        private BiPredicate<Object, PropertyWriter> filter;

        private DynamicFilter(BiPredicate<Object, PropertyWriter> filter) {
            this.filter = filter;
        }

        @Override
        public void serializeAsField(Object pojo,
                                     JsonGenerator jgen,
                                     SerializerProvider provider,
                                     PropertyWriter writer) throws Exception {
            if (filter.test(pojo, writer)) {
                super.serializeAsField(pojo, jgen, provider, writer);
            }
        }
    }

    @JsonFilter("DynamicFilter")
    private interface DynamicFilterMixin {
    }

}
