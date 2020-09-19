package dev.hotdeals.treecreate;

import dev.hotdeals.treecreate.service.PasswordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class HTMLTests {

    private static Stream<Arguments> emailTestArguments()
    {
        return Stream.of(
                Arguments.of("this-is-an@example.com", true),
                Arguments.of("thisisalso12@anexample.", false)
        );
    }

    @ParameterizedTest
    @DisplayName("Email Regex Test")
    @MethodSource("emailTestArguments")
    void validateEmailRegexTest(String email, boolean expectedResult)
    {
        String pattern = "^\\b[\\w.!#$%&’*+\\/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)+\\b$";

        assertThat(email.matches(pattern)).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> usernameTestArguments()
    {
        return Stream.of(
                Arguments.of("thisisaname", true),
                Arguments.of("FirstName-LastName", true),
                Arguments.of("ExampleName", true),
                Arguments.of("-NameExample", false),
                Arguments.of("Example-Name", true),
                Arguments.of("Exa mple-Name", false)
        );
    }

    @ParameterizedTest
    @DisplayName("Username Regex Test")
    @MethodSource("usernameTestArguments")
    void validateUsernameRegexTest(String username, boolean expectedResult)
    {
        String pattern = "^[A-Za-z][A-Za-z0-9_-]{2,29}$";

        assertThat(username.matches(pattern)).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> passwordTestArguments()
    {
        return Stream.of(
                Arguments.of("thisIsAsecure!!201pass", true),
                Arguments.of("zb9z^m79tTZFGV", true),
                Arguments.of("IsthisSecure420?!", true),
                Arguments.of("unreliablePassowrd031", false)
        );
    }

    @ParameterizedTest
    @DisplayName("Password Regex Test")
    @MethodSource("passwordTestArguments")
    void validatePasswordRegexTest(String password, boolean expectedResult)
    {
        String pattern = "^(?!.* )(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[|!$%&\\/\\(\\)\\?\\^\\'\\\\\\+\\-\\*]).{8,}$";

        assertThat(password.matches(pattern)).isEqualTo(expectedResult);
    }
}
