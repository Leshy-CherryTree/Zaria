/****************************************/
/* Encrypter.java			*/
/* Created on: Feb 2, 2012		*/
/* Copyright Cherry Tree Studio 2012	*/
/* Released under EUPL v1.1		*/
/* Ported from the jPAJ project.	*/
/****************************************/

package eu.cherrytree.zaria.utilities;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class Encrypter
{
	//--------------------------------------------------------------------------

	public static class KeyGenerationException extends Exception
	{
		private static final long serialVersionUID = -8817918790895626947L;

		public KeyGenerationException(String msg)
		{
			super("Couldn't generate secret key." + msg);
		}
	}

	//--------------------------------------------------------------------------

	public class EncryptionIntializationException extends Exception
	{
		private static final long serialVersionUID = 4224946478738059899L;

		public EncryptionIntializationException(Throwable cause)
		{
			super("Couldn't initialize DES encryption mechanism.",cause);
		}
	}

	//--------------------------------------------------------------------------

	public class DecryptionException extends Exception
	{
		private static final long serialVersionUID = -7436565476614246834L;

		public DecryptionException(String msg)
		{
			super("Couldn't decrypt string." + msg);
		}
	}

	//--------------------------------------------------------------------------

	public class EncryptionException extends Exception
	{
		private static final long serialVersionUID = -380269770134789123L;

		public EncryptionException(String msg)
		{
			super("Couldn't encrypt string." + msg);
		}
	}

	//--------------------------------------------------------------------------

	private Cipher ecipher;
	private Cipher dcipher;

	//--------------------------------------------------------------------------

	public Encrypter(SecretKey key) throws EncryptionIntializationException
	{
		try
		{
			ecipher = Cipher.getInstance("DES");
			dcipher = Cipher.getInstance("DES");

			ecipher.init(Cipher.ENCRYPT_MODE, key);
			dcipher.init(Cipher.DECRYPT_MODE, key);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e)
		{
			throw new EncryptionIntializationException(e);
		}
	}

	//--------------------------------------------------------------------------

	public String encrypt(String str) throws EncryptionException
	{
		try
		{	
			return Base64.encodeToString(ecipher.doFinal(str.getBytes("UTF8")),false);
		}
		catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e)
		{
			throw new EncryptionException(e.getMessage());
		}
	}

	//--------------------------------------------------------------------------

	public String decrypt(String str) throws DecryptionException
	{
		try
		{
			return new String(dcipher.doFinal(Base64.decode(str)), "UTF8");
		}
		catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e)
		{
			throw new DecryptionException(e.getMessage());
		}
	}

	//--------------------------------------------------------------------------

	public static SecretKey generateKey() throws KeyGenerationException
	{
		try
		{
			return KeyGenerator.getInstance("DES").generateKey();
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new KeyGenerationException(e.getMessage());
		}
	}

	//--------------------------------------------------------------------------
}
