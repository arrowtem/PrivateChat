package com.example.myapplication.utilities;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Java String Encryption Decryption Example
 * @author Ramesh Fadatare
 *
 */
public class encryption {

    public String encrypt(String password, String key) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException, ShortBufferException {
        byte[] KeyData = key.getBytes();
        SecretKeySpec KS = new SecretKeySpec(KeyData, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, KS);
        String encryptedtext = Base64.encodeToString(cipher.doFinal(password.getBytes("UTF-8")),Base64.DEFAULT);
        return encryptedtext;

    }

    public String decrypt(String encryptedtext, String key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        byte[] KeyData = key.getBytes();
        SecretKeySpec KS = new SecretKeySpec(KeyData, "Blowfish");
        byte[] ecryptedtexttobytes = Base64.decode(encryptedtext,Base64.DEFAULT);

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, KS);
        byte[] decrypted = cipher.doFinal(ecryptedtexttobytes);
        String decryptedString =
                new String(decrypted, Charset.forName("UTF-8"));
        return decryptedString;

    }
    }

