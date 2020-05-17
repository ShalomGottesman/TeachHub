package command;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.Repos.RepoCreate;
import com.jcabi.http.Request;
import com.jcabi.http.RequestURI;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;

import authentication.Authentication;
import data_structures.Queue;
import githubAction.Cloning;
import utilities.Strings;

public class CLICommandRunner {
	private Github github;
	private Repos repos;
	private boolean useTestMessege;
	private String username;
	private Scanner sc;
	private String initMsg;
	
	Queue<String> messegeQueue = new Queue<String>();
	//stats
	int totalCreates = 0;
	int succesfulCreates = 0;
	int totalDeletes = 0;
	int succesfulDeletes = 0;
	int totalInvites = 0;
	int succesfulInvites = 0;
	int totalRemoves = 0;
	int succesfulRemoves = 0;
	int totalClones = 0;
	int succesfulClones = 0;
	int totalInvitesToAccept = 0;
	int succesfulInvitesAccepted = 0;
	
	HashSet<myCoordinates> creatingRepoCoordSet = new HashSet<myCoordinates>(); 
	HashSet<myCoordinates> ModifyingRepoCoordSet = new HashSet<myCoordinates>(); 
	
	private void printStats() {
		System.out.println("\n****************");
		System.out.println("MESSEGES:");
		while(this.messegeQueue.size() != 0) {
			String msg = messegeQueue.deque();
			System.out.println(msg);
		}
		if (totalCreates != 0) {
			System.out.println("Succesful Creates: " + succesfulCreates +"/"+ totalCreates);
			totalCreates = 0;
			succesfulCreates = 0;
		}
		if (totalDeletes != 0) {
			System.out.println("Succesful Deletes: " + succesfulDeletes +"/"+ totalDeletes);
			totalDeletes = 0;
			succesfulDeletes = 0;
		}
		if (totalInvites != 0) {
			System.out.println("Succesful Invites: " + succesfulInvites +"/"+ totalInvites);
			totalInvites = 0;
			succesfulInvites = 0;
		}
		if (totalRemoves != 0) {
			System.out.println("Succesful Removes: " + succesfulRemoves +"/"+ totalRemoves);
			totalRemoves = 0;
			succesfulRemoves = 0;
		}
		if (totalClones != 0) {
			System.out.println("Succesful Clones: " + succesfulClones +"/"+ totalClones);
			totalClones = 0;
			succesfulClones = 0;
		}
		if (totalInvitesToAccept != 0) {
			System.out.println("Succesful Invites Accepted: " + succesfulInvitesAccepted +"/"+ totalInvitesToAccept);
			totalInvitesToAccept = 0;
			succesfulInvitesAccepted = 0;
		}
		System.out.println("****************\n");
	}
	
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
		if (this.useTestMessege) {
			initMsg = Strings.testRepoinitMsg;
		} else {
			initMsg = Strings.defaultRepoInitMsg;
		}
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
	
	private boolean acceptInvitation(Coordinates coords) throws IOException {
		System.out.println("Accepting invitation");
		Iterator<JsonValue> iter = github.entry().uri().path("/user/repository_invitations").back().method(Request.GET)
				.body().back()
				.fetch().as(RestResponse.class)
		        .assertStatus(HttpURLConnection.HTTP_OK)
		        .as(JsonResponse.class)
		        .json().readArray().iterator();
		int idToAccept = 0;
		String thisCoord = coords.user() +"/"+ coords.repo();
		while (iter.hasNext()) {
			JsonObject obj = (JsonObject) iter.next();
			JsonObject repository = obj.getJsonObject("repository");
			String fullRepoName = repository.getString("full_name");
			if (fullRepoName.equals(thisCoord)) {
				idToAccept = obj.getInt("id");
				break;
			}
		}
		RestResponse resp = github.entry().uri().path("/user/repository_invitations/" + idToAccept).back().method(Request.PATCH)
				.body().back()
				.fetch().as(RestResponse.class);
		if (resp.status() == HttpURLConnection.HTTP_NO_CONTENT) {
			succesfulInvitesAccepted++;
			return true;
		}
		return false;
	}
	
	private void createRepository(ExecuteCommand cmd) throws IOException {
		try {
			System.out.println("creating repo");
			boolean makePrivate = cmd.isMakeRepoPrivate();
			String apiUriPath = "";
			//derive where the repo goes in this users account, either in the organization or the regular repos
			if (!cmd.getUser().equals(this.username)) {
				apiUriPath = "/orgs/"+ cmd.getUser() + "/repos";
			} else {
				apiUriPath = "user/repos";
			}
			//generate basic settings
			RepoCreate createRepoSettings = new RepoCreate(cmd.getRepoName(), makePrivate)
					.withDescription(initMsg)
					.withAutoInit(true);
			
			//use settings to generate repo under specified path
			this.github.entry().uri().path(apiUriPath)
			.back().method(Request.POST)
	        .body().set(createRepoSettings.json()).back()
	        .fetch().as(RestResponse.class)
	        .assertStatus(HttpURLConnection.HTTP_CREATED);
			succesfulCreates++;

		} catch (AssertionError e) {
			String msg = "WARNING: couldn't generate repository [" + repoURLAbstractor(cmd.getUser(), cmd.getRepoName()) + "] becuase it already exists, skipping feature";
			System.out.println(msg);
			messegeQueue.enque(msg);
		}
		updateRepos();
	}
	
