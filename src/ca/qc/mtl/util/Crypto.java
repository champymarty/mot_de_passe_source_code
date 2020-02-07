/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.mtl.util;

import encrypteurFichier.AESKey;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author raphael
 */
public abstract class Crypto {

    private static final int PBKDF2_ITERATIONS = 9000;

    private static final int SALT_BYTE_CONNECTION = 512;
    private static final int HASH_BYTE_CONNECTION = 512;

    private static final int RSA_KEY_SIZE = 2048;

    public static final int AES_KEY_SIZE = 256;
    public static final int SALT_BYTE_AES = 256;

    private static final String STRING_ENCRYPTED_FILE = "Encrypted_file";
    public static final int IV_LENGTH = 16;

    public static SecretKey generateAESKeyFromPassword(char[] password, byte[] salt, int numberOfIteration, int AESKeySize) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, numberOfIteration, AESKeySize);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secret;
    }

    public static byte[] genererRandomIv() {
        byte[] iv = generateSalt(IV_LENGTH);
        return iv;
    }

    public static String generateStrongPasswordHash(String password) {
        try {
            char[] chars = password.toCharArray();
            byte[] salt = generateSalt(SALT_BYTE_CONNECTION);

            PBEKeySpec spec = new PBEKeySpec(chars, salt, PBKDF2_ITERATIONS, HASH_BYTE_CONNECTION);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            spec.clearPassword();
            return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" + toHex(hash);
        } catch (NoSuchAlgorithmException ex) {
        } catch (InvalidKeySpecException ex) {
        }
        return null;
    }

    public static boolean validatePassword(String originalPassword, String storedPassword) {
        try {
            String[] parts = storedPassword.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = fromHex(parts[1]);
            byte[] hash = fromHex(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            int diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            spec.clearPassword();
            return diff == 0;
        } catch (NoSuchAlgorithmException ex) {
        } catch (InvalidKeySpecException ex) {
        }
        return false;
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static byte[] generateSalt(int saltLenght) {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[saltLenght];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    public static String decryptString(String data, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException {
        return decryptFromBytes(Base64.getDecoder().decode(data.getBytes()), privateKey);
    }

    private static String decryptFromBytes(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    private static byte[] encryptToBytes(String data, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    public static String encryptString(String data, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        return Base64.getEncoder().encodeToString(encryptToBytes(data, publicKey));
    }

    public static Object[] generateAESKey() throws NoSuchAlgorithmException {
        Object[] tab = new Object[2];
        Key key;
        SecureRandom rand = new SecureRandom();
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256, rand);
        key = generator.generateKey();

        byte[] iv = new byte[16];
        rand.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        tab[0] = key;
        tab[1] = ivspec;

        return tab;
    }

    public static void encryptImage(AESKey key, File inputFile, File outputFile) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, FileNotFoundException,
            IOException, IllegalBlockSizeException, BadPaddingException {
        if (inputFile.getAbsolutePath().equals(outputFile.getAbsolutePath())) {
            File outFileTemp = new File(outputFile.getParent(), "temp_" + outputFile.getName());
            processEncryptImage(key, inputFile, outFileTemp);

            String path = outputFile.getAbsolutePath();
            inputFile.delete();
            outputFile.delete();
            outFileTemp.renameTo(new File(path));
        } else {
            processEncryptImage(key, inputFile, outputFile);
        }

    }

    public static void decryptImage(AESKey key, File inputFile, File outputFile) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, FileNotFoundException,
            IOException, IllegalBlockSizeException, BadPaddingException {
        if (inputFile.getAbsolutePath().equals(outputFile.getAbsolutePath())) {
            File outFileTemp = new File(outputFile.getParent(), "temp_" + outputFile.getName());
            processDecryptImage(key, inputFile, outFileTemp);

            String path = outputFile.getAbsolutePath();
            inputFile.delete();
            outputFile.delete();
            outFileTemp.renameTo(new File(path));
        } else {
            processDecryptImage(key, inputFile, outputFile);
        }
    }

    private static void processEncryptImage(AESKey key, File inputFile, File outputFile) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, FileNotFoundException,
            IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key.getSecretKey(), key.getIvParameterSpec());

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);
        outputStream.write(key.getIvParameterSpec().getIV());

        inputStream.close();
        outputStream.close();
        FileWriter writer = new FileWriter(outputFile, true);
        writer.write(STRING_ENCRYPTED_FILE);
        writer.close();
    }

    private static void processDecryptImage(AESKey key, File inputFile, File outputFile) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, FileNotFoundException,
            IOException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key.getSecretKey(), key.getIvParameterSpec());

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length() - STRING_ENCRYPTED_FILE.getBytes().length - IV_LENGTH];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();
    }

    public static boolean isImageEncrypter(File file) throws FileNotFoundException, IOException {
        boolean imageEncrypt = false;
        BufferedReader entree;
        int c;

        entree = new BufferedReader(new FileReader(file));
        if (file.length() >= STRING_ENCRYPTED_FILE.getBytes().length) {
            entree.skip(file.length() - STRING_ENCRYPTED_FILE.getBytes().length);
            c = entree.read();
            if (c == STRING_ENCRYPTED_FILE.charAt(0)) {
                int i = 1;
                boolean pareil = true;
                while (i < STRING_ENCRYPTED_FILE.length() && entree.ready() && pareil) {
                    c = entree.read();
                    if (c != STRING_ENCRYPTED_FILE.charAt(i)) {
                        pareil = false;
                    }
                    i++;
                    if (i == STRING_ENCRYPTED_FILE.length() && pareil) {
                        imageEncrypt = true;
                    }
                }
            }
            entree.close();
            return imageEncrypt;
        }
        entree.close();
        return false;
    }

    public static IvParameterSpec getIvParameterSpecFromImage(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        inputStream.skipNBytes((long) file.length() - STRING_ENCRYPTED_FILE.getBytes().length - IV_LENGTH);
        byte[] ivBytes = new byte[IV_LENGTH];
        inputStream.read(ivBytes);
        inputStream.close();
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        return iv;
    }

    public static Object[] encryptAESKeyWithRSA(PublicKey publicRSAKey, Object[] tab) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Object[] tabTemp = new Object[2];
        tabTemp[0] = tab[0];
        tabTemp[1] = tab[1];

        SecretKey skey = (SecretKey) tabTemp[0];
        IvParameterSpec ivspec = (IvParameterSpec) tabTemp[1];

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicRSAKey);

        byte[] AESkey = cipher.doFinal(skey.getEncoded());
        byte[] AESVecteurIv = cipher.doFinal(ivspec.getIV());

        tabTemp[0] = AESkey;
        tabTemp[1] = AESVecteurIv;

        return tabTemp;
    }

    public static Object[] decryptAESKeyWithRSA(PrivateKey privateRSAKey, Object[] tab) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Object[] tabTemp = new Object[2];
        tabTemp[0] = tab[0];
        tabTemp[1] = tab[1];

        byte[] key = (byte[]) tabTemp[0];
        byte[] vecteurIv = (byte[]) tabTemp[1];

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateRSAKey);
        byte[] keyDecrypt = cipher.doFinal(key);
        byte[] vecteurIvDecrypt = cipher.doFinal(vecteurIv);

        SecretKey secretAESKey = new SecretKeySpec(keyDecrypt, 0, keyDecrypt.length, "AES");
        IvParameterSpec ivspec = new IvParameterSpec(vecteurIvDecrypt);
        tabTemp[0] = secretAESKey;
        tabTemp[1] = ivspec;
        return tabTemp;
    }

    public static SecretKey generateAESKeyFromPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 80, AES_KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secret;
    }

    public static String encryptStringWithAES(String strToEncrypt, SecretKey secretKey, IvParameterSpec ivspec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
    }

    public static String decryptStringWithAES(String strToDecrypt, SecretKey secretKey, IvParameterSpec ivspec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)), "UTF-8");
    }

    public static IvParameterSpec genererVecteurAESKey(byte[] iv) {
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        return ivSpec;
    }

    public static byte[] genererIv() {
        SecureRandom rand = new SecureRandom();
        byte[] iv = generateSalt(IV_LENGTH);
        return iv;
    }

    public static String genererRandomPasswordMelangeur(int stringLength) {
        String string = "";
        SecureRandom random = new SecureRandom();
        int nb;

        for (int i = 0; i < stringLength; i++) {
            nb = random.nextInt(9);
            string = string + Integer.toString(nb);
        }
        return string;
    }
}
