package software.plusminus.crud.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import software.plusminus.crud.repository.CrudRepository;
import software.plusminus.data.exception.NotFoundException;
import software.plusminus.data.model.Update;
import software.plusminus.data.service.DataContext;
import software.plusminus.data.util.DataUtil;
import software.plusminus.listener.service.ListenerService;
import software.plusminus.patch.service.PatchService;

import javax.annotation.Nullable;
import javax.validation.Validator;

@SuppressWarnings("java:S119")
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractCrudService<T, ID> implements CrudService<T, ID>  {

    private Validator validator;
    private PatchService patchService;
    private ListenerService listenerService;
    private CrudRepository<T, ID> repository;

    @Autowired
    void init(Validator validator,
              PatchService patchService,
              ListenerService listenerService,
              @Nullable CrudRepository<T, ID> repository,
              DataContext dataContext) {
        if (this.validator == null) {
            this.validator = validator;
        }
        if (this.patchService == null) {
            this.patchService = patchService;
        }
        if (this.listenerService == null) {
            this.listenerService = listenerService;
        }
        if (this.repository == null) {
            this.repository = DataUtil.provideCrudRepository(repository, dataContext, this, CrudService.class);
        }
    }

    @Override
    public T getById(ID id) {
        T object = repository.getById(id);
        if (object == null) {
            throw new NotFoundException("Can't find object with id " + id);
        }
        listenerService.afterRead(object);
        return object;
    }

    @Override
    public Page<T> getPage(Pageable pageable) {
        Page<T> page = repository.findAll(pageable);
        page.forEach(listenerService::afterRead);
        return page;
    }

    @Override
    public T create(T object) {
        DataUtil.verifyOnCreate(object);
        listenerService.beforeCreate(object);
        T created = repository.save(object);
        listenerService.afterCreate(created);
        return created;
    }

    @Override
    public T update(T object) {
        DataUtil.verifyOnUpdate(object);
        listenerService.beforeUpdate(object);
        T updated = repository.save(object);
        listenerService.afterUpdate(updated);
        return updated;
    }

    @Override
    public T patch(T patch) {
        ID id = DataUtil.verifyOnPatch(patch);
        T target = getById(id);
        listenerService.beforePatch(patch);
        patchService.patch(patch, target);
        validator.validate(target, Update.class);
        T saved = repository.save(target);
        listenerService.afterPatch(saved);
        return saved;
    }

    @Override
    public void delete(T object) {
        DataUtil.verifyOnDelete(object);
        listenerService.beforeDelete(object);
        repository.delete(object);
        listenerService.afterDelete(object);
    }
}
