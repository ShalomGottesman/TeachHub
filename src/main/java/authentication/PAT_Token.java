package authentication;

import java.io.IOException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;

import pat_manager.security.Encryption;

public class PAT_Token implements PAT{
	private String userName;
	private String token;
	private String encryptedToken;
	private PAT.securityLevel secLevel;
	private int keyHash;
	private String PAT_Name;
	
	public PAT_Token(String userName, String token, PAT.securityLevel secLevel, String PAT_Name)throws InvalidCredentialException {
		this.userName = userName;
		this.token = token;
		this.encryptedToken = null;
		this.secLevel = secLevel;
		this.PAT_Name = PAT_Name;
		verify();
	}

	public PAT.securityLevel getSecLevel() {
		return secLevel;
	}

	public String getJsonName() {
		if (PAT_Name == null) {
			return "["+userName + "]-[" + secLevel.toString() +"].json";
		} else {
			return "["+userName + "]-[" +PAT_Name + "]-[" + secLevel.toString() +"].json";
		}
	}
	
	public boolean hashCompare(int hash) {
		return this.keyHash == hash;
	}
	
	
	private void verify() throws InvalidCredentialException {
		Github github = this.authenticate();
		try {
			github.users().self().login();
		} catch (AssertionError | IOException e) {
			throw new InvalidCredentialException("the credentials provided are not valid");
		}
	}
	

	@Override
	public Github authenticate() {
		Github github = new RtGithub(userName, token);
		return github;
	}

	@Override
	public void encryptToken(String password) {
		String passwordToUse = qualifyPassword(password);
		Encryption enyr = new Encryption();
		String encrypted = enyr.encrypt(this.token, passwordToUse);
		this.encryptedToken = encrypted;
		this.token = null;
		this.keyHash = password.hashCode();
	}

	@Override
	public void decryptToken(String password) {
		String passwordToUse = qualifyPassword(password);
		Encryption enyr = new Encryption();
		String decrypted = enyr.decrypt(this.encryptedToken, passwordToUse);
		try {
			this.token = decrypted;
			verify();
			this.encryptedToken = null;
			System.out.println("decryption sucessful");
		} catch (InvalidCredentialException e){
			this.token = null;
			e.printStackTrace();
		}	
	}
	
	/**
	 * DES encryption requires that the password be 16 chars in length. This takes whatever it is passed an 
	 * Deterministically returns a 16 char sequence.
	 * @param password the password to convert
	 * @return the 16 char password for encryption
	 */
	private String qualifyPassword(String password) {
		String passwordToUse = "";
		if (password.length() >= 16) {
			passwordToUse = password.substring(0, 16);
		} 
		if(password.length() == 16) {
			passwordToUse = password;
		}
		if(password.length() < 16) {
			String temp = password;
			while (temp.length() < 16) {
				temp+= temp;
			}
			passwordToUse = temp.substring(0, 16);
		}
		return passwordToUse;
	}

	@Override
	public boolean isEncrypted() {
		return (this.token == null && this.encryptedToken != null);
	}

	@Override
	public boolean isDecrypted() {
		return (this.token != null && this.encryptedToken == null);
	}

	@Override
	public UsernamePasswordCredentialsProvider getUsernamePasswordCredentialsProvider() {
		if(this.token == null) {
			return null;
		}
		return new UsernamePasswordCredentialsProvider(this.userName, this.token);
	}

	@Override
	public String getUser() {
		return this.userName;
	}
}
