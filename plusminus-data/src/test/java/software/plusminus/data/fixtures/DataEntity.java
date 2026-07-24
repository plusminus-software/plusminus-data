package software.plusminus.data.fixtures;

import lombok.Data;
import software.plusminus.json.model.Classable;

@Data
public class DataEntity implements Classable {

    private Long id;
    private String myField;

}
