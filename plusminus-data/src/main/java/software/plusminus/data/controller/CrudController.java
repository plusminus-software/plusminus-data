package software.plusminus.data.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import software.plusminus.data.model.Create;
import software.plusminus.data.model.Delete;
import software.plusminus.data.model.Patch;
import software.plusminus.data.model.Update;
import software.plusminus.data.service.CrudService;
import software.plusminus.data.service.DataService;
import software.plusminus.data.util.DataUtil;

import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
@NoArgsConstructor
@AllArgsConstructor
public abstract class CrudController<T, ID> {

    private CrudService<T, ID> service;

    @Autowired
    void init(@Nullable CrudService<T, ID> service, DataService dataService) {
        if (this.service == null) {
            this.service = DataUtil.provideCrudService(service, dataService, this, CrudController.class);
        }
    }

    @GetMapping("{id}")
    public T getById(@PathVariable ID id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<T> getPage(@PageableDefault(direction = Sort.Direction.DESC) Pageable pageable) {
        return service.getPage(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public T create(@Validated(Create.class) @RequestBody T object) {
        return service.create(object);
    }

    @PutMapping
    public T update(@Validated(Update.class) @RequestBody T object) {
        return service.update(object);
    }

    @PatchMapping
    public T patch(@Validated(Patch.class) @RequestBody T patch) {
        return service.patch(patch);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Validated(Delete.class) @RequestBody T object) {
        service.delete(object);
    }
}
