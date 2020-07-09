package authentication;

public interface PAT extends Authentication{
	
	public enum securityLevel {NONE, LOW, HIGH};
	
	/**
	 * Decrypt token in PAT object
	 * @param password password to decrypt with
	 */
	void encryptToken(String password);
	
	/**
	 * Encrypt token in PAT object
	 * @param password password to encrypt with
	 */
	void decryptToken(String password);
	
	/**
	 * tests if token is encrpyed 
	 * @return true if token is encrypted, false otherwise
	 */
	boolean isEncrypted();
	
	/**
	 * tests if token is decrpyed
	 * @return true if token is decrypted, false otherwise
	 */
	boolean isDecrypted();
}
