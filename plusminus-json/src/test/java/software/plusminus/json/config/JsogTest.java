package software.plusminus.json.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.Test;
import software.plusminus.json.model.Jsog;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class JsogTest {

    @Test
    public void structure_IsSerializable_ForRecursiveDependenciesTroughJsog() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsogFoo foo = new JsogFoo();
        JsogBar bar = new JsogBar();
        foo.setObject(bar);
        bar.setFoo(foo);

        String json = mapper.writeValueAsString(bar);
        JsogBar result = mapper.readValue(json, JsogBar.class);

        assertThat(result).isOfAnyClassIn(JsogBar.class);
        assertThat(result.foo).isOfAnyClassIn(JsogFoo.class);
        assertThat(result.foo.object).isSameAs(result);
        assertThat(result.foo.roo).isEqualTo("roo");
    }

    static class JsogBaseEntity implements Jsog {
    }

    @Data
    static class JsogFoo extends JsogBaseEntity {
        private JsogBaseEntity object;
        private String roo = "roo";
    }

    @Data
    static class JsogBar extends JsogBaseEntity {
        private JsogFoo foo;
    }
}
