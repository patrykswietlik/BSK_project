package pl.bsk.project.bsk_project.cipher;

import org.bouncycastle.eac.operator.EACSignatureVerifier;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import pl.bsk.project.bsk_project.utils.EnvHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AsymmetricKey;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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
        keyPairGenerator.initialize(
               Integer.parseInt(
                       EnvHandler.getSystemEnv("KEY_LENGTH")
               )
        );

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void saveKeyPem(String path, KeyType type) throws Exception {
        boolean isPrivateKey = type == KeyType.PRIVATE_KEY;

        byte[] keyToSave = isPrivateKey ? AESCipher.encrypt(pin, privateKey.getEncoded()) : getPublicKey().getEncoded();

        PemObject pemObject = new PemObject(type.toString(), keyToSave);
        File dest = new File(path, isPrivateKey ? "private_key.pem" : "public_key.pem" );

        try (PemWriter pemWriter = new PemWriter(new FileWriter(dest))) {
            pemWriter.writeObject(pemObject);
        }
    }

    public AsymmetricKey loadKeyPem(String path, KeyType type) throws Exception {
        boolean isPrivateKey = type == KeyType.PRIVATE_KEY;

        try (PemReader pemReader = new PemReader(new FileReader(String.valueOf(Paths.get(path, isPrivateKey ? "private_key.pem" : "public_key.pem"))))) {
            PemObject pemObject = pemReader.readPemObject();
            byte[] key = pemObject.getContent();
            byte[] decoded = isPrivateKey ? AESCipher.decrypt(pin, key) : key;

            KeyFactory keyFactory = KeyFactory.getInstance(
                    EnvHandler.getSystemEnv("PRIVATE_KEY_FORMAT_ALGORITHM")
            );

            if (isPrivateKey) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
                return keyFactory.generatePrivate(keySpec);
            } else {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
                return keyFactory.generatePublic(keySpec);
            }
        }
    }
}
