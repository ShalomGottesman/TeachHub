package command;

import java.util.ArrayList;

public interface Command {
	
	public ArrayList<String> getAllAddCollabs();
	
	public ArrayList<String> getAllRemoveCollabs();
	
	public String getCommandInfo();
	
	public String getUser();
	
	public boolean isMakeRepoPrivate();
	
	public void setMakeRepoPrivate(boolean makeRepoPrivate);
	
	public boolean isDeleteRepo();
	
	public void setDeleteRepo(boolean deleteRepo);
	
	public void setUser(String user);
	
	public boolean isCreateRepo();
	
	public void setCreateRepo(boolean createRepo);
	
	public String getRepoDescription();
	
	public void setRepoDescription(String repoDescription);
	
	public String getRepoName();
	
	public void setRepoName(String repoName);
	
	public ArrayList<String> getAddCollabs_Profs();
	
	public void setAddCollabs_Profs(ArrayList<String> addCollabs_Profs);

	public ArrayList<String> getAddCollabs_TAs();

	public void setAddCollabs_TAs(ArrayList<String> addCollabs_TAs);

	public ArrayList<String> getAddCollabs_Studs();

	public void setAddCollabs_Studs(ArrayList<String> addCollabs_Studs);

	public ArrayList<String> getRemoveCollabs_Profs();

	public void setRemoveCollabs_Profs(ArrayList<String> removeCollabs_Profs);

	public ArrayList<String> getRemoveCollabs_TAs();

	public void setRemoveCollabs_TAs(ArrayList<String> removeCollabs_TAs);

	public ArrayList<String> getRemoveCollabs_Studs();

	public void setRemoveCollabs_Studs(ArrayList<String> removeCollabs_Studs);
}
