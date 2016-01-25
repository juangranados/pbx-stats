package com.pbx_stats.tools;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
/**
 * Clase que sirve para encriptar y desencriptar una cadena de texto.
 * @author Juan Granados
 *
 */
public class AdvancedEncryptionStandard
{
	/**
	 * M�todo que encripta una cadena de caracteres en funcion de una clave.
	 * @param plainText Contrase�a en texto plano
	 * @param encryptionKey Clave de encriptaci�n
	 * @return Cadena de caracteres encriptada
	 * @throws Exception
	 */
    public static String encrypt(String plainText,String encryptionKey) throws Exception
    {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE,encryptionKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        return Base64.encodeBase64String(encryptedBytes);
    }
    /**
     * M�todo que desencripta una cadena de caracteres en funcion de una clave.
     * @param encrypted Cadena de caracteres encriptada
     * @param encryptionKey Clave de encriptaci�n
     * @return Cadena de caracteres desencriptada
     * @throws Exception
     */
    public static String decrypt(String encrypted,String encryptionKey) throws Exception
    {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE,encryptionKey);
        byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));

        return new String(plainBytes);
    }
    /**
     * M�todo que genera una clase Cipher (cifrado criptogr�fico) en base a una clave de encriptaci�n.
     * @param cipherMode M�todo de cifrado, encriptar o desencriptar
     * @param encryptionKey Clave de encriptaci�n
     * @return Clase Cipher
     * @throws Exception
     */
    private static Cipher getCipher(int cipherMode,String encryptionKey)
            throws Exception
    {
        String encryptionAlgorithm = "AES";
        SecretKeySpec keySpecification = new SecretKeySpec(
                encryptionKey.getBytes("UTF-8"), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);

        return cipher;
    }
}