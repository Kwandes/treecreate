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
                Arguments.of("thisisalso12@anexample.", false),
                Arguments.of("example@smth.com", true),
                Arguments.of("@smth.com", false),
                Arguments.of("a@ggl.com", true),
                Arguments.of("1@ggl.com", true),
                Arguments.of("examplesmth.com", false),
                Arguments.of("example@gmail", false),
                Arguments.of("example@gmail.", false),
                Arguments.of("example@gmail.c", true),
                Arguments.of("example@gmail.!", false),
                Arguments.of("example.com@gmail", false),
                Arguments.of("example.smth@somewhere.what.com", true)
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
                // 1 Type of character - All should be false
                Arguments.of("$$$$$$$$$$", false),
                Arguments.of("123456", false),
                Arguments.of("ADADADADA", false),
                Arguments.of("crisfiat", false),
                Arguments.of("thisisnotworking", false),

                // 2 Types of characters - All should be false
                // Format : [x + digits]
                Arguments.of("DADADA23", false),
                Arguments.of("cris8989", false),
                Arguments.of("!?!?!?!23", false),

                // Format : [x + symbols]
                Arguments.of("dadada!!", false),
                Arguments.of("DADADA!!", false),
                Arguments.of("2313!!!", false),

                // Format : [x + lowercase]
                Arguments.of("123456asda", false),
                Arguments.of("DADAdada", false),
                Arguments.of("!?!?!?asdad", false),

                // Format : [x + uppercase]
                Arguments.of("1234DADA", false),
                Arguments.of("asdasdDADA", false),
                Arguments.of("!?!-ADAA", false),

                // 3 Types of characters - All should be false
                // Format : [x + y + digits]
                Arguments.of("asdDAS123", false),
                Arguments.of("asd!?!123", false),
                Arguments.of("DAS!?!123", false),

                // Format : [x + y + symbols]
                Arguments.of("asdDAS!?!", false),
                Arguments.of("asd123!?!", false),
                Arguments.of("GAH832!!!", false),

                // Format : [x + y + lowercase]
                Arguments.of("ASD123asd", false),
                Arguments.of("ADS!?!asd", false),
                Arguments.of("123!?das", false),

                // Format : [x + y + uppercase]
                Arguments.of("ads123ASD", false),
                Arguments.of("asd!?ASD", false),
                Arguments.of("!?!?123ASD", false),

                // 4 Types of characters [All of them] - First set should be true
                // Length >= 6
                Arguments.of("Cris!2", true),
                Arguments.of("Mr@Proper89", true),
                Arguments.of("ManHandler84$", true),
                Arguments.of("$$$izLife4me", true),

                // Length < 6
                Arguments.of("cR1!", false),
                Arguments.of("Ro!4", false),
                Arguments.of("?4Aef", false),

                // With spaces
                Arguments.of("Cri s!2", false),
                Arguments.of("Mr!Prope r89", false),
                Arguments.of("Man Handler84$", false),
                Arguments.of("$$$ izLife4me", false),
                Arguments.of("Cr !1i", false)

        );
    }

    @ParameterizedTest
    @DisplayName("Password Regex Test")
    @MethodSource("passwordTestArguments")
    void validatePasswordRegexTest(String password, boolean expectedResult)
    {
        String pattern = "^(?!.* )(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!$%&\\?\\^\\+\\-\\*#@]).{6,}$";

        assertThat(password.matches(pattern)).isEqualTo(expectedResult);
    }
}
