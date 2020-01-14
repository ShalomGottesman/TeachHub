package command;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.Repos.RepoCreate;

import authentication.Credential;
import data_structures.Queue;
import utilities.Strings;

public class CLICommandRunner {
	private Github github;
	private Repos repos;
	private boolean useTestMessege;
	private String username;
	private Scanner sc;
	
	/**
	 * 
	 * @param github already authenticated github object
	 * @param testing true for the repository init message (found in the ReadMe.md file) should be the testing message. false for default
	 * @param userName the username of the current user
	 * @param sc the scanner on which to scan user input (needed to verify if a user wants to delete a repository)
	 */
	public CLICommandRunner(Github github, boolean testing, String userName, Scanner sc) {
		this.github = github;
		this.repos = github.repos();
		this.useTestMessege = testing;
		this.username = userName;
		this.sc = sc;
	}
	
	/**
	 * updates the Repos object this class is holding onto
	 */
	private void updateRepos() {
		this.repos = github.repos();
	}
	
	/**
	 * updates the given repo from the Repos object this class is holding onto, note that the Repos abject should be updated before use unless determined otherwise by calling the updateRepos() 
	 * @param repo the repo to be updated
	 * @return the updated repo
	 */
	private Repo updateRepo(Repo repo) {
		Coordinates coords = repo.coordinates();
		Repo updatedRepo = this.repos.get(coords);
		return updatedRepo;
	}
	
	/**
	 * 
	 * @param queue the queue of commands to be executed. Calls CommandRunner.executeSingle on each one
	 * @throws IOException 
	 */
	public void executeStack(Queue<ExecuteCommand> queue, boolean haveToAuthenticateClone, Credential creds) throws IOException {
		while (queue.size() != 0) {
			ExecuteCommand cmd = queue.deque();
			executeSingle(cmd, haveToAuthenticateClone, creds);
		}
	}
	
	/**
	 * executes a single instance of an ExecuteCommand
	 * @param cmd the command to be executed
	 * @throws IOException when any action taken on a repository is done, this is possible (create, add collaborator, remove collaborator, deleting a repository)
	 */
	public void executeSingle(ExecuteCommand cmd, boolean haveToAuthenticateClone, Credential creds) throws IOException {
		System.out.println();
		this.repos = this.github.repos();
		String initMsg = "";
		if (this.useTestMessege) {
			initMsg = Strings.testRepoinitMsg;
		} else {
			initMsg = Strings.defaultRepoInitMsg;
		}
		
		String repoName = cmd.getRepoName();
		System.out.println("working on repository: " + repoName);
		
		if (cmd.isCreateRepo()) {
			try {
				System.out.println("creating repo");
				boolean makePrivate = cmd.isMakeRepoPrivate();
				RepoCreate createRepo = new RepoCreate(repoName, makePrivate)
						.withDescription(initMsg)
						.withAutoInit(true);
				this.repos.create(createRepo);
			} catch (AssertionError e) {
				System.out.println("WARNING: couldn't generate repository [" + cmd.getRepoName() + "] becuase it already exists, skipping feature");
			}
			updateRepos();
		}
		
		//Coordinates coords = new Coordinates.Simple(this.username, repoName); //this style gives errors when the user owns the repo, but under a different name
		Coordinates coords = new Coordinates.Simple(cmd.getUser(), cmd.getRepoName());//proposed change to solve issue mentioned above. must be tested
		Repo repo = this.repos.get(coords);
		for (String collabToAdd : cmd.getAllAddCollabs()) {
			if (!repo.collaborators().isCollaborator(collabToAdd)) {
				System.out.println("adding: " + collabToAdd + " for: " + repoURLAbstractor(cmd.getUser(), cmd.getRepoName()));
				try {
					repo.collaborators().add(collabToAdd);
				} catch (AssertionError e) {
					continue;
				}
			}
		}
		updateRepos();
		repo = updateRepo(repo);
		for (String collabToRemove : cmd.getAllRemoveCollabs()) {
			if (repo.collaborators().isCollaborator(collabToRemove)) {
				System.out.println("removing: " + collabToRemove);
				try {
					repo.collaborators().remove(collabToRemove);
				} catch (AssertionError e) {
					continue;
				}
			}
		}
		updateRepos();
		repo = updateRepo(repo);
		if (cmd.isDeleteRepo()) {
			System.out.println();
			boolean verify = verifyDelete(this.username, repoName, this.sc);
			if (verify) {
				try {
					repos.remove(coords);
				} catch (AssertionError e) {
					System.out.println("WARNING: TeachHub couldn't delete repository [" + cmd.getRepoName() + "]");
				}
			} else {
				System.out.println("deletion verification returned false, not deleting this repo!");
			}
		}
		
		if (cmd.isCloneRepo()) {
			System.out.println("cloning repo to: " + cmd.getCloneLocation().toString());
			File cloneLocation = new File(cmd.getCloneLocation().toString() + File.separator + cmd.getRepoName());
			String cloneUrl = repoURLAbstractor(cmd.getUser(), cmd.getRepoName());
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
				System.out.println("ERROR: TeachHub couldn't clone repository [" + cmd.getRepoName() + "] to desired location (does the folder already exist?), skipping feature");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * abstracts the cloning URL of repository based on the owner name and the repo name
	 * @param ownerName user name of the owner fo the repository
	 * @param repoName name of the repository
	 * @return The cloning url, in String form
	 */
	private String repoURLAbstractor(String ownerName, String repoName) {
		return "https://github.com/" + ownerName + "/" + repoName + ".git";
	}
	
	
	/**
	 * process to verify from user if a repo should be deleted
	 * @param userName owner of the repo
	 * @param repoName name of the repo
	 * @return boolean if user confirmed to have the repo deleted
	 */
	private boolean verifyDelete(String userName, String repoName, Scanner sc) {
		String completeRepoName = userName + "/" + repoName;
		System.out.println("trying to delete repository [" + completeRepoName + "]. This CANNOT be undone! Are you sure you want to do this? [Yes/No]");
		String str = sc.nextLine();
		if (str.toLowerCase().equals("yes")) {
			System.out.println("then please type the name of the repository (no need to say JohnDoe/ThisRepo, just ThisRepo is fine)");
			System.out.println("or type \"cancel\" to cancel his request");
			String str2 = sc.nextLine();
			if (str2.equals(repoName)) {
				System.out.println("ok then, here we go, deleting " + completeRepoName);
				return true;
			} else if (str2.toLowerCase().equals("cancel")){
				System.out.println("ok, canceling this request");
				return false;
			} else {
				System.out.println("that was not a valid response, please either type in the correct repository name (case specific!) or cancel. Lets try this again");
				return verifyDelete(userName, repoName, sc);
			}
		} else if (str.toLowerCase().equals("no")){
			System.out.println("ok, I will not delete this Repo then");
			return false;
		} else {
			System.out.println("That was not a recognized response, valid responses are: \"Yes\" and \"No\", lets try this again");
			return verifyDelete(userName, repoName, sc);
		}
	}
}
