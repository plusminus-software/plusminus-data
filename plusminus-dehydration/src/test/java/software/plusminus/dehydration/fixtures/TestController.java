package software.plusminus.dehydration.fixtures;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.plusminus.json.model.ApiObject;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;

@AllArgsConstructor
@RestController
public class TestController {

    private EntityManager entityManager;

    @GetMapping("/test")
    public List<ApiObject> test() {
        return Arrays.asList(
                entityManager.find(A.class, 1L),
                entityManager.find(B.class, 1L),
                entityManager.find(C.class, 1L)
        );
    }
}
