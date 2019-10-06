package authentication;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;

public class OAuthTokenAuthentication implements Authentication {
	private String authToken;
	
	public OAuthTokenAuthentication(String token) {
		this.authToken = token;
	}
	
	public Github authenticate() {
		Github github = new RtGithub(this.authToken);
		return github;
	}

}
