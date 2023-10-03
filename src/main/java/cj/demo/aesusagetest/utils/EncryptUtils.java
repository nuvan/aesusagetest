package cj.demo.aesusagetest.utils;

import java.util.Random;

public class EncryptUtils {

    public static String generateRandomString(int targetStringLength){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public static String encryptAESCBC(String message, String aesKey) throws Exception {
        return AESCBCEncryption.encrypt(message, aesKey);
    }
    public static String decryptAESCBC(String message, String aesKey) throws Exception {
        return AESCBCEncryption.decrypt(message, aesKey);
    }
    public static String encryptAESGCM(String message, String aesKey) throws Exception {
        return AESGCMEncryption.encrypt(message, aesKey);
    }
    public static String decryptAESGCM(String message, String aesKey) throws Exception {
        return AESGCMEncryption.decrypt(message, aesKey);
    }
}
