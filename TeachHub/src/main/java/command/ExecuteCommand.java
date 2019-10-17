package command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ExecuteCommand {
	private String user;
	private boolean createRepo;
	private String repoDescription;
	private String repoName;
	private ArrayList<String> addCollabs = new ArrayList<String>();
	private ArrayList<String> removeCollabs = new ArrayList<String>();
	private boolean cloneRepo;
	private File cloneLocation;
	private boolean makeRepoPrivate;
	private boolean deleteRepo;
	
	public String getCommandInfo() {
		String user = String.format("user: %20s", this.user);
		String name = String.format("repo name: %20s", this.repoName);
		String instantiation = "init repo: ";
		if (createRepo) {
			instantiation = instantiation + "yes";
		} else {
			instantiation = instantiation + "no";
		}
		String addCollabs = Arrays.toString(this.addCollabs.toArray(new String[1]));
		String removeCollabs = Arrays.toString(this.removeCollabs.toArray(new String[1]));
		addCollabs = "adding: " + addCollabs;
		removeCollabs = "removing: " + removeCollabs;
		String makePrivate = String.format("make private: %5s", this.makeRepoPrivate);
		
		String concatinated = user + " | " + name + " | " + instantiation + " | " + addCollabs + " | " + removeCollabs+ " | " + makePrivate + " | ";
		
		String clone = "clone: ";
		if (cloneRepo) {
			clone = clone + "yes, location: " + cloneLocation.toString();
		} else {
			clone = clone + "no";
		}
		concatinated = concatinated + clone;
		if (deleteRepo) {
			return user + " | " + name + " | " + "DELETE REPO";
		} else {
			return concatinated;
		}
		
	}
	
	public ExecuteCommand(){}
	
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

	public ArrayList<String> getAddCollabs() {
		if (this.addCollabs == null) {
			this.addCollabs = new ArrayList<String>();
		}
		return addCollabs;
	}

	public void setAddColabs(ArrayList<String> addColabs) {
		this.addCollabs = addColabs;
	}

	public ArrayList<String> getRemoveCollabs() {
		if (this.removeCollabs == null) {
			this.removeCollabs = new ArrayList<String>();
		}
		return removeCollabs;
	}

	public void setRemoveColabs(ArrayList<String> removeColabs) {
		this.removeCollabs = removeColabs;
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
	
}
