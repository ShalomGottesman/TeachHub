package pat_manager.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Encryption {
	private String initVector = "zFLVIU4RWrTigwmL";
	
	public String encrypt(String toBeEncrypted, String key) {
		if(key.length() != 16) {
			return null;
		}
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(toBeEncrypted.getBytes());
            return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			System.out.println("encryption failed");
			ex.printStackTrace();
            return null;
        }
	}
	
	public String decrypt(String encrypted, String key) {
		if(key.length() != 16) {
			return null;
		}
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] decrypted = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));
            return new String(decrypted);
		} catch (Exception ex) {
            System.out.println("decryption failed");
			ex.printStackTrace();
            return null;
        }
	}
}
