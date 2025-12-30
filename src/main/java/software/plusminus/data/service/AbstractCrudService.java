package software.plusminus.data.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import software.plusminus.crud.listener.CrudListenerContext;
import software.plusminus.data.exception.NotFoundException;
import software.plusminus.data.model.Update;
import software.plusminus.data.repository.CrudRepository;
import software.plusminus.data.repository.RepositoryContext;
import software.plusminus.data.util.DataUtil;
import software.plusminus.patch.service.PatchService;

import javax.annotation.Nullable;
import javax.validation.Validator;

@SuppressWarnings("java:S119")
public abstract class AbstractCrudService<T, ID> implements CrudService<T, ID> {

    private Validator validator;
    private PatchService patchService;
    private CrudListenerContext listenerContext;
    private CrudRepository<T, ID> repository;
    @Nullable
    private AbstractCrudService<T, ID> self;

    protected AbstractCrudService() {
    }

    protected AbstractCrudService(Validator validator,
                                  PatchService patchService,
                                  CrudListenerContext listenerContext,
                                  CrudRepository<T, ID> repository) {
        this.validator = validator;
        this.patchService = patchService;
        this.listenerContext = listenerContext;
        this.repository = repository;
    }

    @Autowired
    void init(Validator validator,
              PatchService patchService,
              CrudListenerContext listenerContext,
              @Nullable CrudRepository<T, ID> repository,
              RepositoryContext repositoryContext,
              ObjectProvider<AbstractCrudService<T, ID>> self) {
        if (this.validator == null) {
            this.validator = validator;
        }
        if (this.patchService == null) {
            this.patchService = patchService;
        }
        if (this.listenerContext == null) {
            this.listenerContext = listenerContext;
        }
        if (this.repository == null) {
            this.repository = DataUtil.provideCrudRepository(repository, repositoryContext, this, CrudService.class);
        }
        this.self = self.getIfUnique();
    }

    @Override
    public T getById(ID id) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.getById(id);
        }
        T object = repository.getById(id);
        if (object == null) {
            throw new NotFoundException("Can't find object with id " + id);
        }
        listenerContext.onSingleRead(object);
        return object;
    }

    @Override
    public Page<T> getPage(Pageable pageable) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.getPage(pageable);
        }
        Page<T> page = repository.findAll(pageable);
        page.forEach(listenerContext::onRead);
        return page;
    }

    @Override
    public T create(T object) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.create(object);
        }
        DataUtil.verifyOnCreate(object);
        listenerContext.beforeCreate(object);
        T created = repository.save(object);
        listenerContext.afterCreate(created);
        return created;
    }

    @Override
    public T update(T object) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.update(object);
        }
        DataUtil.verifyOnUpdate(object);
        listenerContext.beforeUpdate(object);
        T updated = repository.save(object);
        listenerContext.afterUpdate(updated);
        return updated;
    }

    @Override
    public T patch(T patch) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.patch(patch);
        }
        ID id = DataUtil.verifyOnPatch(patch);
        T target = getById(id);
        listenerContext.beforePatch(patch);
        patchService.patch(patch, target);
        validator.validate(target, Update.class);
        T saved = repository.save(target);
        listenerContext.afterPatch(saved);
        return saved;
    }

    @Override
    public void delete(T object) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            self.delete(object);
            return;
        }
        DataUtil.verifyOnDelete(object);
        listenerContext.beforeDelete(object);
        repository.delete(object);
        listenerContext.afterDelete(object);
    }

    @Override
    public T save(T object) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.save(object);
        }
        return CrudService.super.save(object);
    }

    protected CrudListenerContext listenerContext() {
        return listenerContext;
    }
}
