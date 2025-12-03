package software.plusminus.data.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import software.plusminus.data.exception.DataException;
import software.plusminus.data.repository.CrudRepository;
import software.plusminus.data.repository.DataCrudRepository;
import software.plusminus.data.repository.DataRepository;
import software.plusminus.data.repository.SpringCrudRepository;
import software.plusminus.spring.SpringUtil;

import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
@Component
public class DataContext {

    private Map<Class<?>, CrudService<?, ?>> crudServices;
    private Map<Class<?>, CrudRepository<?, ?>> crudRepositories;
    @Nullable
    private DataRepository dataRepository;

    @Autowired
    void init(ObjectProvider<CrudService<?, ?>> crudServices,
              ObjectProvider<CrudRepository<?, ?>> crudRepositories,
              ObjectProvider<PagingAndSortingRepository<?, ?>> pagingAndSortingRepositories,
              @Nullable DataRepository dataRepository) {
        this.crudServices = SpringUtil.beansToMapByGenericType(crudServices.stream()
                .collect(Collectors.toList()), CrudService.class);
        this.crudRepositories = SpringUtil.beansToConcurrentMapByGenericType(crudRepositories.stream()
                .collect(Collectors.toList()), CrudRepository.class);
        this.dataRepository = dataRepository;
        pagingAndSortingRepositories.forEach(repository -> this.crudRepositories.computeIfAbsent(
                SpringUtil.resolveGenericType(repository, PagingAndSortingRepository.class),
                type -> new SpringCrudRepository<>(repository)));
    }

    @Nullable
    public <T, ID> CrudService<T, ID> findService(Class<T> type) {
        return (CrudService<T, ID>) crudServices.get(type);
    }

    @Nullable
    public <T, ID> CrudRepository<T, ID> findRepository(Class<T> type) {
        return (CrudRepository<T, ID>) crudRepositories.get(type);
    }

    public <T, ID> CrudRepository<T, ID> provideRepository(Class<T> type) {
        CrudRepository<T, ID> crudRepository = findRepository(type);
        if (crudRepository != null) {
            return crudRepository;
        }
        if (dataRepository != null) {
            return (CrudRepository<T, ID>) crudRepositories.computeIfAbsent(
                    type, t -> new DataCrudRepository<>(t, dataRepository));
        }
        throw new DataException("Can't provide CrudRepository for type " + type);
    }
}
