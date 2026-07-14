package software.plusminus.softdelete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Profile("test")
public class TestCrudController {
    
    @Autowired
    private TestRepository repository;
    
    @GetMapping
    public Page<TestEntity> getPage(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
}