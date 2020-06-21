package parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import command.CLICommandRunner.Permissions;
import command.ExecuteCommand;

public class CSVParser {
	private CSVHeaderParse format;;
	
	public CSVParser(){}
	
	public void parseCSVHeader(String header) throws IllegalHeaderException {
		this.format =  new CSVHeaderParse(header);
	}
	
	public ExecuteCommand parseLine(String line) throws IllegalDataException {
		if (this.format == null) {
			throw new IllegalStateException("no header was passed in to format the input!");
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
		int createRepoColumn = format.getCreateRepoColumn();
		if (createRepoColumn != -1) {
			String createRepo = info[createRepoColumn];
			if (createRepo.toLowerCase().equals("yes")) {
				cmd.setCreateRepo(true);
			} else if (createRepo.toLowerCase().equals("no")) {
				cmd.setCreateRepo(false);
			} else {
				throw new IllegalDataException (createRepoColumn, info[createRepoColumn], "the data at this entry does not conform to a boolean type!");
			}
		}
		
		//make private-can be imperially invalid
		int makePrivateColumn = format.getMakeRepoPrivate();
		if(makePrivateColumn != -1) {
			String makePrivate = info[makePrivateColumn];
			if (makePrivate.toLowerCase().equals("yes")) {
				cmd.setMakeRepoPrivate(true);
			} else if (makePrivate.toLowerCase().equals("no")) {
				cmd.setMakeRepoPrivate(false);
			} else {
				throw new IllegalDataException (createRepoColumn, info[createRepoColumn], "the data at this entry does not conform to a boolean type!");
			}
		}
		//add collaborators_Profs-data cannot be empirically invalid
		ArrayList<Integer> columnsOfColabs_ProfsToAdd = format.getProf_Add_Columns();
		for (int columnNum : columnsOfColabs_ProfsToAdd) {
			ArrayList<String> temp = cmd.getAddCollabs_Profs();
			temp.add(info[columnNum]);
			cmd.setAddCollabs_Profs(temp);
		}
		
		//add collaborators_TAs-data cannot be empirically invalid
		ArrayList<Integer> columnsOfColabs_TAsToAdd = format.getTA_Add_Columns();
		for (int columnNum : columnsOfColabs_TAsToAdd) {
			ArrayList<String> temp = cmd.getAddCollabs_TAs();
			temp.add(info[columnNum]);
			cmd.setAddCollabs_TAs(temp);
		}
		
		//add collaborators_Studs-data cannot be empirically invalid
		ArrayList<Integer> columnsOfColabs_StudsToAdd = format.getStudent_Add_Columns();
		for (int columnNum : columnsOfColabs_StudsToAdd) {
			ArrayList<String> temp = cmd.getAddCollabs_Studs();
			temp.add(info[columnNum]);
			cmd.setAddCollabs_Studs(temp);
		}
		
		//remove collaborators_Profs-data cannot be empirically invalid
		ArrayList<Integer> columnsOfColabs_ProfsToRemove = format.getProf_Remove_Columns();
		for (int columnNum : columnsOfColabs_ProfsToRemove) {
			ArrayList<String> temp = cmd.getRemoveCollabs_Profs();
			temp.add(info[columnNum]);
			cmd.setRemoveCollabs_Profs(temp);
		}
		//remove collaborators_TAs-data cannot be empirically invalid
		ArrayList<Integer> columnsOfColabs_TAsToRemove = format.getTA_Remove_Columns();
		for (int columnNum : columnsOfColabs_TAsToRemove) {
			ArrayList<String> temp = cmd.getRemoveCollabs_TAs();
			temp.add(info[columnNum]);
			cmd.setRemoveCollabs_TAs(temp);
		}
		//remove collaborators_Studs-data cannot be empirically invalid
		ArrayList<Integer> columnsOfColabs_StudsToRemove = format.getStudent_Remove_Columns();
		for (int columnNum : columnsOfColabs_StudsToRemove) {
			ArrayList<String> temp = cmd.getRemoveCollabs_Studs();
			temp.add(info[columnNum]);
			cmd.setRemoveCollabs_Studs(temp);
		}
		//set cmd invite to read only if applicable
		if(format.readOnlyInvitesColumn != -1) {
			if(info[format.readOnlyInvitesColumn].toLowerCase().equals("yes")) {
				cmd.setPermissionInvite(Permissions.PULL);
			}
		}
		
		if(format.permissionsInviteColumn != -1) {
			boolean match = false;
			for(Permissions permission : Permissions.values()) {
				String arg = info[format.permissionsInviteColumn].toLowerCase();
				if(arg.equals(permission.permission) || arg.equals(permission.displayName)) {
					match = true;
					cmd.setPermissionInvite(permission);
				}
			}
			if(!match) {
				throw new IllegalDataException(format.permissionsInviteColumn, info[format.permissionsInviteColumn], "The data did not conform to one of the permissions types: " + Arrays.toString(Permissions.values()));
			}
		}
		//clone repo? -data can be empirically invalid
		int cloneRepoBooleanColumn = format.getCloneRepo();
		if (cloneRepoBooleanColumn != -1) {
			String cloneRepo = info[cloneRepoBooleanColumn];
			if (cloneRepo.toLowerCase().equals("yes")) {
				cmd.setCloneRepo(true);
			} else if (cloneRepo.toLowerCase().equals("no")) {
				cmd.setCloneRepo(false);
			} else {
				throw new IllegalDataException (cloneRepoBooleanColumn, info[cloneRepoBooleanColumn], "the data at this entry does not conform to a boolean type!");
			}
		}
		
		//clone location -data can be empirically invalid
		int cloneLocationColumn = format.getRepoCloneLocation();
		if (cloneLocationColumn != -1) {
			String location = info[cloneLocationColumn];
			File file = new File(location);
			file.mkdirs();
			if (!file.isDirectory()) {
				throw new IllegalDataException(cloneLocationColumn, info[cloneLocationColumn], "this does not represent a valid path!");
			}
			cmd.setCloneLocation(file);
		}
		
		//delete repo -data can be empirically invalid
		int deleteRepoColumn = format.getDeleteRepoColumn();
		if (deleteRepoColumn != -1) {
			String deleteRepoData = info[deleteRepoColumn];
			if (deleteRepoData.toLowerCase().equals("yes")) {
				cmd.setDeleteRepo(true);
			} else if (deleteRepoData.toLowerCase().equals("no")) {
				cmd.setDeleteRepo(false);
			} else {
				throw new IllegalDataException (deleteRepoColumn, info[deleteRepoColumn], "the data at this entry does not conform to a boolean type!");
			}
		}
		
		//Accept Invites - yes no
		if(format.acceptInviteColumn != -1) {
			String userResponse = info[format.acceptInviteColumn];
			if(userResponse.toLowerCase().equals("yes")) {
				cmd.setAcceptInvite(true);
			}
		}
		
		return cmd;
	}
	
	
	private class CSVHeaderParse{	
		public int userColumn = -1;
		public int RepoNameColumn = -1;
		public int createRepoColumn = -1;
		public ArrayList<Integer> Prof_Add_Columns = new ArrayList<Integer>();
		public ArrayList<Integer> Prof_Remove_Columns = new ArrayList<Integer>();
		public ArrayList<Integer> TA_Add_Columns = new ArrayList<Integer>();
		public ArrayList<Integer> TA_Remove_Columns = new ArrayList<Integer>();
		public ArrayList<Integer> Student_Add_Columns = new ArrayList<Integer>();
		public ArrayList<Integer> Student_Remove_Columns = new ArrayList<Integer>();
		public int numberOfColumns = -1;
		public int cloneRepo = -1;
		public int repoCloneLocation = -1;
		public int deleteRepoColumn = -1;
		public int makeRepoPrivate = -1;
		public int readOnlyInvitesColumn = -1;
		public int permissionsInviteColumn = -1;
		public int acceptInviteColumn = -1;
		
		/*
		 * 
		 * all column headers must conform to one of the following (Case Insensitive), not all headers must be used, 
		 * Owner
		 * Repo_Name
		 * Create_Repo
		 * Make_Private
		 * Prof_Add_Collab
		 * Prof_Remove_Collab
		 * TA_Add_Collab
		 * TA_Remove_Collab
		 * Student_Add_Collab
		 * Student_Remove_Collab
		 * Read_Only
		 * Accept_Invite
		 * Git_Clone_To_Computer?
		 * Git_Clone_Location
		 * Delete_Repo
		 */
		CSVHeaderParse(String header) throws IllegalHeaderException{
			String[] tokens = header.split(",");
			this.numberOfColumns = tokens.length;
			for (int x = 0; x < tokens.length; x++) {//for each token
				tokens[x] = tokens[x].trim();
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.OWNER.toLowerCase())){
					if (this.userColumn != -1) {
						String msg = "there can only be one column defined with the \"Owner\" header!";
						throw new IllegalHeaderException(x, tokens[x], msg);
					}
					this.userColumn = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.REPO_NAME.toLowerCase())){
					if (this.RepoNameColumn != -1) {
						String msg = "there can only be one column defined with the \"Repo_Name\" header!";
						throw new IllegalHeaderException(x, tokens[x], msg);
					}
					this.RepoNameColumn = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.CREATE_REPO.toLowerCase())){
					if (this.createRepoColumn != -1) {
						String msg = "there can only be one column defined with the \"Create_Repo\" header!";
						throw new IllegalHeaderException(x, tokens[x], msg);
					}
					this.createRepoColumn = x;
					continue;
				}
				if(tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.MAKE_PRIVATE.toLowerCase())) {
					if(this.makeRepoPrivate != -1) {
						String msg = "there can only be one column defined with the \"Make_Private\" header!";
						throw new IllegalHeaderException(x, tokens[x], msg);
					}
					this.makeRepoPrivate = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.ADD_COLLAB_PROF.toLowerCase())){
					this.Prof_Add_Columns.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.REMOVE_COLLAB_PROF.toLowerCase())){
					this.Prof_Remove_Columns.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.ADD_COLLAB_TA.toLowerCase())){
					this.TA_Add_Columns.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.REMOVE_COLLAB_TA.toLowerCase())){
					this.TA_Remove_Columns.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.ADD_COLLAB_STUD.toLowerCase())){
					this.Student_Add_Columns.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.REMOVE_COLLAB_STUD.toLowerCase())){
					this.Student_Remove_Columns.add(x);
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.READ_ONLY_INVITE.toLowerCase())) {
					this.readOnlyInvitesColumn = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.PERMISSION_INVITE.toLowerCase())) {
					this.permissionsInviteColumn = x;
					continue;
				}
				if(tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.ACCEPT_INVITATION.toLowerCase())) {
					this.acceptInviteColumn = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.CLONE_QUESTION.toLowerCase())) {
					if(this.cloneRepo != -1) {
						String msg = "there can only be one column defined with the \"Git_Clone_To_Computer?\" header!";
						throw new IllegalArgumentException(msg);
					}
					this.cloneRepo = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.CLONE_LOCATION.toLowerCase())) {
					if(this.repoCloneLocation != -1) {
						String msg = "there can only be one column defined with the \"repoCloneLocation?\" header!";
						throw new IllegalArgumentException(msg);
					}
					this.repoCloneLocation = x;
					continue;
				}
				if (tokens[x].toLowerCase().equals(utilities.Strings.CSV_Strings.DELETE_REPOSITORY.toLowerCase())){
					if (this.deleteRepoColumn != -1) {
						String msg = "there can only be one column defined with the \"Delete_Repo\" header!";
						throw new IllegalHeaderException(x, tokens[x], msg);
					}
					this.deleteRepoColumn = x;
					continue;
				}
				//if none of the if statements are triggered, then there is a header that is not recognized...
				throw new IllegalHeaderException(x, tokens[x], "there is an unrecognized token in the header!");
			}
			
			if (this.userColumn == -1 || this.RepoNameColumn == -1) {
				throw new IllegalHeaderException(-1, "N/A", "Both the \"Owner\" and \"Repo_Name\" headers must be present in the top line of the CSV file!");
			}
			
			if (this.makeRepoPrivate != -1 && !(this.createRepoColumn != -1)) {
				throw new IllegalHeaderException(-1, "N/A", "If the \"Make_Private\" header is used, then so must \"Create_Repo\" header must be present in the top line of the CSV file!");
			}
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

		public ArrayList<Integer> getProf_Add_Columns() {
			return Prof_Add_Columns;
		}

		public ArrayList<Integer> getProf_Remove_Columns() {
			return Prof_Remove_Columns;
		}

		public ArrayList<Integer> getTA_Add_Columns() {
			return TA_Add_Columns;
		}

		public ArrayList<Integer> getTA_Remove_Columns() {
			return TA_Remove_Columns;
		}

		public ArrayList<Integer> getStudent_Add_Columns() {
			return Student_Add_Columns;
		}

		public ArrayList<Integer> getStudent_Remove_Columns() {
			return Student_Remove_Columns;
		}

		public int getNumberOfColumns() {
			return numberOfColumns;
		}

		public int getCloneRepo() {
			return cloneRepo;
		}

		public int getRepoCloneLocation() {
			return repoCloneLocation;
		}

		public int getDeleteRepoColumn() {
			return deleteRepoColumn;
		}

		public int getMakeRepoPrivate() {
			return makeRepoPrivate;
		}
	}
}
