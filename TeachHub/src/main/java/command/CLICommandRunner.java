package command;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.net.ssl.HttpsURLConnection;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.Repos.RepoCreate;
import com.jcabi.http.Request;
import com.jcabi.http.RequestBody;
import com.jcabi.http.Response;
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
	
	static enum Permissions {PULL, PUSH, ADMIN, MAINTAIN, TRIAGE};
	
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
	
	private void printStats() {
		System.out.println("\n****************");
		System.out.println("MESSEGES:");
		while(this.messegeQueue.size() != 0) {
			String msg = messegeQueue.deque();
			System.out.println(msg);
		}
		System.out.println();
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
	 * @param creds Authentication object with stored credentials/token
	 * @param testing true for the repository init message (found in the ReadMe.md file) should be the testing message. false for default
	 * @param userName the username of the current user
	 * @param sc the scanner on which to scan user input (needed to verify if a user wants to delete a repository)
	 */
	public CLICommandRunner(Authentication creds, boolean testing, String userName, Scanner sc) {
		this.github = creds.authenticate();
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
		String url = "/user/repository_invitations";
		Iterator<JsonValue> iter = genericRequest(url, Request.GET, HttpURLConnection.HTTP_OK, null).readArray().iterator();
		int idToAccept = 0;
		String thisCoord = coords.user() +"/"+ coords.repo();
		while (iter.hasNext()) {
			JsonObject obj = (JsonObject) iter.next();
			String fullRepoName = obj.getJsonObject("repository").getString("full_name");
			if (fullRepoName.equals(thisCoord)) {
				idToAccept = obj.getInt("id");
				break;
			}
		}
		String path = "/user/repository_invitations/" + idToAccept;
		try {
			genericRequest(path, Request.PATCH, HttpURLConnection.HTTP_NO_CONTENT, null);
			succesfulInvitesAccepted++;
			return true;
		} catch (AssertionError e) {
			return false;
		}
	}
	
	private void createRepository(ExecuteCommand cmd) throws IOException {
		//generate basic settings
		RepoCreate createRepoSettings = new RepoCreate(cmd.getRepoName(), cmd.isMakeRepoPrivate())
				.withDescription(initMsg)
				.withAutoInit(true);
		if (!cmd.getUser().equals(this.username)) {//implies creation of an organization repository
			createRepoSettings = createRepoSettings.withOrganization(cmd.getUser());
		}
		String uriPath = "user/repos";
        final String org = createRepoSettings.organization();
        if (org != null && !org.isEmpty()) {
            uriPath = "/orgs/".concat(org).concat("/repos");
        }
        Response response = this.github.entry().uri().path(uriPath)
            .back().method(Request.POST)
            .body().set(createRepoSettings.json()).back()
            .fetch().as(RestResponse.class);
        if (response.status() == HttpURLConnection.HTTP_CREATED) {
        	succesfulCreates++;
        } 
        else if (response.status() == HttpURLConnection.HTTP_FORBIDDEN) {
        	String msg = repoURLAbstractor(cmd.getUser(), cmd.getRepoName()) + " could not be created, unauthorized access. Probable causes: You don't have access to the [" + cmd.getUser() + "] domain in which you are trying to create the repository, or this token does not the repo scope enabled on the GitHub UI";
        	printMessegeAndAddToQue(msg);
        }
        else if (response.status() == 422) {//Unprocessable Entity
        	String msg = repoURLAbstractor(cmd.getUser(), cmd.getRepoName()) + " could not be created because it already exists";
    		printMessegeAndAddToQue(msg);
        }
        else if (response.status() == HttpURLConnection.HTTP_NOT_FOUND) {//Unprocessable Entity
        	String msg = repoURLAbstractor(cmd.getUser(), cmd.getRepoName()) + " could not be created because the [" + cmd.getUser() + "] domain exists";
    		printMessegeAndAddToQue(msg);
        }
        else {        	
        	String msg = repoURLAbstractor(cmd.getUser(), cmd.getRepoName()) + " could not be created. Unknown error code response ["+ response.status()+"]. Please check GitHub developer pages for reason behind response";
			printMessegeAndAddToQue(msg);
		}
		updateRepos();
	}
	
	private void addCollaboratorWithPermission(Coordinates coords, String userToAdd, Permissions permission) throws IOException {
		try {
			JsonObject obj = Json.createObjectBuilder().add("permission", permission.toString().toLowerCase()).build();
			String url = "/repos/"+coords.user()+"/"+coords.repo()+"/collaborators/"+userToAdd;
			JsonObject ret = genericRequest(url, Request.PUT, HttpsURLConnection.HTTP_CREATED, obj).readObject();
			if ((permission.equals(Permissions.PULL)    && ret.containsKey("permissions") && ret.getString("permissions").equals("read")) ||
			   (permission.equals(Permissions.PUSH)     && ret.containsKey("permissions") && ret.getString("permissions").equals("write"))||
			   (permission.equals(Permissions.ADMIN)    && ret.containsKey("permissions") && ret.getString("permissions").equals("admin"))||
			   (permission.equals(Permissions.MAINTAIN) && ret.containsKey("permissions") && ret.getString("permissions").equals("maintain")) ||
			   (permission.equals(Permissions.TRIAGE)   && ret.containsKey("permissions") && ret.getString("permissions").equals("triage"))
			   ) {
					succesfulInvites++;
					return;
			} else {
				String msg = "Invite of " + userToAdd+ " tried with permission " + permission.toString().toLowerCase() +
						"but came back with permission: " + ret.getString("permissions");
				System.out.println(msg);
				messegeQueue.enque(msg);
			}
		} catch (AssertionError e) {
			String msg = "invite of "+ userToAdd+ " failed.";
			printMessegeAndAddToQue(msg);
		}
	}
	
	private void addCollaborators(ExecuteCommand cmd, Coordinates coords, Repo repo) throws IOException {
		for (String collabToAdd : cmd.getAllAddCollabs()) {
			if (isInvitee(repo, collabToAdd)) {
				System.out.println(collabToAdd + " is already invited to " + repoURLAbstractor(cmd.getUser(), cmd.getRepoName()));
				continue;
			}
			if (!repo.collaborators().isCollaborator(collabToAdd)) {
				System.out.print("adding: " + collabToAdd + " for: " + repoURLAbstractor(cmd.getUser(), cmd.getRepoName()));
				if(cmd.isInvitesReadonly()) {
					System.out.println(" as read only");
					addCollaboratorWithPermission(coords, collabToAdd, Permissions.PULL);
				} else {
					System.out.println();
					String path = "/repos/" + coords.user() +"/"+ coords.repo() +"/collaborators/"+ collabToAdd;
					try {
						genericRequest(path, Request.PUT, HttpURLConnection.HTTP_CREATED, null);
						succesfulInvites++;
					} catch(AssertionError e) {
						String msg = "unsecessful add of user ["+collabToAdd+"] to repo["+repoURLAbstractor(cmd.getUser(), cmd.getRepoName())+"]";
						printMessegeAndAddToQue(msg);
					}
				}
			} else {
				String msg = collabToAdd + " is already a collaborator for " + repoURLAbstractor(cmd.getUser(), cmd.getRepoName());
				printMessegeAndAddToQue(msg);
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
					printMessegeAndAddToQue(msg);
					continue;
				}
			} else {
				String msg = collabToRemove + " is not a collaborator for " + repoURLAbstractor(cmd.getUser(), cmd.getRepoName());
				printMessegeAndAddToQue(msg);
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
			messegeQueue.enque(msg);
		}
		printMessegeAndAddToQue(msg);
	}
	
	private void cloneRepo(ExecuteCommand cmd, Authentication creds, boolean haveToAuthenticateClone) {
		Cloning cloner = new Cloning(cmd);
		String cloneUrl = repoURLAbstractor(cmd.getUser(), cmd.getRepoName());
		boolean ret = cloner.clone(haveToAuthenticateClone, creds, cloneUrl);
		if(ret) {
			succesfulClones++;
		} else {
			String msg = "ERROR: TeachHub couldn't clone repository [" + cmd.getRepoName() + "] to desired location (does the folder already exist?), skipping feature";
			printMessegeAndAddToQue(msg);
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
		if(cmd.isAcceptInvite()) {
			totalInvitesToAccept++;
		}
		if (cmd.isCreateRepo()) {
			this.totalCreates++;
		}
		if(cmd.isDeleteRepo()) {
			this.totalDeletes++;
		}
		if(cmd.isCloneRepo()) {
			this.totalClones++;
		}
		this.totalInvites += cmd.getAllAddCollabs().size();
		this.totalRemoves += cmd.getAllRemoveCollabs().size();
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
		System.out.print("TeachHub>");
		String str = sc.nextLine();
		if (str.toLowerCase().equals("yes")) {
			System.out.println("then please type the name of the repository (no need to say JohnDoe/ThisRepo, just ThisRepo is fine)");
			System.out.println("or type \"cancel\" to cancel his request");
			System.out.print("TeachHub>");
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
		String path = "/repos/" + repo.coordinates().user() + "/" + repo.coordinates().repo() + "/invitations";
		try{
			iter = genericRequest(path, Request.GET, HttpURLConnection.HTTP_OK, null).readArray().iterator();
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
	
	private JsonReader genericRequest(String url, String type, int statusAssertion, JsonObject requestObj) throws IOException {
		RequestBody bd = github.entry().uri().path(url).back().method(type).body();
		if(requestObj != null) {
			bd = bd.set(requestObj);
		}
		return bd.back()
				.fetch().as(RestResponse.class)
				.assertStatus(statusAssertion)
				.as(JsonResponse.class).json();
		
	}
	
	private void printMessegeAndAddToQue(String msg) {
		System.out.println(msg);
		messegeQueue.enque(msg);
	}
}
