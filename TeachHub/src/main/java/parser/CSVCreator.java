package parser;

import java.io.File;
import java.util.Arrays;

import command.Command;
import command.ExecuteCommand;
import data_structures.Que;

public class CSVCreator {
	private CSVHeaderTags tags;
	
	public CSVCreator() {
		this.tags = new CSVHeaderTags();
	}
	
	public File parseSingle(Command cmd) {
		return null;}
	
	public String parseQue(Que<Command> commandQue) {
		Que<Command> que2 = new Que<Command>();
		//first derive all tags required
		while(commandQue.size() != 0) {
			Command cmd = commandQue.deque();
			analyzeCommandForTags(cmd);
			que2.enque(cmd);
		}
		//parse each cmd into a CSV line
		Que<String[]> allLines = new Que<String[]>();
		while (que2.size() != 0) {
			Command cmd = que2.deque();
			String[] lineInfo = analyzeCommandForInfo(cmd);
			allLines.enque(lineInfo);
		}
		
		String fileBody = generateFileTextBody(allLines);
		
		return fileBody;
		}
	
	private String generateFileTextBody(Que<String[]> infoQue) {
		String fullText = "";
		while (infoQue.size() != 0) {
			String[] thisLineArray = infoQue.deque();
			String line = arrayToCSV(thisLineArray);
			fullText += line + "\n";
		}
		System.out.println(fullText);
		return fullText;
	}
	
	private String arrayToCSV(String[] info) {
		String result = "";
		for(int x = 0; x < info.length; x++) {
			if (info[x] != null) {
				result += (info[x].trim());
			}
			if (info.length - x > 1) {
				result += ","; //will add comman after each value except the last one
			}
		}
		return result;
	}
	
	private String[] analyzeCommandForInfo(Command cmd) {
		if (tags.isUser() == false || tags.isRepoName() == false) {
			throw new IllegalStateException("None of the commands passed in had info in the user or repoName fields");
		}
		int totalColumns = this.tags.totalColumnsRequired();
		String[] info = new String[totalColumns];
		/*
		private boolean user;
		private boolean repoName;
		private boolean createRepo;
		private boolean privateColumn;
		private boolean deleteRepo;
		private int numToAdd_Profs = 0;
		private int numToAdd_TAs = 0;
		private int numToAdd_Studs = 0;
		private int numToRemove_Profs = 0;
		private int numToRemove_TAs = 0;
		private int numToRemove_Studs = 0;
		 */
		//We already know the user and repoName tags exist in the header, so fill those in
		info[0] = cmd.getUser();
		info[1] = cmd.getRepoName();
		int counter = 2;
		if (this.tags.isCreateRepo()) {
			info[counter] = boolToYesNo(cmd.isCreateRepo());
			counter++;
		}
		if(this.tags.isPrivateColumn()) {
			info[counter] = boolToYesNo(cmd.isMakeRepoPrivate());
			counter++;
		}
		if (this.tags.isDeleteRepo()) {
			info[counter] = boolToYesNo(cmd.isDeleteRepo());
			counter++;
		}
		//add info
		for (int x = 0; x < this.tags.getNumToAdd_Profs(); x++) {
			if (x < cmd.getAddCollabs_Profs().size()) {
				info[counter] = cmd.getAddCollabs_Profs().get(x);
			}
			counter++;//this should go up regardless of if there is input
		}
		for (int x = 0; x < this.tags.getNumToAdd_TAs(); x++) {
			if (x < cmd.getAddCollabs_TAs().size()) {
				info[counter] = cmd.getAddCollabs_TAs().get(x);
			}
			counter++;//this should go up regardless of if there is input
		}
		for (int x = 0; x < this.tags.getNumToAdd_Studs(); x++) {
			if (x < cmd.getAddCollabs_Studs().size()) {
				info[counter] = cmd.getAddCollabs_Studs().get(x);
			}
			counter++;//this should go up regardless of if there is input
		}
		//remove info
		for (int x = 0; x < this.tags.getNumToRemove_Profs(); x++) {
			if (x < cmd.getRemoveCollabs_Profs().size()) {
				info[counter] = cmd.getRemoveCollabs_Profs().get(x);
			}
			counter++;//this should go up regardless of if there is input
		}
		for (int x = 0; x < this.tags.getNumToRemove_TAs(); x++) {
			if (x < cmd.getRemoveCollabs_TAs().size()) {
				info[counter] = cmd.getRemoveCollabs_TAs().get(x);
			}
			counter++;//this should go up regardless of if there is input
		}
		for (int x = 0; x < this.tags.getNumToRemove_Studs(); x++) {
			if (x < cmd.getRemoveCollabs_Studs().size()) {
				info[counter] = cmd.getRemoveCollabs_Studs().get(x);
			}
			counter++;//this should go up regardless of if there is input
		}
		return info;
	}
	
	private String boolToYesNo(boolean bool) {
		if (bool) {
			return "Yes";
		} else {
			return "No";
		}
	}
	
