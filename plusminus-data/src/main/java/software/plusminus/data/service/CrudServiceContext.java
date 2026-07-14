package software.plusminus.data.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.plusminus.spring.SpringUtil;

import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
@Component
public class CrudServiceContext {

    private Map<Class<?>, CrudService<?, ?>> crudServices;

    @Autowired
    void init(ObjectProvider<CrudService<?, ?>> crudServices) {
        this.crudServices = SpringUtil.beansToMapByGenericType(crudServices.stream()
                .collect(Collectors.toList()), CrudService.class);
    }

    @Nullable
    public <T, ID> CrudService<T, ID> findService(Class<T> type) {
        return (CrudService<T, ID>) crudServices.get(type);
    }
}
