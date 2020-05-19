package authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;

import utilities.OS;

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
	
	public UsernamePasswordCredentialsProvider getUsernamePasswordCredentialsProvider() {
		return new UsernamePasswordCredentialsProvider(this.userName, this.password);
	}

	@Override
	public int issueCurlInviteReadOnly(String org, String repoName, String invitee) throws IOException {
		String url ="https://api.github.com/repos/"+org+"/"+repoName+"/collaborators/"+invitee;
		String json = null;
		if (OS.isWindows()) {
			json = "{\\\"permission\\\":\\\"pull\\\"}";
		} else {
			json = "'{\"permission\":\"pull\"}'";
		}
		String[] commandArgs = {"curl", "-u", this.userName +":"+this.password, "-X", "PUT", "-d", json, url};
		ProcessBuilder process = new ProcessBuilder(commandArgs); 
	    Process p = process.start();
        BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
        }
        String result = builder.toString();
        JsonReader jr = Json.createReader(new StringReader(result));
        JsonObject jobj = jr.readObject();
        if(jobj.getString("permissions").equals("read")) {
        	return 0;
        }
        if(jobj.getString("permissions").equals("write")) {
        	return 1;
        }
        return -1;
	}
	
}
