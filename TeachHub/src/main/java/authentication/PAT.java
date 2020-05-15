package authentication;

public interface PAT extends Authentication{
	
	public enum securityLevel {NONE, LOW, HIGH};
	void encryptToken(String password);
	void decryptToken(String password);
	boolean isEncrypted();
	boolean isDecrypted();

}
