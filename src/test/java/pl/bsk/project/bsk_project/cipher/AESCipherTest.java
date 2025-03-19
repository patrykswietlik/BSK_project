package pl.bsk.project.bsk_project.cipher;

import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class AESCipherTest {

    @Test
    public void pinIsValidShouldReturnTrueWhenGivenValidPins() throws Exception {
        Method method = AESCipher.class.getDeclaredMethod("pinIsValid", String.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(AESCipher.class, "1234"));
        assertTrue((boolean) method.invoke(AESCipher.class, "5151"));
        assertTrue((boolean) method.invoke(AESCipher.class, "9900"));
        assertTrue((boolean) method.invoke(AESCipher.class, "0900"));
        assertTrue((boolean) method.invoke(AESCipher.class, "1415"));

    }

    @Test
    public void pinIsValidShouldReturnFalseWhenGivenNotValidPins() throws Exception {
        Method method = AESCipher.class.getDeclaredMethod("pinIsValid", String.class);
        method.setAccessible(true);

        assertFalse((boolean) method.invoke(AESCipher.class, "a124"));
        assertFalse((boolean) method.invoke(AESCipher.class, "124"));
        assertFalse((boolean) method.invoke(AESCipher.class, "1 21"));
        assertFalse((boolean) method.invoke(AESCipher.class, "12345"));
        assertFalse((boolean) method.invoke(AESCipher.class, "ac!$"));
    }

    @Test
    public void decryptShouldReturnInputDataGivenCorrectPin() throws Exception {
        String pin = "0941";
        String input = "TEST_CASE";

            byte[] encrypted = AESCipher.encrypt(pin, input.getBytes());
            byte[] decrypted = AESCipher.decrypt(pin, encrypted);

            String result = AESCipher.toString(decrypted);

            assertEquals(input, result);
    }

    @Test
    public void decryptShouldThrowGivenIncorrectPin() throws Exception {
        String pin = "1112";
        String fakePin = "1111";
        String input = "TEST_CASE_2";

        byte[] encrypted = AESCipher.encrypt(pin, input.getBytes());

        assertThrows(BadPaddingException.class, () -> {
            AESCipher.decrypt(fakePin, encrypted);
        });
    }
}