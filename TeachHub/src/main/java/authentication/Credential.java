package authentication;

import java.io.IOException;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;

public class Credential implements Authentication {
	private String userName;
	private String password;
	
	public Credential(String username, String password) throws InvalidCredentialException {
		this.userName = username;
		this.password = password;
		verify();
	}
	
	private void verify() throws InvalidCredentialException {
		Github github = this.authenticate();
		try {
			github.users().self().login();
		} catch (AssertionError | IOException e) {
			throw new InvalidCredentialException("the credentials provided are not valid");
		}
	}

	public Github authenticate() {
		Github github = new RtGithub(userName, password);
		return github;
	}
	
}