	private void analyzeCommandForTags(Command cmd) {
		if (cmd.getUser() != null) {
			this.tags.setUser(true);
		}
		if (cmd.isCreateRepo()) {
			this.tags.setCreateRepo(true);
		}
		if (cmd.getRepoName() != null) {
			this.tags.setRepoName(true);
		}
		
		if (cmd.getAddCollabs_Profs().size() > this.tags.getNumToAdd_Profs()) {
			int numToAdd = cmd.getAddCollabs_Profs().size();
			this.tags.setNumToAdd_Profs(numToAdd);
		}
		if (cmd.getAddCollabs_TAs().size() > this.tags.getNumToAdd_TAs()) {
			int numToAdd = cmd.getAddCollabs_TAs().size();
			this.tags.setNumToAdd_TAs(numToAdd);
		}
		if (cmd.getAddCollabs_Studs().size() > this.tags.getNumToAdd_Studs()) {
			int numToAdd = cmd.getAddCollabs_Studs().size();
			this.tags.setNumToAdd_Studs(numToAdd);
		}
		if (cmd.getRemoveCollabs_Profs().size() > this.tags.getNumToRemove_Profs()) {
			int numToRemove = cmd.getRemoveCollabs_Profs().size();
			this.tags.setNumToAdd_Profs(numToRemove);
		}
		if (cmd.getRemoveCollabs_TAs().size() > this.tags.getNumToRemove_TAs()) {
			int numToRemove = cmd.getRemoveCollabs_TAs().size();
			this.tags.setNumToAdd_TAs(numToRemove);
		}
		if (cmd.getRemoveCollabs_Studs().size() > this.tags.getNumToRemove_Studs()) {
			int numToRemove = cmd.getRemoveCollabs_Studs().size();
			this.tags.setNumToAdd_Studs(numToRemove);
		}
		if (cmd.isMakeRepoPrivate()) {
			this.tags.setPrivateColumn(true);
		}
		if(cmd.isDeleteRepo()) {
			this.tags.setDeleteRepo(true);
		}
	}
	
	
	private class CSVHeaderTags{
		private boolean user;
		private boolean repoName;
		private boolean createRepo;
		private boolean privateColumn;
		private boolean deleteRepo;
		private int numToAdd_Profs;
		private int numToAdd_TAs;
		private int numToAdd_Studs;
		private int numToRemove_Profs;
		private int numToRemove_TAs;
		private int numToRemove_Studs;
		
		public CSVHeaderTags() {}

		public int totalColumnsRequired() {
			int total = 0;
			if (user) {
				total++;
			}
			if (createRepo) {
				total++;
			}
			if (repoName) {
				total++;
			}
			total += numToAdd_Profs
					+ numToAdd_TAs
					+ numToAdd_Studs
					+ numToRemove_Profs
					+ numToRemove_TAs
					+ numToRemove_Studs;
			if (privateColumn) {
				total++;
			}
			if (deleteRepo) {
				total++;
			}
			return total;
		}
		
		public boolean isUser() {
			return user;
		}

		public void setUser(boolean user) {
			this.user = user;
		}

		public boolean isCreateRepo() {
			return createRepo;
		}

		public void setCreateRepo(boolean createRepo) {
			this.createRepo = createRepo;
		}

		public boolean isRepoName() {
			return repoName;
		}

		public void setRepoName(boolean repoName) {
			this.repoName = repoName;
		}

		public int getNumToAdd_Profs() {
			return numToAdd_Profs;
		}

		public void setNumToAdd_Profs(int numToAdd_Profs) {
			this.numToAdd_Profs = numToAdd_Profs;
		}

		public int getNumToAdd_TAs() {
			return numToAdd_TAs;
		}

		public void setNumToAdd_TAs(int numToAdd_TAs) {
			this.numToAdd_TAs = numToAdd_TAs;
		}

		public int getNumToAdd_Studs() {
			return numToAdd_Studs;
		}

		public void setNumToAdd_Studs(int numToAdd_Studs) {
			this.numToAdd_Studs = numToAdd_Studs;
		}

		public int getNumToRemove_Profs() {
			return numToRemove_Profs;
		}

		public void setNumToRemove_Profs(int numToRemove_Profs) {
			this.numToRemove_Profs = numToRemove_Profs;
		}

		public int getNumToRemove_TAs() {
			return numToRemove_TAs;
		}

		public void setNumToRemove_TAs(int numToRemove_TAs) {
			this.numToRemove_TAs = numToRemove_TAs;
		}

		public int getNumToRemove_Studs() {
			return numToRemove_Studs;
		}

		public void setNumToRemove_Studs(int numToRemove_Studs) {
			this.numToRemove_Studs = numToRemove_Studs;
		}

		public boolean isPrivateColumn() {
			return privateColumn;
		}

		public void setPrivateColumn(boolean privateColumn) {
			this.privateColumn = privateColumn;
		}

		public boolean isDeleteRepo() {
			return deleteRepo;
		}

		public void setDeleteRepo(boolean deleteRepo) {
			this.deleteRepo = deleteRepo;
		}
		
		
	}

}
