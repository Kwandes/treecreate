package dev.hotdeals.treecreate.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordService
{
    private static final int encodingStrength = 12; // work factor of bcrypt

    /**
     * Produces a hashed and salted version of a string using Springboots' Bcrypt encoder.
     * In order to see if a given password matches the hashed one, a encoder.matches(string1, string2) method has to be used
     *
     * @param password String to be hashed
     * @return Returns a hashed string
     */
    public static String encodePassword(String password)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(encodingStrength);
        return encoder.encode(password);
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
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(encodingStrength);
        return encoder.matches(password, password2);
    }
}