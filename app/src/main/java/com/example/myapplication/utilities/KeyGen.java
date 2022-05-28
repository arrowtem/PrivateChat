package com.example.myapplication.utilities;

import android.util.Base64;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyGen {



    public static KeyPair generateKeys() {
        KeyPair keyPair = null;
        try {
            // get instance of rsa cipher
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
            keyGen.initialize(1024);            // initialize key generator
            keyPair = keyGen.generateKeyPair(); // generate pair of keys
        } catch(GeneralSecurityException e) {

            System.out.println(e);
        }
        return keyPair;
    }



    public static String toStringPrivate(PrivateKey key)
    {
        String privateKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        return privateKey;
    }


    public static PrivateKey getKeyPrivate(String base64encoded){
        try{
            byte[] byteKey = Base64.decode(base64encoded.getBytes(), Base64.DEFAULT);
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory factory = KeyFactory.getInstance("DSA");
            return factory.generatePrivate(keySpec);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String toStringPublic(PublicKey key)
    {
        String publicKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        return publicKey;
    }


    public static PublicKey getKeyPublic(String base64encoded){
        try{
            byte[] byteKey = Base64.decode(base64encoded.getBytes(), Base64.DEFAULT);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory factory = KeyFactory.getInstance("DSA");
            return factory.generatePublic(keySpec);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] performSigning(PrivateKey privateKey,String data) throws Exception {

        //Creating a Signature object
        Signature sign = Signature.getInstance("DSA");

        //Initializing the signature
        sign.initSign(privateKey);
        byte[] bytes = data.getBytes();

        //Adding data to the signature
        sign.update(bytes);

        //Calculating the signature
        byte[] signature = sign.sign();

        return signature;
    }

    public static boolean performVerification( byte[] signature, PublicKey publicKey,String data) throws Exception {
        Signature sign = Signature.getInstance("DSA");
        sign.initVerify(publicKey);
        sign.update(data.getBytes());
        //Verifying the signature
        boolean bool = sign.verify(signature);
       if(bool==true){
           return true;
       }else
       {return false;}
       }

       public static boolean tryToCheck() throws Exception {
           KeyPair pair = generateKeys();
           byte [] signature = performSigning(pair.getPrivate(),"boba");
           boolean check = performVerification(signature,pair.getPublic(),"boba");
        return check;
       }
    }

