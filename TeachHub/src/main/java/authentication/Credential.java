package authentication;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;

public class Credential implements Authentication {
	private String userName;
	private String password;
	
	public Credential(String username, String password) {
		this.userName = username;
		this.password = password;
	}

	public Github authenticate() {
		Github github = new RtGithub(userName, password);
		return github;
	}
	
}
