package software.plusminus.json.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import software.plusminus.json.config.BeanIdResolver;

@JsonPropertyOrder("class")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "class")
@JsonTypeIdResolver(BeanIdResolver.class)
public interface Classable {

    @JsonProperty("class")
    default String getClazz() {
        return getClass().getSimpleName();
    }
}
