package my.practice.user.security;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 암호화
 */
public class CryptoAES {
    private final SecretKey key = new SecretKeySpec(
            "jXzpIkVj5obCdorBRkKBXHLQk2tW2B8=".getBytes(StandardCharsets.UTF_8),
            "AES");

    public String encrypt(byte[] contents) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new String(cipher.doFinal(contents));
    }

    public byte[] decrypt(byte[] contents) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(contents);
    }

}
