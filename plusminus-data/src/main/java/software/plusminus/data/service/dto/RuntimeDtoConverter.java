package software.plusminus.data.service.dto;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import software.plusminus.data.exception.DataException;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("java:S119")
@AllArgsConstructor
public class RuntimeDtoConverter<DTO, O> implements DtoConverter<DTO, O> {

    private Class<DTO> dtoType;
    private Class<O> objectType;

    @Override
    public DTO toDto(O object) {
        DTO dto = createDto();
        BeanUtils.copyProperties(object, dto);
        return dto;
    }

    @Override
    public O toEntity(DTO dto) {
        O entity = createObject();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    private DTO createDto() {
        try {
            return dtoType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                 | NoSuchMethodException | InvocationTargetException e) {
            throw new DataException(e);
        }
    }

    private O createObject() {
        try {
            return objectType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                 | NoSuchMethodException | InvocationTargetException e) {
            throw new DataException(e);
        }
    }
}
