package software.plusminus.data.service.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.check.util.JsonUtils;
import software.plusminus.data.fixtures.TestDto;
import software.plusminus.data.fixtures.TestEntity;

import static software.plusminus.check.Checks.check;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeDtoConverterTest {

    private RuntimeDtoConverter<TestDto, TestEntity> converter = new RuntimeDtoConverter<>(
            TestDto.class, TestEntity.class);

    @Test
    public void toDto() {
        TestEntity entity = JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
        TestDto dto = JsonUtils.fromJson("/json/test-same-dto.json", TestDto.class);

        TestDto result = converter.toDto(entity);

        check(result).is(dto);
    }

    @Test
    public void toEntity() {
        TestEntity entity = JsonUtils.fromJson("/json/test-entity.json", TestEntity.class);
        TestDto dto = JsonUtils.fromJson("/json/test-same-dto.json", TestDto.class);

        TestEntity result = converter.toEntity(dto);

        check(result).is(entity);
    }
}