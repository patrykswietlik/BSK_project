package pl.bsk.project.bsk_project.component;

import java.io.File;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class PDFSigner {

    public static byte[] sign(File file, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        byte[] data = Files.readAllBytes(file.toPath());
        signature.update(data);

        return signature.sign();
    }

    public static boolean verify(File file, PublicKey publicKey, byte[] sign) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);

        byte[] data = Files.readAllBytes(file.toPath());
        signature.update(data);

        return signature.verify(sign);
    }
}
