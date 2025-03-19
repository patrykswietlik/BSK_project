package pl.bsk.project.bsk_project.cipher;

import pl.bsk.project.bsk_project.utils.EnvHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AsymmetricKey;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class RSACipher {

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private final String pin;

    public RSACipher(String pin) {
        if (!AESCipher.pinIsValid(pin)) {
            throw new IllegalArgumentException("Pin is not valid");
        }

        this.pin = pin;
    }

    public void generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                EnvHandler.getSystemEnv("PRIVATE_KEY_FORMAT_ALGORITHM")
        );
        keyPairGenerator.initialize(4096);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void saveKey(String path, KeyType type) throws Exception {
        boolean isPrivateKey = type == KeyType.PRIVATE_KEY;

        File dest = new File(path, isPrivateKey ? "private_key.bin" : "public_key.bin" );
        byte[] keyToSave = isPrivateKey ? AESCipher.encrypt(pin, privateKey.getEncoded()) : getPublicKey().getEncoded();

        try (FileOutputStream outputStream = new FileOutputStream(dest)) {
            outputStream.write(
                    keyToSave
            );
        }
    }

    public AsymmetricKey loadKey(String path, KeyType type) throws Exception {
        boolean isPrivateKey = type == KeyType.PRIVATE_KEY;

        byte[] key = Files.readAllBytes(Paths.get(path, isPrivateKey ? "private_key.bin" : "public_key.bin"));
        byte[] decoded = isPrivateKey ? AESCipher.decrypt(pin, key) : key;

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance(
                EnvHandler.getSystemEnv("PRIVATE_KEY_FORMAT_ALGORITHM")
        );

        if (isPrivateKey) {
            return keyFactory.generatePrivate(keySpec);
        }

        return keyFactory.generatePublic(keySpec);

    }
}
