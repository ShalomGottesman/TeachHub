package parser;

import java.util.ArrayList;

import command.ExecuteCommand;

public class CSVParser {
	private CSVHeaderParse format;;
	
	public CSVParser(){}
	
	public void parseCSVHeader(String header) {
		this.format =  new CSVHeaderParse(header);
	}
	
	public ExecuteCommand parseLine(String line) {
		if (this.format == null) {
			throw new IllegalStateException();
		}
		String[] info = line.split(",");
		if (info.length != format.getNumberOfColumns()) {
			throw new IllegalArgumentException ("the number of arguments in this line does not match the number of arguments in the header");
		}
		ExecuteCommand cmd = new ExecuteCommand();
		//user
		int userColumn = format.getUserColumn();
		cmd.setUser(info[userColumn]);
		//repo name
		int repoColumn = format.getRepoNameColumn();
		cmd.setRepoName(info[repoColumn]);
		//create repo
		int createBooleanColumn = format.getCreateRepoColumn();
		String boo = info[createBooleanColumn];
		if (boo.toLowerCase().equals("yes")) {
			cmd.setCreateRepo(true);
		} else if (boo.toLowerCase().equals("no")) {
			cmd.setCreateRepo(false);
		} else {
			throw new IllegalArgumentException ("the argument in column [" + createBooleanColumn + "] does not conform to a boolean data type");
		}
		//add collaborators 
		ArrayList<Integer> columnsOfColabsToAdd = format.getAllAddsColabs();
		for (int columnNum : columnsOfColabsToAdd) {
			ArrayList<String> temp = cmd.getAddColabs();
			temp.add(info[columnNum]);
			cmd.setAddColabs(temp);
		}
		//remove collaborators
		ArrayList<Integer> columnsOfColabsToRemove = format.getAllRemovesColabs();
		for (int columnNum : columnsOfColabsToRemove) {
			ArrayList<String> temp = cmd.getRemoveColabs();
			temp.add(info[columnNum]);
			cmd.setRemoveColabs(temp);
		}		
		return cmd;
	}
	
	
	private class CSVHeaderParse{	
		private int userColumn = -1;
		private int RepoNameColumn = -1;
		private int createRepoColumn = -1;
		private ArrayList<Integer> prof_Add_Columns = new ArrayList<Integer>();
		private ArrayList<Integer> prof_Remove_Columns = new ArrayList<Integer>();
		private ArrayList<Integer> TA_Add_Collab = new ArrayList<Integer>();
		private ArrayList<Integer> TA_Remove_Collab = new ArrayList<Integer>();
		private ArrayList<Integer> Student_Add_Collab = new ArrayList<Integer>();
		private ArrayList<Integer> Student_Remove_Collab = new ArrayList<Integer>();
		private ArrayList<Integer> allAddColabs = new ArrayList<Integer>();
		private ArrayList<Integer> allRemoveColabs = new ArrayList<Integer>();
		private int numberOfColumns = -1;
		
		/*
		 * 
		 * all column headers must conform to one of the following (Case Insensitive), not all headers must be used, 
		 * User
		 * Repo_Name
		 * Create_Repo
		 * Prof_Add_Collab
		 * Prof_Remove_Collab
		 * TA_Add_Collab
		 * TA_Remove_Collab
		 * Student_Add_Collab
		 * Student_Remove_Collab
		 */
		CSVHeaderParse(String header){
			String[] tokens = header.split(",");
			this.numberOfColumns = tokens.length;
			for (int x = 0; x < tokens.length; x++) {//for each token
				tokens[x] = tokens[x].trim();
				if (tokens[x].toLowerCase().equals("User".toLowerCase())){
					if (this.userColumn != -1) {
						String msg = "there can only be one column defined with the \"User\" header!";
						throw new IllegalArgumentException(msg);
					}
					this.userColumn = x;
				}
				if (tokens[x].toLowerCase().equals("Repo_Name".toLowerCase())){
					if (this.RepoNameColumn != -1) {
						String msg = "there can only be one column defined with the \"Repo_Name\" header!";
						throw new IllegalArgumentException(msg);
					}
					this.RepoNameColumn = x;
				}
				if (tokens[x].toLowerCase().equals("Create_Repo".toLowerCase())){
					if (this.createRepoColumn != -1) {
						String msg = "there can only be one column defined with the \"Create_Repo\" header!";
						throw new IllegalArgumentException(msg);
					}
					this.createRepoColumn = x;
				}
				if (tokens[x].toLowerCase().equals("Prof_Add_Collab".toLowerCase())){
					this.prof_Add_Columns.add(x);
				}
				if (tokens[x].toLowerCase().equals("Prof_Remove_Collab".toLowerCase())){
					this.prof_Remove_Columns.add(x);
				}
				if (tokens[x].toLowerCase().equals("TA_Add_Collab".toLowerCase())){
					this.TA_Add_Collab.add(x);
				}
				if (tokens[x].toLowerCase().equals("TA_Remove_Collab".toLowerCase())){
					this.TA_Remove_Collab.add(x);
				}
				if (tokens[x].toLowerCase().equals("Student_Add_Collab".toLowerCase())){
					this.Student_Add_Collab.add(x);
				}
				if (tokens[x].toLowerCase().equals("Student_Remove_Collab".toLowerCase())){
					this.Student_Remove_Collab.add(x);
				}
			}
			allAddColabs.addAll(this.prof_Add_Columns);
			allAddColabs.addAll(this.TA_Add_Collab);
			allAddColabs.addAll(this.Student_Add_Collab);
			
			allRemoveColabs.addAll(this.prof_Remove_Columns);
			allRemoveColabs.addAll(this.TA_Remove_Collab);
			allRemoveColabs.addAll(this.Student_Remove_Collab);
			
		}
		
		public ArrayList<Integer> getAllAddsColabs(){
			return this.allAddColabs;
		}
		
		public ArrayList<Integer> getAllRemovesColabs(){
			return this.allRemoveColabs;
		}
		
		public int getNumberOfColumns() {
			return this.numberOfColumns;
		}

		public int getUserColumn() {
			return userColumn;
		}
		public int getRepoNameColumn() {
			return RepoNameColumn;
		}
		public int getCreateRepoColumn() {
			return createRepoColumn;
		}
		@SuppressWarnings("unused")
		public ArrayList<Integer> getProfAddColumns() {
			return prof_Add_Columns;
		}
		@SuppressWarnings("unused")
		public ArrayList<Integer> getProfRemoveColumns() {
			return prof_Remove_Columns;
		}
		@SuppressWarnings("unused")
		public ArrayList<Integer> getTA_Add_Collab() {
			return TA_Add_Collab;
		}
		@SuppressWarnings("unused")
		public ArrayList<Integer> getTA_Remove_Collab() {
			return TA_Remove_Collab;
		}
		@SuppressWarnings("unused")
		public ArrayList<Integer> getStudent_Add_Collab() {
			return Student_Add_Collab;
		}
		@SuppressWarnings("unused")
		public ArrayList<Integer> getStudent_Remove_Collab() {
			return Student_Remove_Collab;
		}
	}

}
