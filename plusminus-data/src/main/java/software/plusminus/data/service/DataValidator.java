package software.plusminus.data.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.data.exception.ClientDataException;

import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

@RequiredArgsConstructor
@Component
public class DataValidator {

    private final Validator validator;

    public <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new ClientDataException("Validation failed: " + message);
        }
    }
}
