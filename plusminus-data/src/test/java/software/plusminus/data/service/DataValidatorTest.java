package software.plusminus.data.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.data.exception.ClientDataException;
import software.plusminus.data.fixtures.TestEntity;
import software.plusminus.data.model.Update;

import java.util.Collections;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataValidatorTest {

    @Mock
    private Validator validator;
    @Mock
    private ConstraintViolation<TestEntity> violation;
    @Mock
    private Path propertyPath;

    private DataValidator dataValidator;

    @Before
    public void beforeEach() {
        dataValidator = new DataValidator(validator);
    }

    @Test
    public void validate_NoViolations() {
        TestEntity entity = new TestEntity();
        when(validator.validate(entity, Update.class)).thenReturn(Collections.emptySet());

        dataValidator.validate(entity, Update.class);
    }

    @Test
    public void validate_WithViolations() {
        TestEntity entity = new TestEntity();
        when(validator.validate(entity, Update.class)).thenReturn(Collections.singleton(violation));
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(propertyPath.toString()).thenReturn("myField");
        when(violation.getMessage()).thenReturn("must not be empty");

        assertThatThrownBy(() -> dataValidator.validate(entity, Update.class))
                .isInstanceOf(ClientDataException.class)
                .hasMessage("Validation failed: myField must not be empty");
    }
}
