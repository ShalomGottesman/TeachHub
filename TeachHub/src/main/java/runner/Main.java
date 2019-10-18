package runner;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.jcabi.github.Github;

import authentication.Authentication;
import authentication.Credential;
import authentication.InvalidCredentialException;
import command.CLICommandRunner;
import command.ExecuteCommand;
import data_structures.Que;
import parser.CSVParser;
import parser.IllegalDataException;
import parser.IllegalHeaderException;


public class Main {
	//the formating of the file does matter, not all CSV files are the same. i had issues when it was formatted as a UTF-8 by excel, but regular .CSV works now
	
	
	
	public static void main(String[] args) throws IOException, IllegalDataException {
		if (args.length != 1) {
			throw new IllegalArgumentException("have to pass in a single file to run off of");
		}
		File file = new File(args[0]);
		//File file = new File(pathToFile);
		//File file = new File(pathToFile2);
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
		Scanner sc2 = new Scanner(System.in);
		
		Authentication auth = readCredential(sc2);
		
		Github github = auth.authenticate();
		
		String username = github.users().self().login();
		
		CLICommandRunner clir = new CLICommandRunner(github, false, username, sc2);
		clir.executeStack(commandQue2);
		sc2.close();
			
		
	}
	
	private static Authentication readCredential(Scanner sc) {
		System.out.print("user name: ");
		String username = sc.nextLine();
		java.io.Console console = System.console();
		char[] pwd = console.readPassword("password: ");
		String password = new String(pwd);
		Credential cred = null;
		try {
			cred = new Credential(username, password);
			return cred;
		} catch (InvalidCredentialException e) {
			System.out.println("credentials were invalid, try again");
			return readCredential(sc);
		}
	}
}
