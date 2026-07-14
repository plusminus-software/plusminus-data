package software.plusminus.data.fixtures;

import lombok.Data;
import software.plusminus.data.model.Create;
import software.plusminus.data.model.Update;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Entity
public class TestEntity {

    @Null(groups = Create.class)
    @NotNull(groups = Update.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    private String myField;

    @ElementCollection
    private List<String> list = new ArrayList<>();

}