package client;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA {

    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static final String PRIVATE_KEY_STRING ="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAISHt34lq6eEBnzdN4RF5byJ/jNft2zoMewsOduQNmWSj6BaDDAG+7+okc2H5w8nLuFY2DtAyVQGd28b+uhNp8ThhWJ1B01ZQPLozRshciVJseEq5SAIlEV3hNB+9WOAOddg+Q+0eAn052xxqM4ShGm7IfWJOBnScz7Xd0d82bDTAgMBAAECgYAry5GGfTpZVBa0woGrE1IU2OEI5xN9SMneisrf1Ks1K5aM3dTzNguRTUpJSiS/7Cngy/RAuGsPekeTjwJ+K9NJjndZuxiIyub+HdOAzGLHXtYd2VXdecqSmU7MRnQQ6z+ye2xKpGbI/cu6yAouUyxX4lh5ffAv6cTJCkqZw3ck8QJBAJKoigd6Zvg3iWZcFNA8mHb9PitIpdQM0pOv6C4c41mU7uIbKvnq6OpkPG3e3mLgGDNAFZPoAuROkIvemNjwQosCQQDnVqSl7oo1TYjNC9IVQoInT+eftq+db9acRsrqM/DYa2pqPg3DCJFLhH4p9UpGg0p3OQfNAhk/Y+ZlLjmFOfvZAkA9/Tkigg1F+4t7FY7CmepD2TTO7M0S/TyAqki0PQen1LjIH32h1zYN5MJaGmrbVidEemDkfJvETqTwSe1HPP6TAkEAriEnmcatvWV5HW5SWbJQKasZm7x0/7pY/hTDU/p4xU5FMWc4EJ/4TbGTDZ4WpHDaslb5KjT0MCTQHUToAwIcMQJAW4Kcu+QwmZu3jTCiLqzRmo4JQTFGAtOmC+4egVSrhZM081fHMJC6Q0T43y0O7qi5w0LH3Nu1UcAfLx5zV629zg==";
    private static final String PUBLIC_KEY_STRING = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEh7d+JaunhAZ83TeEReW8if4zX7ds6DHsLDnbkDZlko+gWgwwBvu/qJHNh+cPJy7hWNg7QMlUBndvG/roTafE4YVidQdNWUDy6M0bIXIlSbHhKuUgCJRFd4TQfvVjgDnXYPkPtHgJ9OdscajOEoRpuyH1iTgZ0nM+13dHfNmw0wIDAQAB";


    // Initializes the KeyPairGenerator with the RSA algorithm and a key length of 1024 bits
    public static void init() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        } catch (Exception e) {
            // Catch and log any exceptions that may occur while generating the key pair
        }
    }

    // Initializes the public and private keys from their string representations
    public static void initFromStrings(){
        try {
            // Decode the public key string into a byte array
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(PUBLIC_KEY_STRING));
            // Decode the private key string into a byte array
            PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(PRIVATE_KEY_STRING));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            // Generate the public and private keys from their respective byte arrays
            publicKey = keyFactory.generatePublic(keySpecPublic);
            privateKey = keyFactory.generatePrivate(keySpecPrivate);
        }
        catch (Exception e){
            // Catch and log any exceptions that may occur while initializing the keys from strings
        }
    }

    // Prints the string representations of the public and private keys to the error stream
    public static void printKeys(){
        System.err.println("Public key\n"+ encode(publicKey.getEncoded()));
        System.err.println("Private key\n"+ encode(privateKey.getEncoded()));
    }

    // Encrypts the input message using the public key
    public static String encrypt(String message) throws Exception{
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    // Encodes the input byte array into a Base64 string
    private static String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    // Decrypts the input encrypted message using the private key
    public static String decrypt(String encryptedMessage) throws Exception{
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage, "UTF8");
    }

    // Decodes the input Base64 string into a byte array
    private static byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }

}
