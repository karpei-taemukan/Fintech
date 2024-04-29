package com.zerobase.token.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class Aes256Util {

  private static final String ALG = "AES/CBC/PKCS5Padding";
  private static final String KEY = "UJtSyGesuVnZxmDY";
  private static final String IV = KEY.substring(0,16);


    public static String encrypt(String text){
      try{

        Cipher cipher = Cipher.getInstance(ALG);

        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE,keySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64String(encrypted);
      }catch (InvalidAlgorithmParameterException | NoSuchPaddingException |
              IllegalBlockSizeException |
              NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
        throw new RuntimeException(e);
      }
    }

public static String decrypt(String cipherText){
      try{
        Cipher cipher = Cipher.getInstance(ALG);
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] decodedBytes = Base64.decodeBase64(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);

        return new String(decrypted, StandardCharsets.UTF_8);

      }catch (NoSuchPaddingException | NoSuchAlgorithmException |
              InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException |
              BadPaddingException e) {
        return null;
      }
}

}
