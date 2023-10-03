package cj.demo.aesusagetest.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESCBCEncryption {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16; // 128 bits IV for AES

    public static String encrypt(String message, String aesKey) throws Exception {
        // Generate a random IV (Initialization Vector)
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Initialize the cipher in encryption mode
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey.getBytes(), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        // Encrypt the message
        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted message into a single byte array
        byte[] combined = new byte[IV_LENGTH + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
        System.arraycopy(encryptedBytes, 0, combined, IV_LENGTH, encryptedBytes.length);

        // Encode the result in Base64 for easier storage and transmission
        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String encryptedMessage, String aesKey) throws Exception {
        // Decode the Base64-encoded message
        byte[] combined = Base64.getDecoder().decode(encryptedMessage);

        // Extract the IV and the encrypted message
        byte[] iv = Arrays.copyOfRange(combined, 0, IV_LENGTH);
        byte[] encryptedBytes = Arrays.copyOfRange(combined, IV_LENGTH, combined.length);

        // Initialize the cipher in decryption mode
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey.getBytes(), ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        // Decrypt the message
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // Convert the decrypted bytes back to a string
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}
