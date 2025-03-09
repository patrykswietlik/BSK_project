package pl.bsk.project.bsk_project.cipher;

import pl.bsk.project.bsk_project.utils.EnvHandler;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AESCipher {

    private static SecretKey generateKeyFromPin(String pin) throws Exception {
        if (!validatePin(pin)) {
            throw new IllegalArgumentException("Pin is not valid");
        }

        MessageDigest messageDigest = MessageDigest.getInstance(
                EnvHandler.getSystemEnv("KEY_HASH_ALGORITHM")
        );

        byte[] hash = messageDigest.digest(pin.getBytes());

        return new SecretKeySpec(hash, EnvHandler.getSystemEnv("KEY_FORMAT_ALGORITHM"));

    }

    public static byte[] encrypt(String pin, byte[] data) throws Exception {
        SecretKey secretKey = generateKeyFromPin(pin);

        Cipher cipher = Cipher.getInstance(
                EnvHandler.getSystemEnv("KEY_CIPHER_ALGORITHM")
        );
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(data);
    }

    public static byte[] decrypt(String pin, byte[] data) throws Exception {
        SecretKey secretKey = generateKeyFromPin(pin);

        Cipher cipher = Cipher.getInstance(
                EnvHandler.getSystemEnv("KEY_CIPHER_ALGORITHM")
        );
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(data);
    }

    public static String toString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    public static boolean validatePin(String pin) {
        Pattern pattern = Pattern.compile("^\\d{4}$");
        Matcher matcher = pattern.matcher(pin);
        return matcher.matches();
    }
}