	private void addCollaborators(ExecuteCommand cmd, Coordinates coords, Repo repo) throws IOException {
		for (String collabToAdd : cmd.getAllAddCollabs()) {
			if (isInvitee(repo, collabToAdd)) {
				System.out.println(collabToAdd + " is already invited to " + repoURLAbstractor(cmd.getUser(), cmd.getRepoName()));
				continue;
			}
			if (!repo.collaborators().isCollaborator(collabToAdd)) {
				try {
					System.out.print("adding: " + collabToAdd + " for: " + repoURLAbstractor(cmd.getUser(), cmd.getRepoName()));
					String path = "/repos/" + coords.user() +"/"+ coords.repo() +"/collaborators/"+ collabToAdd;
					RequestURI req = github.entry().uri();
					if(cmd.isInvitesReadonly()) {
						System.out.println(" as read only");
						req = req.queryParam("permission", "read");
					} else {
						System.out.println();
					}
					req.path(path).back().method(Request.PUT)
					.body().back()
					.fetch().as(RestResponse.class)
					.assertStatus(HttpURLConnection.HTTP_CREATED);
					succesfulInvites++;
				} catch (AssertionError e) {
					String msg = "unsecessful add of user ["+collabToAdd+"] to repo["+repoURLAbstractor(cmd.getUser(), cmd.getRepoName())+"]";
					System.out.println(msg);
					messegeQueue.enque(msg);
					continue;
				}
			} else {
				String msg = collabToAdd + " is already a collaborator for " + repoURLAbstractor(cmd.getUser(), cmd.getRepoName());
				System.out.println(msg);
				messegeQueue.enque(msg);
			}
		}
	}
	
	private void removeCollaborators(ExecuteCommand cmd, Repo repo) throws IOException {
		for (String collabToRemove : cmd.getAllRemoveCollabs()) {
			if (repo.collaborators().isCollaborator(collabToRemove)) {
				System.out.println("removing: " + collabToRemove);
				try {
					repo.collaborators().remove(collabToRemove);
					succesfulRemoves++;
				} catch (AssertionError e) {
					String msg = "unsecessful remove of user ["+collabToRemove+"] to repo["+repoURLAbstractor(cmd.getUser(), cmd.getRepoName())+"]";
					messegeQueue.enque(msg);
					continue;
				}
			} else {
				String msg = collabToRemove + " is not a collaborator for " + repoURLAbstractor(cmd.getUser(), cmd.getRepoName());
				System.out.println(msg);
				messegeQueue.enque(msg);
			}
		}
	}
	
	private void deleteRepo(ExecuteCommand cmd) throws IOException {
		boolean ret = deleteProcess(cmd);
		String msg = "";
		if (ret) {
			succesfulDeletes++;
			msg = "Deleted repository [" + repoURLAbstractor(cmd.getUser(), cmd.getRepoName()) +"]";
		} else {
			msg = "Did not delete repository [" + repoURLAbstractor(cmd.getUser(), cmd.getRepoName()) +"]";
		}
		System.out.println(msg);
		messegeQueue.enque(msg);
	}
	
	private void cloneRepo(ExecuteCommand cmd, Authentication creds, boolean haveToAuthenticateClone) {
		Cloning cloner = new Cloning(cmd);
		String cloneUrl = repoURLAbstractor(cmd.getUser(), cmd.getRepoName());
		boolean ret = cloner.clone(haveToAuthenticateClone, creds, cloneUrl);
		if(ret) {
			succesfulClones++;
		} else {
			String msg = "ERROR: TeachHub couldn't clone repository [" + cmd.getRepoName() + "] to desired location (does the folder already exist?), skipping feature";
			System.out.println(msg);
			messegeQueue.enque(msg);
		}
	}
	
	/**
	 * 
	 * @param queue the queue of commands to be executed. Calls CommandRunner.executeSingle on each one
	 * @throws IOException 
	 */
	public void executeStack(Queue<ExecuteCommand> queue, boolean haveToAuthenticateClone, Authentication creds) throws IOException {
		Queue<ExecuteCommand> temp = new Queue<ExecuteCommand>();
		while (queue.size() != 0) {
			ExecuteCommand cmd = queue.deque();
			updateStats(cmd);
			temp.enque(cmd);
		}
		queue = temp;
		
		while (queue.size() != 0) {
			ExecuteCommand cmd = queue.deque();
			executeSinglePrivate(cmd, haveToAuthenticateClone, creds);
		}
		printStats();
	}
	
