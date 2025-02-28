package pl.bsk.project.bsk_project;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

public class KeysGenerator {

    public static byte[] getHash(String pin) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        return messageDigest.digest(pin.getBytes());
    }

    public static SecretKey createKeyFromHash(byte[] hash) {
        return new SecretKeySpec(hash, "AES");
    }

    public static byte[] AESCipher(SecretKey key, byte[] data, int opmode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(opmode, key);
        return cipher.doFinal(data);
    }

    /*
        Ta funkcja zwraca najpierw zakodowany klucz prywatny, a potem klucz publiczny
    */
    public static List<String> getRSAKeys(String pin) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        SecretKey secretKey = createKeyFromHash(getHash(pin));

        byte[] encryptedPrivateKey = AESCipher(secretKey, privateKey.getEncoded(), Cipher.ENCRYPT_MODE);

        return List.of(
                Base64.getEncoder().encodeToString(encryptedPrivateKey),
                Base64.getEncoder().encodeToString(publicKey.getEncoded())
        );
    }
}
