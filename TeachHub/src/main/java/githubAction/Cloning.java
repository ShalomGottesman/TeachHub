package githubAction;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import authentication.Credential;
import command.ExecuteCommand;

public class Cloning {
	ExecuteCommand cmd;
	public Cloning(ExecuteCommand cmd){
		this.cmd = cmd;
	}
	
	public boolean clone(boolean haveToAuthenticateClone, Credential creds, String cloneUrl) {
		System.out.println("cloning repo to: " + cmd.getCloneLocation().toString());
		File cloneLocation = new File(cmd.getCloneLocation().toString() + File.separator + cmd.getRepoName());
		//String cloneUrl = repoURLAbstractor(cmd.getUser(), cmd.getRepoName());
		try {
			CloneCommand cloneCommand = Git.cloneRepository().setURI(cloneUrl).setDirectory(cloneLocation);
			if(haveToAuthenticateClone) {
				UsernamePasswordCredentialsProvider userpass = creds.getUsernamePasswordCredentialsProvider();
				if (userpass == null) {
					System.out.println("userpass is null");
				}
				cloneCommand.setCredentialsProvider(userpass);
			}
			cloneCommand.call();
		} catch (GitAPIException | JGitInternalException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
