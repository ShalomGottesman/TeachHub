package command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ExecuteCommand implements Command{
	private String user;
	private boolean createRepo;
	private String repoDescription;
	private String repoName;
	private ArrayList<String> addCollabs_Profs = new ArrayList<String>();
	private ArrayList<String> addCollabs_TAs = new ArrayList<String>();
	private ArrayList<String> addCollabs_Studs = new ArrayList<String>();
	private ArrayList<String> removeCollabs_Profs = new ArrayList<String>();
	private ArrayList<String> removeCollabs_TAs = new ArrayList<String>();
	private ArrayList<String> removeCollabs_Studs = new ArrayList<String>();
	private boolean cloneRepo;
	private File cloneLocation;
	private boolean makeRepoPrivate;
	private boolean deleteRepo;
	private boolean invitesReadonly;
	private boolean acceptInvite;
	
	public ExecuteCommand(){}
	
	public ArrayList<String> getAllAddCollabs(){
		ArrayList<String> addAll = new ArrayList<String>();
		addAll.addAll(this.addCollabs_Profs);
		addAll.addAll(this.addCollabs_TAs);
		addAll.addAll(this.addCollabs_Studs);
		return addAll;
	}
	
	public ArrayList<String> getAllRemoveCollabs(){
		ArrayList<String> RemoveAll = new ArrayList<String>();
		RemoveAll.addAll(this.removeCollabs_Profs);
		RemoveAll.addAll(this.removeCollabs_TAs);
		RemoveAll.addAll(this.removeCollabs_Studs);
		return RemoveAll;
	}
	
	public String getCommandInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("owner: %15s | ", this.user));
		builder.append(String.format("repo name: %15s | ", this.repoName));
		if (createRepo) {
			builder.append("Create Repo | ");
		}
		if (this.isAcceptInvite()) {
			builder.append("Accept Invite | ");
		}
		builder.append("Adding: " + Arrays.toString(this.getAllAddCollabs().toArray(new String[1])));
		if (this.isInvitesReadonly()) {
			builder.append(" as READ ONLY ");
		}
		builder.append(" | ");
		builder.append("Removing: " + Arrays.toString(this.getAllRemoveCollabs().toArray(new String[1])) + " | ");
		if (this.isMakeRepoPrivate()) {
			builder.append("Make private | ");
		}
		if (this.isCloneRepo()) {
			builder.append("clone repo to " + cloneLocation.toString() + " | ");
		}
		if (deleteRepo) {
			builder.append(" DELETE REPO | ");
		} 
		return builder.toString();
		
	}
	
	public boolean isDeleteRepo() {
		return deleteRepo;
	}

	public void setDeleteRepo(boolean deleteRepo) {
		this.deleteRepo = deleteRepo;
	}

	public boolean isMakeRepoPrivate() {
		return makeRepoPrivate;
	}

	public void setMakeRepoPrivate(boolean makeRepoPrivate) {
		this.makeRepoPrivate = makeRepoPrivate;
	}	

	public boolean isCreateRepo() {
		return createRepo;
	}

	public void setCreateRepo(boolean createRepo) {
		this.createRepo = createRepo;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRepoDescription() {
		return repoDescription;
	}

	public void setRepoDescription(String repoDescription) {
		this.repoDescription = repoDescription;
	}

	public String getRepoName() {
		return repoName;
	}

	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}


	
	public ArrayList<String> getAddCollabs_Profs() {
		return addCollabs_Profs;
	}

	public void setAddCollabs_Profs(ArrayList<String> addCollabs_Profs) {
		this.addCollabs_Profs = addCollabs_Profs;
	}

	public ArrayList<String> getAddCollabs_TAs() {
		return addCollabs_TAs;
	}

	public void setAddCollabs_TAs(ArrayList<String> addCollabs_TAs) {
		this.addCollabs_TAs = addCollabs_TAs;
	}

	public ArrayList<String> getAddCollabs_Studs() {
		return addCollabs_Studs;
	}

	public void setAddCollabs_Studs(ArrayList<String> addCollabs_Studs) {
		this.addCollabs_Studs = addCollabs_Studs;
	}

	public ArrayList<String> getRemoveCollabs_Profs() {
		return removeCollabs_Profs;
	}

	public void setRemoveCollabs_Profs(ArrayList<String> removeCollabs_Profs) {
		this.removeCollabs_Profs = removeCollabs_Profs;
	}

	public ArrayList<String> getRemoveCollabs_TAs() {
		return removeCollabs_TAs;
	}

	public void setRemoveCollabs_TAs(ArrayList<String> removeCollabs_TAs) {
		this.removeCollabs_TAs = removeCollabs_TAs;
	}

	public ArrayList<String> getRemoveCollabs_Studs() {
		return removeCollabs_Studs;
	}

	public void setRemoveCollabs_Studs(ArrayList<String> removeCollabs_Studs) {
		this.removeCollabs_Studs = removeCollabs_Studs;
	}

	public boolean isCloneRepo() {
		return cloneRepo;
	}

	public void setCloneRepo(boolean cloneRepo) {
		this.cloneRepo = cloneRepo;
	}

	public File getCloneLocation() {
		return cloneLocation;
	}

	public void setCloneLocation(File cloneLocation) {
		this.cloneLocation = cloneLocation;
	}

	public boolean isInvitesReadonly() {
		return invitesReadonly;
	}

	public void setInvitesReadOnly(boolean bool) {
		this.invitesReadonly = bool;
	}
	
	public boolean isAcceptInvite() {
		return acceptInvite;
	}
	
	public void setAcceptInvite(boolean bool) {
		this.acceptInvite = bool;
	}
	
}
