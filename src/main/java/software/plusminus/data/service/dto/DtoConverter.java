package software.plusminus.data.service.dto;

@SuppressWarnings("java:S119")
public interface DtoConverter<DTO, O> {

    DTO toDto(O object);

    O toEntity(DTO dto);

}
