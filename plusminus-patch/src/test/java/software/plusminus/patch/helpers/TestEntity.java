package software.plusminus.patch.helpers;

import lombok.Data;
import software.plusminus.patch.annotation.StringCollectionPatch;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestEntity {

    private String myField;

    @StringCollectionPatch(splitter = ":")
    private List<String> list = new ArrayList<>();

}