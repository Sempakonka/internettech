package client;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AES {
    private static SecretKeySpec  secretKey;//needs to be 16 Bytes
    private static byte[] key;

    public static void setKey(String myKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        key = myKey.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        key = java.util.Arrays.copyOf(key, 16);
        secretKey = new SecretKeySpec(key, "AES");
    }

    public static String encrypt(String strToEnc, String sec){
        try {
            setKey(sec);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEnc.getBytes("UTF-8")));
        }
        catch (Exception e){
            System.out.println("Error during encryption: " + e.getMessage());
        }
        return null;
    }
    public static String decrypt(String strToDec, String sec){
        try {
            setKey(sec);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDec)));
        }
        catch (Exception e){
            System.out.println("Error during decryption: " + e.getMessage());
        }
        return null;
    }
}
