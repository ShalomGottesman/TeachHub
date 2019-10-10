package command;

import java.io.File;
import java.util.ArrayList;

public class ExecuteCommand {
	private String user;
	private boolean createRepo;
	private String repoDescription;
	private String repoName;
	private ArrayList<String> addColabs = new ArrayList<String>();
	private ArrayList<String> removeColabs = new ArrayList<String>();
	private boolean cloneRepo;
	private File cloneLocation;
	private boolean makeRepoPrivate;
	private boolean deleteRepo;
	
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

	public ArrayList<String> getAddColabs() {
		return addColabs;
	}

	public void setAddColabs(ArrayList<String> addColabs) {
		this.addColabs = addColabs;
	}

	public ArrayList<String> getRemoveColabs() {
		return removeColabs;
	}

	public void setRemoveColabs(ArrayList<String> removeColabs) {
		this.removeColabs = removeColabs;
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
