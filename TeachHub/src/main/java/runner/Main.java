package runner;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.jcabi.github.Github;

import authentication.Authentication;
import authentication.Credential;
import command.CLICommandRunner;
import command.ExecuteCommand;
import data_structures.Que;
import parser.CSVParser;
import parser.IllegalDataException;
import parser.IllegalHeaderException;


public class Main {
	private static String currentDir = System.getProperty("user.dir");
	@SuppressWarnings("unused")
	private static String pathToFile = currentDir + "\\src\\main\\resources\\testing_CSVs\\Book1.csv";
	private static String pathToFile2 = currentDir + "\\src\\main\\resources\\testing_CSVs\\Book1-inverse2.csv";
	//the formating of the file does matter, not all CSV files are the same. i had issues when it was formatted as a UTF-8 by excel, but regular .CSV works now
	
	
	
	public static void main(String[] args) throws IOException, IllegalDataException {
		//File file = new File(pathToFile);
		File file = new File(pathToFile2);
		Scanner sc = new Scanner(file);
		String topLine = sc.nextLine();
		CSVParser csvp = new CSVParser();
		try {
			csvp.parseCSVHeader(topLine);
		} catch (IllegalHeaderException e) {
			System.out.println("column: " + e.getColumn() + ": " + e.getIllegalHeader() + ". " + e.getMessage());
			e.printStackTrace();
		}
		Que<ExecuteCommand> commandQue = new Que<ExecuteCommand>();
		while (sc.hasNextLine()) {
			String nextLine = sc.nextLine();
			ExecuteCommand cmd = csvp.parseLine(nextLine);
			commandQue.enque(cmd);
		}
		sc.close();
		
		Que<ExecuteCommand> commandQue2 = new Que<ExecuteCommand>();
		while (commandQue.size() != 0) {
			ExecuteCommand cmd = commandQue.deque();
			commandQue2.enque(cmd);
			System.out.println(cmd.getCommandInfo());
		}
		
		Authentication auth = new Credential("", "");
		Github github = auth.authenticate();
		
		
		Scanner sc2 = new Scanner(System.in);
		CLICommandRunner clir = new CLICommandRunner(github, false, "", sc2);
		clir.executeStack(commandQue2);
		sc2.close();
			
		
	}

}
