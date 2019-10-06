package parser;

import java.io.File;
import java.util.ArrayList;

import command.ExecuteCommand;

public class CSVParser {
	private CSVHeaderParse format;;
	
	public CSVParser(){}
	
	public void parseCSVHeader(String header) throws IllegalHeaderException {
		this.format =  new CSVHeaderParse(header);
	}
	
	public ExecuteCommand parseLine(String line) throws IllegalDataException {
		if (this.format == null) {
			throw new IllegalStateException();
		}
		String[] info = line.split(",");
		if (info.length != format.getNumberOfColumns()) {
			throw new IllegalArgumentException ("the number of arguments in this line does not match the number of arguments in the header");
		}
		ExecuteCommand cmd = new ExecuteCommand();
		//user-data cannot be empirically invalid
		int userColumn = format.getUserColumn();
		cmd.setUser(info[userColumn]);
		
		//repo name-data cannot be empirically invalid
		int repoColumn = format.getRepoNameColumn();
		cmd.setRepoName(info[repoColumn]);
		
		//create repo
		int createBooleanColumn = format.getCreateRepoColumn();
		String createRepo = info[createBooleanColumn];
		if (createRepo.toLowerCase().equals("yes")) {
			cmd.setCreateRepo(true);
		} else if (createRepo.toLowerCase().equals("no")) {
			cmd.setCreateRepo(false);
		} else {
			throw new IllegalDataException (createBooleanColumn, info[createBooleanColumn], "the data at this entry does not conform to a boolean type!");
		}
		//add collaborators-data cannot be empirically invalid
		ArrayList<Integer> columnsOfColabsToAdd = format.getAllAddsColabs();
		for (int columnNum : columnsOfColabsToAdd) {
			ArrayList<String> temp = cmd.getAddColabs();
			temp.add(info[columnNum]);
			cmd.setAddColabs(temp);
		}
		//remove collaborators-data cannot be empirically invalid
		ArrayList<Integer> columnsOfColabsToRemove = format.getAllRemovesColabs();
		for (int columnNum : columnsOfColabsToRemove) {
			ArrayList<String> temp = cmd.getRemoveColabs();
			temp.add(info[columnNum]);
			cmd.setRemoveColabs(temp);
		}
		
		//clone repo? -data can be empirically invalid
		int cloneRepoBooleanColumn = format.getCloneRepo();
		String cloneRepo = info[cloneRepoBooleanColumn];
		if (cloneRepo.toLowerCase().equals("yes")) {
			cmd.setCloneRepo(true);
		} else if (cloneRepo.toLowerCase().equals("no")) {
			cmd.setCloneRepo(false);
		} else {
			throw new IllegalDataException (cloneRepoBooleanColumn, info[cloneRepoBooleanColumn], "the data at this entry does not conform to a boolean type!");
		}
		
		//clone location -data can be empirically invalid
		int cloneLocationColumn = format.getRepoCloneLocation();
		String location = info[cloneLocationColumn];
		File file = new File(location);
		if (!file.isDirectory()) {
			throw new IllegalDataException(cloneLocationColumn, info[cloneLocationColumn], "this does not represent a valid path!");
		}
		cmd.setCloneLocation(file);
		
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
		private int cloneRepo = -1;
		private int repoCloneLocation = -1;
		
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
		 * Git_Clone_To_Computer?
		 * Git_Clone_Location
		 */
		CSVHeaderParse(String header) throws IllegalHeaderException{
			String[] tokens = header.split(",");
			this.numberOfColumns = tokens.length;
			for (int x = 0; x < tokens.length; x++) {//for each token
				tokens[x] = tokens[x].trim();
				if (tokens[x].toLowerCase().equals("Git_Clone_To_Computer?".toLowerCase())) {
					if(this.cloneRepo != -1) {
						String msg = "there can only be one column defined with the \"Git_Clone_To_Computer?\" header!";
						throw new IllegalArgumentException(msg);
					}
					this.createRepoColumn = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals("repoCloneLocation".toLowerCase())) {
					if(this.repoCloneLocation != -1) {
						String msg = "there can only be one column defined with the \"repoCloneLocation?\" header!";
						throw new IllegalArgumentException(msg);
					}
					this.repoCloneLocation = x;
					continue;
				}
				
				if (tokens[x].toLowerCase().equals("User".toLowerCase())){
					if (this.userColumn != -1) {
						String msg = "there can only be one column defined with the \"User\" header!";
						throw new IllegalHeaderException(x, tokens[x], msg);
					}
					this.userColumn = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals("Repo_Name".toLowerCase())){
					if (this.RepoNameColumn != -1) {
						String msg = "there can only be one column defined with the \"Repo_Name\" header!";
						throw new IllegalHeaderException(x, tokens[x], msg);
					}
					this.RepoNameColumn = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals("Create_Repo".toLowerCase())){
					if (this.createRepoColumn != -1) {
						String msg = "there can only be one column defined with the \"Create_Repo\" header!";
						throw new IllegalHeaderException(x, tokens[x], msg);
					}
					this.createRepoColumn = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals("Prof_Add_Collab".toLowerCase())){
					this.prof_Add_Columns.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals("Prof_Remove_Collab".toLowerCase())){
					this.prof_Remove_Columns.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals("TA_Add_Collab".toLowerCase())){
					this.TA_Add_Collab.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals("TA_Remove_Collab".toLowerCase())){
					this.TA_Remove_Collab.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals("Student_Add_Collab".toLowerCase())){
					this.Student_Add_Collab.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals("Student_Remove_Collab".toLowerCase())){
					this.Student_Remove_Collab.add(x);
					continue;
				}
				//if none of the if statments are triggered, then there is a header that is not recognized...
				throw new IllegalHeaderException(x, tokens[x], "there is an unrecognized token in the header!");
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
		public int getCloneRepo() {
			return cloneRepo;
		}

		public int getRepoCloneLocation() {
			return repoCloneLocation;
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
