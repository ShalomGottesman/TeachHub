package command;

import java.util.ArrayList;

public class ExecuteCommand {
	private String user;
	private boolean createRepo;
	private String repoDescription;
	private String repoName;
	private ArrayList<String> addColabs = new ArrayList<String>();
	private ArrayList<String> removeColabs = new ArrayList<String>();

	public ExecuteCommand(){}

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
	
}
