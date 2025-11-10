package software.plusminus.data.service.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import software.plusminus.check.util.JsonUtils;
import software.plusminus.data.service.CrudService;
import software.plusminus.fixtures.TestDto;
import software.plusminus.fixtures.TestEntity;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

@RunWith(MockitoJUnitRunner.class)
public class DtoCrudServiceTest {

    private static final String ENTITY = "entity";
    private static final String SAVED = "saved";
    private static final String CONVERTED = "converted";

    @Mock
    private DtoConverter<TestDto, TestEntity> converter;
    @Mock
    private CrudService<TestEntity, Long> crudService;
    @InjectMocks
    private DtoCrudService<TestDto, TestEntity, Long> dtoCrudService =
            mock(DtoCrudService.class, Answers.CALLS_REAL_METHODS);
    @Captor
    private ArgumentCaptor<TestEntity> captor;

    @Test
    public void readById() {
        TestEntity entity = readEntity();
        TestDto dto = readDto();
        when(crudService.getById(2L)).thenReturn(entity);
        when(converter.toDto(entity)).thenReturn(dto);

        TestDto result = dtoCrudService.getById(2L);

        assertThat(result).isSameAs(dto);
    }

    @Test
    public void readPage() {
        TestEntity entity = readEntity();
        TestDto dto = readDto();
        Pageable pageable = PageRequest.of(0, 1);
        Page<TestEntity> entityPage = new PageImpl<>(Collections.singletonList(entity), pageable, 1);
        Page<TestDto> dtoPage = new PageImpl<>(Collections.singletonList(dto), pageable, 1);
        when(crudService.getPage(pageable)).thenReturn(entityPage);
        when(converter.toDto(entity)).thenReturn(dto);

        Page<TestDto> result = dtoCrudService.getPage(pageable);

        check(result).is(dtoPage);
    }

    @Test
    public void create() {
        TestEntity entity = readEntity(ENTITY);
        entity.setId(null);
        TestEntity saved = readEntity(SAVED);
        TestDto dto = readDto("dto");
        TestDto converted = readDto(CONVERTED);
        when(converter.toEntity(dto)).thenReturn(entity);
        when(crudService.create(entity)).thenReturn(saved);
        when(converter.toDto(saved)).thenReturn(converted);

        TestDto result = dtoCrudService.create(dto);

        assertThat(result).isSameAs(converted);
    }

    @Test
    public void update() {
        TestEntity entity = readEntity(ENTITY);
        TestEntity saved = readEntity(SAVED);
        TestDto dto = readDto("dto");
        TestDto converted = readDto(CONVERTED);
        when(converter.toEntity(dto)).thenReturn(entity);
        when(crudService.update(entity)).thenReturn(saved);
        when(converter.toDto(saved)).thenReturn(converted);

        TestDto result = dtoCrudService.update(dto);

        assertThat(result).isSameAs(converted);
    }

    @Test
    public void patch() {
        TestDto patch = readDto("patch");
        TestEntity convertedPatch = readEntity("convertedPatch");
        TestEntity result = readEntity("result");
        TestDto convertedResult = readDto("convertedResult");

        when(converter.toEntity(patch)).thenReturn(convertedPatch);
        when(crudService.patch(convertedPatch)).thenReturn(result);
        when(converter.toDto(result)).thenReturn(convertedResult);

        TestDto actual = dtoCrudService.patch(patch);

        verify(crudService).patch(convertedPatch);
        assertThat(actual).isSameAs(convertedResult);
    }

    @Test
    public void delete() {
        TestEntity entity = readEntity();
        TestDto dto = readDto();
        when(converter.toEntity(dto)).thenReturn(entity);

        dtoCrudService.delete(dto);

        verify(crudService).delete(captor.capture());
        assertThat(captor.getValue()).isSameAs(entity);
    }

    private TestEntity readEntity() {
        return JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
    }

    private TestEntity readEntity(String myField) {
        TestEntity entity = readEntity();
        entity.setMyField(myField);
        return entity;
    }

    private TestDto readDto() {
        return JsonUtils.fromJson("/json/test-dto.json", TestDto.class);
    }

    private TestDto readDto(String myField) {
        TestDto dto = readDto();
        dto.setMyField(myField);
        return dto;
    }
}