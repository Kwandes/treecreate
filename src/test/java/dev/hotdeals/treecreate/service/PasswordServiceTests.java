package dev.hotdeals.treecreate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordServiceTests
{
    @Test
    @DisplayName("Can encode strings")
    void encodePasswordTest()
    {
        String password = "test";
        assertThat(PasswordService.encodePassword(password).isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Verify that encoded string is different from original")
    void verifyEncodedPasswordTest()
    {
        String password = "test";
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

    private static Stream<Arguments> isEncodedArguments()
    {
        return Stream.of(
                Arguments.of("$2a$12$Lq.GcgsLnsK9azvu1tk6ge0Jq4MRv7IZwiuAo4vAb/yIJVKjJ8vTy", true),
                Arguments.of("$2a$12$A/7JhgsAqDycZSWP6n6etO9eDdL4tG09RjOrJDvIa5iIa9uWCt98.", true),
                Arguments.of("invalidPass", false),
                Arguments.of("$2a$12$A/7JhgsAqDycZSWP6n6etO9eDdL4tG09RjOrJDvIa5iIa9uWCt98", false),
                Arguments.of("$2a$12$A/7JhgsAqDycZSWP6n6etO9eDdL4tG09RjOrJDvIa5iIa9uWCt98!", false)
        );
    }

    @ParameterizedTest
    @MethodSource("isEncodedArguments")
    @DisplayName("Can determine if a password is already encoded")
    void isEncoded(String encoding, Boolean isEncoded)
    {
        assertThat(PasswordService.isEncoded(encoding)).isEqualTo(isEncoded);
    }

    private static Stream<Arguments> encryptionArguments()
    {
        return Stream.of(
                Arguments.of("Foo", "7fb0c669e8c1f297c0e31d52be06583ef2da6495332df6980ba5c11a593ec4550e071cfeee92dd78", true),
                Arguments.of("Foo", "08b5218547cac3944bc00ed9ad550f8e741b9ca751b496936fca3ee836410162bc38bb10a929364d", false),
                Arguments.of("", "439c9389cf1ee7083bc957899e946b2088bf0610d32197380d45b7439e20e57d6f3fa874fd85f235", true),
                Arguments.of("", "9015ff27adfc1c2f7e07cb56ee4d5affc4100082330a3394a3608fa3593afe433dee00d1b133d1b1", true),
                Arguments.of("", "2b58b1204784bf6570ac6d95bd50e48e2866cf4531757ed0afb3b356474a0c29afe38c023c39473b", true),
                Arguments.of("", "31b800809eb41db5e06e3ff9aa89bc2bfff0a40c1b5007e80404ff21691f9a2836003b6a12d6f062", false)
        );
    }

    @ParameterizedTest
    @MethodSource("encryptionArguments")
    @DisplayName("Can encrypt data")
    void encryptTest(String data)
    {
        System.out.println(PasswordService.encrypt(data));
        assertThat(PasswordService.encrypt(data)).isNotEmpty();
    }

    @ParameterizedTest
    @MethodSource("encryptionArguments")
    @DisplayName("Can decrypt data")
    void decryptTest(String data, String encrypted, Boolean isMatch)
    {
        assertThat(PasswordService.decrypt(encrypted).equals(data)).isEqualTo(isMatch);
    }

    @Test
    @DisplayName("Can generate a verification token")
    void generateVerificationTokenTest()
    {
        assertThat(PasswordService.generateVerificationToken(10).length()).isEqualTo(10);
    }
}