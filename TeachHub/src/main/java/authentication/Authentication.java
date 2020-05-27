package authentication;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.jcabi.github.Github;

public interface Authentication {
	
	public Github authenticate();
	public UsernamePasswordCredentialsProvider getUsernamePasswordCredentialsProvider();
	
}
