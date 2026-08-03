package software.plusminus.data.controller;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.DispatcherServlet;
import software.plusminus.data.exception.ClientDataException;
import software.plusminus.data.model.Create;
import software.plusminus.data.model.Delete;
import software.plusminus.data.model.Patch;
import software.plusminus.data.model.Update;
import software.plusminus.data.service.DataService;
import software.plusminus.json.model.Classable;
import software.plusminus.metadata.AmbiguousTypeException;
import software.plusminus.metadata.MetadataContext;

@SuppressWarnings({"java:S119", "ClassFanOutComplexity"})
@AllArgsConstructor
@RequestMapping("/data")
@ConditionalOnClass(DispatcherServlet.class)
@ConditionalOnProperty("data.controller")
@RestController
public class DataController {

    private DataService service;

    @GetMapping("{type}/{id}")
    public <T, ID> T get(@PathVariable String type,
                         @PathVariable ID id) {
        Class<T> clazz = resolveClass(type);
        return service.getById(clazz, id);
    }

    @GetMapping("{type}")
    public <T> Page<T> getPage(@PathVariable String type,
                               @PageableDefault(direction = Sort.Direction.DESC) Pageable pageable) {
        Class<T> clazz = resolveClass(type);
        return service.getPage(clazz, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public <T extends Classable> T post(@Validated(Create.class) @RequestBody T entity) {
        return service.create(entity);
    }

    @PutMapping
    public <T extends Classable> T put(@Validated(Update.class) @RequestBody T entity) {
        return service.update(entity);
    }

    @PatchMapping
    public <T extends Classable> T patch(@Validated(Patch.class) @RequestBody T entity) {
        return service.patch(entity);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public <T extends Classable> void delete(@Validated(Delete.class) @RequestBody T entity) {
        service.delete(entity);
    }

    private <T> Class<T> resolveClass(String type) {
        Class<T> clazz;
        try {
            clazz = MetadataContext.getClass(type);
        } catch (AmbiguousTypeException e) {
            throw new ClientDataException(e.getMessage());
        }
        if (clazz == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown type: " + type);
        }
        return clazz;
    }
}
