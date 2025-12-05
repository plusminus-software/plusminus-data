package software.plusminus.data.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.crud.listener.CrudListenerContext;
import software.plusminus.data.exception.NotFoundException;
import software.plusminus.data.model.Update;
import software.plusminus.data.repository.CrudRepository;
import software.plusminus.data.repository.DataRepository;
import software.plusminus.data.repository.RepositoryContext;
import software.plusminus.data.util.DataUtil;
import software.plusminus.patch.service.PatchService;
import software.plusminus.util.EntityUtils;

import javax.annotation.Nullable;
import javax.validation.Validator;

@SuppressWarnings("java:S119")
@AllArgsConstructor
@Service
public class DataService {

    private Validator validator;
    private PatchService patchService;
    private CrudListenerContext listenerContext;
    private CrudServiceContext crudServiceContext;
    private RepositoryContext repositoryContext;
    private @Nullable DataRepository dataRepository;

    @Transactional(readOnly = true)
    public <T, ID> T getById(Class<T> type, ID id) {
        CrudService<T, ID> crudService = crudServiceContext.findService(type);
        if (crudService != null) {
            return crudService.getById(id);
        }
        T object;
        CrudRepository<T, ID> crudRepository = repositoryContext.findRepository(type);
        if (crudRepository != null) {
            object = crudRepository.getById(id);
        } else if (dataRepository != null) {
            object = dataRepository.getById(type, id);
        } else {
            throw new NotFoundException("Can't find repository for type " + type);
        }
        if (object == null) {
            throw new NotFoundException("Can't find object with id " + id);
        }
        listenerContext.onSingleRead(object);
        return object;
    }

    @Transactional(readOnly = true)
    public <T> Page<T> getPage(Class<T> type, Pageable pageable) {
        CrudService<T, ?> crudService = crudServiceContext.findService(type);
        if (crudService != null) {
            return crudService.getPage(pageable);
        }
        Page<T> page;
        CrudRepository<T, ?> crudRepository = repositoryContext.findRepository(type);
        if (crudRepository != null) {
            page = crudRepository.findAll(pageable);
        } else if (dataRepository != null) {
            page = dataRepository.findAll(type, pageable);
        } else {
            throw new NotFoundException("Can't find repository for type " + type);
        }
        page.forEach(listenerContext::onRead);
        return page;
    }

    @Transactional
    public <T> T create(T object) {
        Class<T> c = (Class<T>) object.getClass();
        CrudService<T, ?> crudService = crudServiceContext.findService(c);
        if (crudService != null) {
            return crudService.create(object);
        }
        DataUtil.verifyOnCreate(object);
        listenerContext.beforeCreate(object);
        T created;
        CrudRepository<T, ?> crudRepository = repositoryContext.findRepository(c);
        if (crudRepository != null) {
            created = crudRepository.save(object);
        } else if (dataRepository != null) {
            created = dataRepository.save(object);
        } else {
            throw new NotFoundException("Can't find repository for type " + c);
        }
        listenerContext.afterCreate(created);
        return created;
    }

    @Transactional
    public <T> T update(T object) {
        Class<T> c = (Class<T>) object.getClass();
        CrudService<T, ?> crudService = crudServiceContext.findService(c);
        if (crudService != null) {
            return crudService.update(object);
        }
        DataUtil.verifyOnUpdate(object);
        listenerContext.beforeUpdate(object);
        T updated;
        CrudRepository<T, ?> crudRepository = repositoryContext.findRepository(c);
        if (crudRepository != null) {
            updated = crudRepository.save(object);
        } else if (dataRepository != null) {
            updated = dataRepository.save(object);
        } else {
            throw new NotFoundException("Can't find repository for type " + c);
        }
        listenerContext.afterUpdate(updated);
        return updated;
    }

    @Transactional
    public <T> T patch(T patch) {
        Class<T> c = (Class<T>) patch.getClass();
        CrudService<T, ?> crudService = crudServiceContext.findService(c);
        if (crudService != null) {
            return crudService.patch(patch);
        }
        Object id = DataUtil.verifyOnPatch(patch);
        T target = getById(c, id);
        listenerContext.beforePatch(patch);
        patchService.patch(patch, target);
        validator.validate(target, Update.class);
        T saved;
        CrudRepository<T, ?> crudRepository = repositoryContext.findRepository(c);
        if (crudRepository != null) {
            saved = crudRepository.save(target);
        } else if (dataRepository != null) {
            saved = dataRepository.save(target);
        } else {
            throw new NotFoundException("Can't find repository for type " + c);
        }
        listenerContext.afterPatch(saved);
        return saved;
    }

    @Transactional
    public <T> void delete(T object) {
        Class<T> c = (Class<T>) object.getClass();
        CrudService<T, ?> crudService = crudServiceContext.findService(c);
        if (crudService != null) {
            crudService.delete(object);
            return;
        }
        DataUtil.verifyOnDelete(object);
        listenerContext.beforeDelete(object);
        CrudRepository<T, ?> crudRepository = repositoryContext.findRepository(c);
        if (crudRepository != null) {
            crudRepository.delete(object);
        } else if (dataRepository != null) {
            dataRepository.delete(object);
        } else {
            throw new NotFoundException("Can't find repository for type " + c);
        }
        listenerContext.afterDelete(object);
    }

    @Transactional
    public <T> T save(T object) {
        return EntityUtils.findId(object) == null
                ? create(object)
                : update(object);
    }
}
