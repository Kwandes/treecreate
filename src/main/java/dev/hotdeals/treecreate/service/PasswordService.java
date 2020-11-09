package dev.hotdeals.treecreate.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.util.Random;
import java.util.regex.Pattern;

public class PasswordService
{
    private static final int ENCODING_STRENGTH = 10; // work factor of bcrypt
    private static final String ENCRYPTION_KEY = "very secure key"; //TODO - extract into a ENV variable once encryption is actually used

    /**
     * Produces a hashed and salted version of a string using Springboots' Bcrypt encoder.
     * In order to see if a given password matches the hashed one, a encoder.matches(string1, string2) method has to be used
     *
     * @param password String to be hashed
     * @return Returns a hashed string
     */
    public static String encodePassword(String password)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(ENCODING_STRENGTH);
        return encoder.encode(password);
    }

    /**
     * Runs the password against a Bcrypt-regex for validating whether or not a given string has been encoded
     *
     * @param password String to be validated
     * @return whether or not a string has been encoded with BCrypt
     */
    public static boolean isEncoded(String password)
    {
        Pattern bcryptPattern = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

        return bcryptPattern.matcher(password).matches();
    }

    /**
     * Compares 2 hashed passwords
     *
     * @param password  First String for the comparison
     * @param password2 Second password for the comparison
     * @return Returns whether or not the strings match
     */
    public static boolean matches(String password, String password2)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(ENCODING_STRENGTH);
        return encoder.matches(password, password2);
    }

    public static String encrypt(String text)
    {
        String salt = KeyGenerators.string().generateKey();
        TextEncryptor encryptor = Encryptors.text(ENCRYPTION_KEY, salt);
        return salt + encryptor.encrypt(text);
    }

    public static String decrypt(String text)
    {
        int saltLength = 16;
        String actualData = text.substring(saltLength);
        String salt = text.substring(0, saltLength);
        TextEncryptor encryptor = Encryptors.text(ENCRYPTION_KEY, salt);
        return encryptor.decrypt(actualData);
    }

    public static String generateVerificationToken(int length)
    {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String generateVerificationToken()
    {
        return generateVerificationToken(7);
    }
}