package cj.demo.aesusagetest.utils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESGCMEncryption {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String FACTORY_INSTANCE = "PBKDF2WithHmacSHA256";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final String ALGORITHM_TYPE = "AES";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static byte[] getRandomNonce(int length) {
        byte[] nonce = new byte[length];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    public static SecretKey getSecretKey(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);

        SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY_INSTANCE);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM_TYPE);
    }

    public static String encrypt(String password, String plainMessage) throws Exception {
        byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
        SecretKey secretKey = getSecretKey(password, salt);

        // GCM recommends 12 bytes iv
        byte[] iv = getRandomNonce(IV_LENGTH_BYTE);
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] encryptedMessageByte = cipher.doFinal(plainMessage.getBytes(UTF_8));

        // prefix IV and Salt to cipher text
        byte[] cipherByte = ByteBuffer.allocate(iv.length + salt.length + encryptedMessageByte.length)
                .put(iv)
                .put(salt)
                .put(encryptedMessageByte)
                .array();
        return Base64.getEncoder().encodeToString(cipherByte);
    }

    public static String decrypt(String password, String cipherMessage) throws Exception {
        byte[] decodedCipherByte = Base64.getDecoder().decode(cipherMessage.getBytes(UTF_8));
        ByteBuffer byteBuffer = ByteBuffer.wrap(decodedCipherByte);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);

        byte[] salt = new byte[SALT_LENGTH_BYTE];
        byteBuffer.get(salt);

        byte[] encryptedByte = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedByte);

        SecretKey secretKey = getSecretKey(password, salt);
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] decryptedMessageByte = cipher.doFinal(encryptedByte);
        return new String(decryptedMessageByte, UTF_8);
    }

    private static Cipher initCipher(int mode, SecretKey secretKey, byte[] iv) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        return cipher;
    }
/*
    String outputFormat = "%-25s:%s%n";
    String password = "yourSecretKey";
    String message = "M0993000353";
    String cipherText = encrypt(password, message);

    System.out.println("------ AES-GCM Encryption ------");
    System.out.printf(outputFormat, "encryption input", message);
    System.out.printf(outputFormat, "encryption output", cipherText);
    String decryptedText = decrypt(password, cipherText);

    System.out.println("\n------ AES-GCM Decryption ------");
    System.out.printf(outputFormat, "decryption input", cipherText);
    System.out.printf(outputFormat, "decryption output", decryptedText);
 */
}
