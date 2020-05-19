package authentication;

import java.io.IOException;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.jcabi.github.Github;

public interface Authentication {
	
	public Github authenticate();
	public UsernamePasswordCredentialsProvider getUsernamePasswordCredentialsProvider();
	//this is really bad style but whatever
	/**
	 * issues a read only invitation. This is done here because the jcabi-github implementation has no way to
	 * send JSON data packets with PUT requests ¯\_(ツ)_/¯
	 * @param org the organization of the repository
	 * @param repoName the repository of the invitation
	 * @param invitee person receiving invite
	 * @return 0 on successful read only invite, 1 on successful invite but with writing permissions, -1 otherwise
	 * @throws IOException if process command was not successfully run, unknown result
	 */
	int issueCurlInviteReadOnly(String org, String repoName, String invitee) throws IOException;
}
