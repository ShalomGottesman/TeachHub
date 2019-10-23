package command;

import java.util.ArrayList;
import java.util.Arrays;

public class UndoCommand implements Command{
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
	private boolean makeRepoPrivate;
	private boolean deleteRepo;
	
	/**
	 * The constructor of the command is to create a near mirror image of the command passed in,
	 * Note that this is not a perfect image. The inverse will not touch if the repository was cloned
	 * onto the local machine, but it will delete/create a repository, add/remove collaborators.
	 * One other point, if a delete call is made on a repository, and in that command the tag to in theory 
	 * make a repository private when created was not noted, then in the undo of that command (the recreation of the repository)
	 * the repo will be made public.
	 * @param exeCmd the command to create a near mirror image of.
	 */
	public UndoCommand(ExecuteCommand exeCmd){
		this.user = exeCmd.getUser();
		this.createRepo = !exeCmd.isCreateRepo();
		this.repoDescription = exeCmd.getRepoDescription();
		this.repoName = exeCmd.getRepoName();
		
		this.addCollabs_Profs = exeCmd.getRemoveCollabs_Profs();
		this.addCollabs_TAs = exeCmd.getRemoveCollabs_TAs();
		this.addCollabs_Studs = exeCmd.getRemoveCollabs_Studs();
		
		this.removeCollabs_Profs = exeCmd.getAddCollabs_Profs();
		this.removeCollabs_TAs = exeCmd.getAddCollabs_TAs();
		this.removeCollabs_Studs = exeCmd.getAddCollabs_Studs();
		
		this.makeRepoPrivate = exeCmd.isMakeRepoPrivate();
		this.deleteRepo = !exeCmd.isDeleteRepo();
	}
	
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
		String user = String.format("user: %20s", this.user);
		String name = String.format("repo name: %20s", this.repoName);
		String instantiation = "init repo: ";
		if (createRepo) {
			instantiation = instantiation + "yes";
		} else {
			instantiation = instantiation + "no";
		}
		String addCollabs = Arrays.toString(this.getAllAddCollabs().toArray(new String[1]));
		String removeCollabs = Arrays.toString(this.getAllRemoveCollabs().toArray(new String[1]));
		addCollabs = "adding: " + addCollabs;
		removeCollabs = "removing: " + removeCollabs;
		String makePrivate = String.format("make private: %5s", this.makeRepoPrivate);
		
		String concatinated = user + " | " + name + " | " + instantiation + " | " + addCollabs + " | " + removeCollabs+ " | " + makePrivate + " | ";
		
		if (deleteRepo) {
			return user + " | " + name + " | " + "DELETE REPO";
		} else {
			return concatinated;
		}
		
	}

	public String getUser() {
		return user;
	}

	public boolean isMakeRepoPrivate() {
		return makeRepoPrivate;
	}

	public void setMakeRepoPrivate(boolean makeRepoPrivate) {
		this.makeRepoPrivate = makeRepoPrivate;
	}

	public boolean isDeleteRepo() {
		return deleteRepo;
	}

	public void setDeleteRepo(boolean deleteRepo) {
		this.deleteRepo = deleteRepo;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isCreateRepo() {
		return createRepo;
	}

	public void setCreateRepo(boolean createRepo) {
		this.createRepo = createRepo;
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
}
