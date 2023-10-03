package cj.demo.aesusagetest.service;

import cj.demo.aesusagetest.utils.EncryptUtils;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import java.net.URI;

@RefreshScope
@Configuration
public class RequestConfidentialInfoService {

    @Value( "${target.url}" )
    private String targetUrl;
    @Value( "${aes.key}" )
    private String aesKey;
    @Value( "${aes.mode}" )
    private String aesMode;

    //https://github.com/spring-cloud/spring-cloud-config/issues/703
    //@Scheduled(fixedRate = 5000)
    public void decryptMessage() throws Exception {
        int randomStringSize = 10;
        String message = EncryptUtils.generateRandomString(randomStringSize);
        System.out.println("Cipher mode:" + this.aesMode);
        System.out.println("Message:" + message);

        message = switch (this.aesMode) {
            case "CBC" -> generateAESCBCEncryptedMessage(message);
            case "GCM" -> generateAESGCMEncryptedMessage(message);
            default -> generateAESCBCEncryptedMessage(message);
        };
        System.out.println("Encrypted Message:" + message);
        message = randomTrudyTampering(message);
        System.out.println("Possibly tampered Message:" + message);
        ResponseEntity<String> response = requestDecryption(message);
        System.out.println("Decrypted Message:" + response);
    }

    private String generateAESCBCEncryptedMessage(String message) throws Exception {
        return EncryptUtils.encryptAESCBC(message, this.aesKey);
    }

    private String generateAESGCMEncryptedMessage(String message) throws Exception {
        return EncryptUtils.encryptAESGCM(message, this.aesKey);
    }

    private ResponseEntity<String> requestDecryption(String encryptedMessage) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity requestEntity = new HttpEntity<>(headers);

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        URI uri = factory.uriString(targetUrl).build(encryptedMessage, this.aesMode);
        return restTemplate.exchange(uri.toString(), HttpMethod.GET, requestEntity, String.class);
    }

    private static String randomTrudyTampering(String message) {
        if(randomThird() != 3){
            return message;
        }
        int pos = randomThird();
        message = message.substring(0,pos) +
                EncryptUtils.generateRandomString(1) +
                message.substring(pos+1);

        return message;
    }

    private static int randomThird() {
        int min = 1; // Minimum value of the range
        int max = 3; // Maximum value of the range
        // Create a Random object
        Random random = new Random();
        // Generate a random integer within the specified range
        return random.nextInt(max - min + 1) + min;
    }

}
