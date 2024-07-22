package org.example.validators;

import org.example.service.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UrlValidator {
    private static final String URL_MATCH_REGEX = "(((https)://)(?:www.|[-;:&=\\+\\$,\\w]+@)([-%()_.!~*';/?:@&=+$,A-Za-z0-9])+)";
    private static final Pattern BASE_DOMAIN_EXTRACT_PATTERN = Pattern.compile("^.+?[^\\/:](?=[?\\/]|$)");


    public String getValidBaseUrl(String[] args) {
        var input = args[0];
        List<UrlValidationFailures> failures = this.validate(input);

        if (!failures.isEmpty()) {
            throw new ValidationException(failures);
        }

        Matcher matcher = BASE_DOMAIN_EXTRACT_PATTERN.matcher(input);
        matcher.find();
        return matcher.group(0) + "/";
    }

    private List<UrlValidationFailures> validate(String url) {
        List<UrlValidationFailures> failures = new LinkedList<>();

        if(!url.matches(URL_MATCH_REGEX)) {
            failures.add(UrlValidationFailures.invalidUrlInput(url));
        }

        return failures;
    }
}
