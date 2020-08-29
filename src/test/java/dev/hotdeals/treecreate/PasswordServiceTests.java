package dev.hotdeals.treecreate;

import dev.hotdeals.treecreate.service.PasswordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class PasswordServiceTests
{
    @Test
    @DisplayName("Can encode strings")
    void encodePasswordTest()
    {
        String password = "test";
        assertThat(PasswordService.encodePassword(password).isEmpty()).isFalse();
        assertThat(PasswordService.encodePassword(password)).isNotEqualTo(password);
    }

    // the hash is also salted, therefore multiple tests per value are needed to make sure it works properly
    // the encoded versions work with any work factor
    private static Stream<Arguments> passwordPairArguments()
    {
        return Stream.of(
                Arguments.of("Foo", "$2a$12$Lq.GcgsLnsK9azvu1tk6ge0Jq4MRv7IZwiuAo4vAb/yIJVKjJ8vTy", true),
                Arguments.of("Foo", "$2a$12$A/7JhgsAqDycZSWP6n6etO9eDdL4tG09RjOrJDvIa5iIa9uWCt98.", false),
                Arguments.of("", "$2a$12$GDtrUigbGA1LCiSVeMOJgOHj9XFaVBHSpb.lARYHh6z6m1P0Q5H6e", true),
                Arguments.of("", "$2a$12$qt339oimbciIDH25RN173OqeLddJxlXWn8zFtA87pJ2vydYTkVWbS", true),
                Arguments.of("", "$2a$13$zUpOJfCe22x8ng.F0Us9MenCetp3y5wg1We8k8N4cMGbfuPlOV3TS", true),
                Arguments.of("", "$2a$12$AEMR9da4KeUbyNUN6.ZgDuMTH5lcqTokQ.NL4XwfWeFkR/07RHQvm", false)
        );
    }

    @ParameterizedTest
    @MethodSource("passwordPairArguments")
    @DisplayName("Can check password match")
    void matchesTest(String password1, String password2, boolean expectedMatch)
    {
        assertThat(PasswordService.matches(password1, password2)).isEqualTo(expectedMatch);
    }
}
