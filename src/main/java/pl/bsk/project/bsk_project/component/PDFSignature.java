package pl.bsk.project.bsk_project.component;

import pl.bsk.project.bsk_project.utils.EnvHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;


public class PDFSignature {

    public static void sign(File inputFile, File outputFile, PrivateKey privateKey) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {

            byte[] fileBytes = fileInputStream.readAllBytes();

            Signature signature = Signature.getInstance(
                    EnvHandler.getSystemEnv("SIGNATURE_ALGORITHM")
            );

            signature.initSign(privateKey);
            signature.update(fileBytes);
            byte[] signedData = signature.sign();

            fileOutputStream.write(fileBytes);
            fileOutputStream.write(signedData);
        }
    }

    public static boolean verify(File file, PublicKey publicKey) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            byte[] fileBytes = fileInputStream.readAllBytes();

            final int signatureLengthInBytes = Integer.parseInt(
                    EnvHandler.getSystemEnv("KEY_LENGTH")
            ) / 8;

            byte[] originalData = new byte[fileBytes.length - signatureLengthInBytes];
            byte[] signatureBytes = new byte[signatureLengthInBytes];

            System.arraycopy(fileBytes, 0, originalData, 0, originalData.length);
            System.arraycopy(fileBytes, originalData.length, signatureBytes, 0, signatureLengthInBytes);

            Signature signature = Signature.getInstance(
                    EnvHandler.getSystemEnv("SIGNATURE_ALGORITHM")
            );

            signature.initVerify(publicKey);
            signature.update(originalData);

            return signature.verify(signatureBytes);
        }
    }

}