	private void updateStats(ExecuteCommand cmd) {
		myCoordinates thisCord = new myCoordinates(cmd.getUser(), cmd.getRepoName());
		boolean isCreate = false;
		boolean isAnythingElse = false;
		if(cmd.isAcceptInvite()) {
			totalInvitesToAccept++;
		}
		if (cmd.isCreateRepo()) {
			this.totalCreates++;
			this.creatingRepoCoordSet.add(thisCord);
			isCreate = true;
		}
		if(cmd.isDeleteRepo()) {
			isAnythingElse = true;
			this.totalDeletes++;
		}
		if(cmd.isCloneRepo()) {
			isAnythingElse = true;
			this.totalClones++;
		}
		this.totalInvites += cmd.getAllAddCollabs().size();
		this.totalRemoves += cmd.getAllRemoveCollabs().size();
		if (cmd.getAllAddCollabs().size() != 0 || cmd.getAllRemoveCollabs().size() != 0) {
			isAnythingElse = true;
		}
		if (isAnythingElse && !isCreate) {
			this.ModifyingRepoCoordSet.add(thisCord);
		}
		
	}
	
	public void executeSingle(ExecuteCommand cmd, boolean haveToAuthenticateClone, Authentication creds) throws IOException {
		updateStats(cmd);
		executeSinglePrivate(cmd, haveToAuthenticateClone, creds);
		printStats();
	}
	
	/**
	 * executes a single instance of an ExecuteCommand
	 * @param cmd the command to be executed
	 * @throws IOException when any action taken on a repository is done, this is possible (create, add collaborator, remove collaborator, deleting a repository)
	 */
	private void executeSinglePrivate(ExecuteCommand cmd, boolean haveToAuthenticateClone, Authentication creds) throws IOException {
		System.out.println();
		this.repos = this.github.repos();
		System.out.println("working on repository: " + cmd.getRepoName());
		
		Coordinates coords = new Coordinates.Simple(cmd.getUser(), cmd.getRepoName());
		if(cmd.isAcceptInvite()) {
			acceptInvitation(coords);
		}
		
		if (cmd.isCreateRepo()) {
			createRepository(cmd);
		}
		Repo repo = this.repos.get(coords);
		addCollaborators(cmd, coords, repo);
		updateRepos();
		repo = updateRepo(repo);
		removeCollaborators(cmd, repo);
		updateRepos();
		repo = updateRepo(repo);
		if (cmd.isDeleteRepo()) {
			deleteRepo(cmd);
		}
		
		if (cmd.isCloneRepo()) {
			cloneRepo(cmd, creds, haveToAuthenticateClone);
		}
	}
	
	private boolean deleteProcess(ExecuteCommand cmd) throws IOException {
		Coordinates coords = new Coordinates.Simple(cmd.getUser(), cmd.getRepoName());
		System.out.println();
		boolean verify = verifyDelete(cmd.getUser(), cmd.getRepoName(), this.sc);
		if (verify) {
			try {
				repos.remove(coords);
				return true;
			} catch (AssertionError e) {
				System.out.println("WARNING: TeachHub couldn't delete repository [" + cmd.getRepoName() + "]");
				return false;
			}
		} else {
			System.out.println("deletion verification returned false, not deleting this repo!");
			return false;
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
	
	private boolean isInvitee(Repo repo, String user) throws IOException {
		Iterator<JsonValue> iter;
		try{
			iter = repo.github().entry().uri()
				.path("/repos")
				.path(repo.coordinates().user())
				.path(repo.coordinates().repo())
				.path("/invitations")
				.back().method(Request.GET)
				.body().set(Json.createArrayBuilder().build()).back()
				.fetch().as(RestResponse.class)
		        .assertStatus(HttpURLConnection.HTTP_OK)
		        .as(JsonResponse.class)
		        .json().readArray().iterator();
		} catch (AssertionError e) {
			System.out.println("Could not contact github to see if user is already invited to collaborate, assuming he is not a collaborator");
			return false;
		}
				
		while (iter.hasNext()) {
			JsonObject val = (JsonObject) iter.next();
			if (val.getJsonObject("invitee").getString("login").equals(user)) {
				return true;
			}
		}
		return false;
	}
	
	private class myCoordinates{
		String username;
		String reponame;
		
		myCoordinates(String user, String repo){
			username = user;
			reponame = repo;
		}
		
		@Override
		public boolean equals(Object that) {
			if (that == null) {
				return false;
			}
			if (that.getClass() != this.getClass()) {
				return false;
			}
			that = (myCoordinates) that;
			if (((myCoordinates) that).reponame.equals(this.reponame) && ((myCoordinates) that).username.equals(this.username)) {
				return true;
			} else {
				return false;
			}
		}
		
		@SuppressWarnings("unused")
		public int hashcode() {
			return this.username.hashCode() * this.reponame.hashCode();
		}
	}
}
