package validators;

import org.example.service.exceptions.ValidationException;
import org.example.validators.UrlValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UrlValidatorTest {

    UrlValidator underTest = new UrlValidator();

    @ParameterizedTest
    @ValueSource(strings = {"https://www.monzo.com", "https://www.monzo.com/home", "https://www.monzo.com/some/other/page-123"})
    void testGetValidUrl(String url) {
        String[] input = new String[1];
        input[0] = url;
        var expected = "https://www.monzo.com";
        assertThat(underTest.getValidBaseUrl(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://w.monzo.com", "http:/om/home", "123"})
    void testGetValidUrlThrowsExceptionWithInvalidUrlFormat(String url) {
        String[] input = new String[1];
        input[0] = url;
        assertThrows(ValidationException.class, () -> underTest.getValidBaseUrl(input));
    }
}
