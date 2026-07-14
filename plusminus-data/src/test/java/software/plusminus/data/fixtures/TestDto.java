package software.plusminus.data.fixtures;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class TestDto {

    @Id
    private Long id;
    private String myField;

}
