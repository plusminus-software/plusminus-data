package software.plusminus.data.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.ResolvableType;
import software.plusminus.crud.repository.CrudRepository;
import software.plusminus.crud.service.CrudService;
import software.plusminus.crud.service.DataCrudService;
import software.plusminus.crud.service.dto.DtoConverter;
import software.plusminus.crud.service.dto.RuntimeDtoConverter;
import software.plusminus.data.exception.DataException;
import software.plusminus.data.service.DataContext;
import software.plusminus.data.service.DataService;
import software.plusminus.spring.SpringUtil;
import software.plusminus.util.EntityUtils;

import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
@UtilityClass
public class DataUtil {

    public void verifyOnCreate(Object object) {
        Object id = EntityUtils.findId(object);
        if (id != null) {
            throw new DataException("Cannot create an object because id is " + id + " (expected null)");
        }
    }

    public void verifyOnUpdate(Object object) {
        Object id = EntityUtils.findId(object);
        if (id == null) {
            throw new DataException("Cannot update an object: id is null");
        }
    }

    public <ID> ID verifyOnPatch(Object patch) {
        Object id = EntityUtils.findId(patch);
        if (id == null) {
            throw new DataException("Cannot patch an object: id is null");
        }
        return (ID) id;
    }

    public void verifyOnDelete(Object object) {
        Object id = EntityUtils.findId(object);
        if (id == null) {
            throw new DataException("Cannot delete an object: id is null");
        }
    }

    public <T, ID, B> CrudService<T, ID> provideCrudService(@Nullable CrudService<T, ID> service,
                                                            DataService dataService,
                                                            B bean,
                                                            Class<? super B> beanType) {
        if (service != null) {
            return service;
        } else {
            Class<T> genericType = SpringUtil.resolveGenericType(bean, beanType);
            return new DataCrudService<>(genericType, dataService);
        }
    }

    public <T, ID, B> CrudRepository<T, ID> provideCrudRepository(@Nullable CrudRepository<T, ID> repository,
                                                                  DataContext dataContext,
                                                                  B bean,
                                                                  Class<? super B> beanType) {
        if (repository != null) {
            return repository;
        }
        Class<T> genericType = SpringUtil.resolveGenericType(bean, beanType);
        return dataContext.provideRepository(genericType);
    }

    public <DTO, E, B> DtoConverter<DTO, E> provideDtoConverter(@Nullable DtoConverter<DTO, E> converter,
                                                                B bean,
                                                                Class<? super B> beanType) {
        if (converter != null) {
            return converter;
        }
        ResolvableType resolvableType = ResolvableType.forClass(bean.getClass())
                .as(beanType);
        Class<DTO> dtoType = (Class<DTO>) resolvableType.getGeneric(0).resolve();
        if (dtoType == null) {
            throw new IllegalStateException("Can't resolve dtoType in DtoCrudService class");
        }
        Class<E> entityType = (Class<E>) resolvableType.getGeneric(1).resolve();
        if (entityType == null) {
            throw new IllegalStateException("Can't resolve entityType in DtoCrudService class");
        }
        return new RuntimeDtoConverter<>(dtoType, entityType);
    }
}
