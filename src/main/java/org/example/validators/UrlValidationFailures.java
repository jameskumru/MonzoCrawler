package org.example.validators;

import lombok.Getter;

@Getter
public class UrlValidationFailures extends ValidationFailure {

    public UrlValidationFailures(String errorMessage, String cause) {
        super(errorMessage, cause);
    }

    public static UrlValidationFailures invalidUrlInput(String cause) {
        return new UrlValidationFailures("Invalid URL Provided. Does not match an accepted URL Pattern. \n Cause: ", cause);
    }
}
