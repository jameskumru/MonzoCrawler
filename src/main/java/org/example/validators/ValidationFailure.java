package org.example.validators;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ValidationFailure {
    private final String errorMessage;
    private final String cause;

    public ValidationFailure(@NonNull String errorMessage, @NonNull String cause) {
        this.errorMessage = errorMessage;
        this.cause = cause;
    }
}
