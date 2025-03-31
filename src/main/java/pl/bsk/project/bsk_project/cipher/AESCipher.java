package pl.bsk.project.bsk_project.cipher;

import pl.bsk.project.bsk_project.utils.EnvHandler;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @file AESCipher.java
 * @brief Provides AES encryption and decryption functionality using a PIN-based key.
 *
 * This class handles the generation of secret keys from PINs, encryption and decryption of data,
 * and validation of PIN formats.
 */
public class AESCipher {

    /**
     * @brief Generates a secret key from a given PIN.
     *
     * The PIN must be 4 digits. The key is generated using a hash algorithm specified
     * in the environment variables.
     *
     * @param pin The 4-digit PIN used to generate the key
     * @return SecretKey generated from the PIN
     * @throws Exception If the PIN is invalid or if key generation fails
     * @throws IllegalArgumentException If the PIN is not valid (not 4 digits)
     */
    private static SecretKey generateKeyFromPin(String pin) throws Exception {
        if (!pinIsValid(pin)) {
            throw new IllegalArgumentException("Pin is not valid");
        }

        MessageDigest messageDigest = MessageDigest.getInstance(
                EnvHandler.getSystemEnv("KEY_HASH_ALGORITHM")
        );

        byte[] hash = messageDigest.digest(pin.getBytes());

        return new SecretKeySpec(hash, EnvHandler.getSystemEnv("KEY_FORMAT_ALGORITHM"));
    }

    /**
     * @brief Encrypts data using AES with a key derived from the given PIN.
     *
     * @param pin The 4-digit PIN used for key generation
     * @param data The data to be encrypted
     * @return byte[] Encrypted data
     * @throws Exception If encryption fails (invalid PIN, algorithm not available, etc.)
     */
    public static byte[] encrypt(String pin, byte[] data) throws Exception {
        SecretKey secretKey = generateKeyFromPin(pin);

        Cipher cipher = Cipher.getInstance(
                EnvHandler.getSystemEnv("KEY_CIPHER_ALGORITHM")
        );
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(data);
    }

    /**
     * @brief Decrypts data using AES with a key derived from the given PIN.
     *
     * @param pin The 4-digit PIN used for key generation
     * @param data The encrypted data to be decrypted
     * @return byte[] Decrypted data
     * @throws Exception If decryption fails (invalid PIN, wrong key, corrupted data, etc.)
     */
    public static byte[] decrypt(String pin, byte[] data) throws Exception {
        SecretKey secretKey = generateKeyFromPin(pin);

        Cipher cipher = Cipher.getInstance(
                EnvHandler.getSystemEnv("KEY_CIPHER_ALGORITHM")
        );
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(data);
    }

    /**
     * @brief Converts byte array to UTF-8 string.
     *
     * @param data The byte array to convert
     * @return String representation of the byte array in UTF-8 encoding
     */
    public static String toString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * @brief Validates that a PIN is exactly 4 digits.
     *
     * @param pin The PIN to validate
     * @return boolean True if the PIN is valid (exactly 4 digits), false otherwise
     */
    public static boolean pinIsValid(String pin) {
        Pattern pattern = Pattern.compile("^\\d{4}$");
        Matcher matcher = pattern.matcher(pin);
        return matcher.matches();
    }
}