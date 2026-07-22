package software.plusminus.data.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import software.plusminus.data.model.Update;
import software.plusminus.data.repository.CrudRepository;
import software.plusminus.data.repository.RepositoryContext;
import software.plusminus.data.util.DataUtil;
import software.plusminus.patch.service.PatchService;

import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
public abstract class AbstractCrudService<T, ID> implements CrudService<T, ID> {

    private DataValidator dataValidator;
    private PatchService patchService;
    private CrudRepository<T, ID> repository;
    @Nullable
    private AbstractCrudService<T, ID> self;

    protected AbstractCrudService() {
    }

    protected AbstractCrudService(DataValidator dataValidator,
                                  PatchService patchService,
                                  CrudRepository<T, ID> repository) {
        this.dataValidator = dataValidator;
        this.patchService = patchService;
        this.repository = repository;
    }

    @Autowired
    void init(DataValidator dataValidator,
              PatchService patchService,
              @Nullable CrudRepository<T, ID> repository,
              RepositoryContext repositoryContext,
              ObjectProvider<AbstractCrudService<T, ID>> self) {
        if (this.dataValidator == null) {
            this.dataValidator = dataValidator;
        }
        if (this.patchService == null) {
            this.patchService = patchService;
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
        return repository.getById(id);
    }

    @Override
    public Page<T> getPage(Pageable pageable) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.getPage(pageable);
        }
        return repository.findAll(pageable);
    }

    @Override
    public T create(T object) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.create(object);
        }
        DataUtil.verifyOnCreate(object);
        return repository.save(object);
    }

    @Override
    public T update(T object) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.update(object);
        }
        DataUtil.verifyOnUpdate(object);
        return repository.save(object);
    }

    @Override
    public T patch(T patch) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.patch(patch);
        }
        ID id = DataUtil.verifyOnPatch(patch);
        T target = getById(id);
        patchService.patch(patch, target);
        dataValidator.validate(target, Update.class);
        return repository.save(target);
    }

    @Override
    public void delete(T object) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            self.delete(object);
            return;
        }
        DataUtil.verifyOnDelete(object);
        repository.delete(object);
    }

    @Override
    public T save(T object) {
        if (self != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            return self.save(object);
        }
        return CrudService.super.save(object);
    }
}
