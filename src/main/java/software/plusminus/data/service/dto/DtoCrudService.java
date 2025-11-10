package software.plusminus.data.service.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import software.plusminus.data.service.CrudService;
import software.plusminus.data.service.DataCrudService;
import software.plusminus.data.service.DataService;
import software.plusminus.data.util.DataUtil;

import javax.annotation.Nullable;

@SuppressWarnings("java:S119")
@NoArgsConstructor
@AllArgsConstructor
public abstract class DtoCrudService<DTO, E, ID> implements CrudService<DTO, ID> {

    private DtoConverter<DTO, E> converter;
    private CrudService<E, ID> crudService;

    @Autowired
    void init(@Nullable DtoConverter<DTO, E> converter,
              @Nullable CrudService<E, ID> crudService,
              DataService dataService) {
        if (this.converter == null) {
            this.converter = DataUtil.provideDtoConverter(converter, this, DtoCrudService.class);
        }
        if (this.crudService == null) {
            if (crudService != null && crudService != this) {
                this.crudService = crudService;
            } else {
                ResolvableType resolvableType = ResolvableType.forClass(this.getClass())
                        .as(DtoCrudService.class);
                Class<E> entityType = (Class<E>) resolvableType.getGeneric(1).resolve();
                this.crudService =  new DataCrudService<>(entityType, dataService);
            }
        }
    }


    @Override
    public DTO getById(ID id) {
        E entity = crudService.getById(id);
        return converter.toDto(entity);
    }

    @Override
    public Page<DTO> getPage(Pageable pageable) {
        return crudService.getPage(pageable)
                .map(element -> converter.toDto(element));
    }

    @Override
    public DTO create(DTO dto) {
        E converted = converter.toEntity(dto);
        E result = crudService.create(converted);
        return converter.toDto(result);
    }

    @Override
    public DTO update(DTO dto) {
        E converted = converter.toEntity(dto);
        E result = crudService.update(converted);
        return converter.toDto(result);
    }

    @Override
    public DTO patch(DTO patch) {
        E converted = converter.toEntity(patch);
        E result = crudService.patch(converted);
        return converter.toDto(result);
    }

    @Override
    public void delete(DTO dto) {
        E converted = converter.toEntity(dto);
        crudService.delete(converted);
    }
}
