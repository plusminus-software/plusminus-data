package software.plusminus.data.service.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.check.util.JsonUtil;
import software.plusminus.data.fixtures.TestDto;
import software.plusminus.data.fixtures.TestEntity;

import static software.plusminus.check.Checks.check;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeDtoConverterTest {

    private RuntimeDtoConverter<TestDto, TestEntity> converter = new RuntimeDtoConverter<>(
            TestDto.class, TestEntity.class);

    @Test
    public void toDto() {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);
        TestDto dto = JsonUtil.fromJson("/json/test-same-dto.json", TestDto.class);

        TestDto result = converter.toDto(entity);

        check(result).is(dto);
    }

    @Test
    public void toEntity() {
        TestEntity entity = JsonUtil.fromJson("/json/test-entity.json", TestEntity.class);
        TestDto dto = JsonUtil.fromJson("/json/test-same-dto.json", TestDto.class);

        TestEntity result = converter.toEntity(dto);

        check(result).is(entity);
    }
}