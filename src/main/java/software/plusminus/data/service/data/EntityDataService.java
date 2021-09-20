package software.plusminus.data.service.data;

import company.plusminus.patch.service.PatchService;
import company.plusminus.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import software.plusminus.data.exception.DataException;
import software.plusminus.data.repository.DataRepository;

@Service
@SuppressWarnings("squid:S00119")
public class EntityDataService implements DataService {

    @Autowired
    private PatchService patchService;
    @Autowired
    private DataRepository repository;

    @Override
    public <T, ID> T read(Class<T> type, ID id) {
        return repository.findById(type, id);
    }

    @Override
    public <T> Page<T> read(Class<T> type, Pageable pageable) {
        return repository.findAll(type, pageable);
    }

    @Override
    public <T> T create(T entity) {
        if (EntityUtils.findId(entity) != null) {
            throw new DataException();
        }
        return repository.save(entity);
    }

    @Override
    public <T> T update(T entity) {
        if (EntityUtils.findId(entity) == null) {
            throw new DataException();
        }
        return repository.save(entity);
    }

    @Override
    public <T> T patch(T patch) {
        Object id = EntityUtils.findId(patch);
        Class<T> type = (Class<T>) patch.getClass();
        T target = read(type, id);
        patchService.patch(patch, target);
        return repository.save(target);
    }

    @Override
    public <T> void delete(T entity) {
        repository.delete(entity);
    }
}
