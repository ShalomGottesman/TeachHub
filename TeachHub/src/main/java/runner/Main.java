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
		boolean isAnyCommandClone = false;
		while (commandQue.size() != 0) {
			ExecuteCommand cmd = commandQue.deque();
			commandQue2.enque(cmd);
			if (cmd.isCloneRepo()) {
				isAnyCommandClone = true;
			}
			System.out.println(cmd.getCommandInfo());
		}
		Scanner sc2 = new Scanner(System.in);
		Credential auth = readCredential("please provide username and password for the main user of these commands", sc2);
		
		Github github = auth.authenticate();
		
		String username = github.users().self().login();
		
		Credential cloneCreds = null;
		
		if (isAnyCommandClone) {
			boolean needCredsForClone = userChooseYesNo("detected that you are trying to clone at least one repository to the provided \n" +
															  "location. Note that this command will call the already cached credentials of \n" +
															  "your LOCAL GIT CONTROLLER, not the credentials you have already provided for the \n " +
															  "execution of the commands. If your controller does not have cached credentials, or \n" +
															  "other credentials are needed to clone with your local git controller please \n " +
															  "provide them. do credentils have to be provided?", sc2);
			if (needCredsForClone) {
				boolean useAlreadyProvided = userChooseYesNo("do these credentials happen to be the same as the ones already provided to \n" + 
															 "execute the commands?", sc2);
				if (useAlreadyProvided) {
					cloneCreds = auth;
				} else {
					cloneCreds = readCredential("user name and password to clone the repositories", sc2);
				}
			}
		}
		
		CLICommandRunner clir = new CLICommandRunner(github, false, username, sc2);
		clir.executeStack(commandQue2, isAnyCommandClone, cloneCreds);
		sc2.close();
			
		
	}
	
	/**
	 * Derive boolean value from user. Note the input is not case sensitive
	 * @param msg the prompt (method will append [Yes/No] to message, no need to include it)
	 * @param sc the scanner to scan the response from
	 * @return boolean if user says yes or no
	 */
	private static boolean userChooseYesNo(String msg, Scanner sc) {
		System.out.println(msg + " [Yes/No]");
		String response = sc.nextLine();
		if (response.toLowerCase().trim().equals("yes")) {
			return true;
		} 
		if (response.toLowerCase().trim().equals("no")) {
			return false;
		} 
		//no valid response
		System.out.println("invalid response, please only input Yes or No");
		return userChooseYesNo(msg, sc);
	}
	
	private static Credential readCredential(String msg, Scanner sc) {
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
			return readCredential(msg, sc);
		}
	}
}
