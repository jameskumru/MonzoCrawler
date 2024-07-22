package org.example.service.exceptions;

import lombok.Getter;
import org.example.validators.ValidationFailure;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ValidationException extends RuntimeException {

    public <T extends ValidationFailure> ValidationException(List<T> failures) {
        super(failures.stream().map(ValidationFailure::getErrorMessage).collect(Collectors.joining(",", "[", "]")));
    }
}
