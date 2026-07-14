package software.plusminus.tenant.fixtures;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.plusminus.data.controller.CrudController;

@RestController
@RequestMapping("/test")
public class TestCrudController extends CrudController<TestEntity, Long> {
}