package runner;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import com.google.common.io.Files;
import com.jcabi.github.Github;

import authentication.Credential;
import command.CLICommandRunner;
import command.Command;
import command.ExecuteCommand;
import command.UndoCommand;
import data_structures.Que;
import parser.CSVCreator;
import parser.CSVParser;
import parser.IllegalDataException;
import parser.IllegalHeaderException;
import utilities.EnviormentVariable;
import utilities.ReadCredentials;
import utilities.Strings;
import utilities.UserChoice;

public class Main2 {
	
	private static enum verify{VERIFIED, UNVERIFIED};
	private static verify status = verify.UNVERIFIED;
		
	private static boolean login;
	private static boolean analyze;
	private static boolean file;
	private static boolean history;
	private static boolean undo;
	private static boolean redo;
	private static boolean openRedo;
	private static boolean openUndo;
	private static boolean help;
	
	
	
	public static void main(String[] args) throws IllegalDataException, IOException {
		if (verify()) {
			System.out.println(utilities.Strings.openMsg);
			if (!new EnviormentVariable().doesSysVarExist()) {
				System.out.println(utilities.Strings.noTeachHubVar);
			}
			System.out.println(utilities.Strings.optionsMsg);
			Scanner sc = new Scanner(System.in);
			run(new String[0], sc);
		} else {
			System.out.println("verification failed, has your license expired?");
			System.exit(0);
		}
	}
	
	private static void run(String[] args, Scanner sc) throws IllegalDataException, IOException {
		System.out.print("TeachHub>");
		String[] input = null;
		if (args.length == 0) {
			input = sc.nextLine().split("\\s");
		} else {
			input = args;
		}
		parseLine(input);
		Credential creds = null;
		if (login) {
			creds = ReadCredentials.readCredential("user name and password to use to execute the file", sc);
		}
		Que<ExecuteCommand> commandQue = null;
		Que<ExecuteCommand> commandQue2 = new Que<ExecuteCommand>();
		Que<Command> commandQue3 = new Que<Command>();
		Que<Command> undoQue = new Que<Command>();
		boolean isAnyCommandClone = false;
		String redoCSV = "";
		String undoCSV = "";
		if (analyze | file) {
			//parse file and print out command information
			int filePath = 0;
			for (int x = 0; x < input.length; x++) {
				if (input[x].equals("-a") ||
					input[x].equals("-f") ||
					input[x].equals("--analyze") ||
					input[x].equals("--file") ){
					filePath = x+1;
				}
			}
			try {
				commandQue = parseFileAndPrintInfo(input, sc);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("there was no input after the tag " + input[filePath-1] + "!");
				run(new String[0], sc); //recall the method
				System.exit(0);
			} catch (FileNotFoundException e) {
				System.out.println("The File passed in at " + input[filePath] + " does not exist!");
				run(new String[0], sc); //recall the method
				System.exit(0);
			}
			
			while (commandQue.size() != 0) {
				ExecuteCommand cmd = commandQue.deque();
				UndoCommand undoCmd = new UndoCommand(cmd);
				
				commandQue2.enque(cmd);
				commandQue3.enque((Command) cmd);
				undoQue.enque((Command) undoCmd);
				if (cmd.isCloneRepo()) {
					isAnyCommandClone = true;
				}
			}
			CSVCreator csvc = new CSVCreator();
			redoCSV = csvc.parseQue(commandQue3);
			csvc.resetTags();
			undoCSV = csvc.parseQue(undoQue);
			
			System.out.println("redo csv info: \n" + redoCSV);
			System.out.println("undo csv info: \n" + undoCSV);
			
		}
		if (file) {
			//pass the returned stack from the parsing and the credentials to the execution class			
			if (creds == null) {
				creds = ReadCredentials.readCredential("please provide username and password for the main user of these commands", sc);
			}
			Github github = creds.authenticate();
			String username = github.users().self().login();
			Credential cloneCreds = null;
			
			if (isAnyCommandClone) {
				boolean needCredsForClone = new UserChoice().yesNo(utilities.Strings.cloneDetectMsg, sc);
				if (needCredsForClone) {
					boolean useAlreadyProvided = new UserChoice().yesNo("do these credentials happen to be the same as the ones already provided to \n" + 
																 "execute the commands?", sc);
					if (useAlreadyProvided) {
						cloneCreds = creds;
					} else {
						cloneCreds = ReadCredentials.readCredential("user name and password to clone the repositories", sc);
					}
				} else {
					cloneCreds = creds;
				}
			}
			
			CLICommandRunner clir = new CLICommandRunner(github, false, username, sc);
			clir.executeStack(commandQue2, isAnyCommandClone, cloneCreds);
			//now use que3 to create an undo/redo file set
			setUndoRedoFiles(redoCSV, undoCSV);
		}
		if (history) {
			System.out.println("this feature has not yet been implemented");
		}
		if (undo) {
			//first display the file
			//ask if file should be executed, if yes, execute
			File undoFile = new File(new EnviormentVariable().getStorageLocation() + File.separator + "undo_last.csv");
			displayFileContents(undoFile);
			boolean execute = new UserChoice().yesNo("do you want to execute this file? Note that it will overwrite the most recent undo/redo file set", sc);
			String[] argsToRun = {"-f", undoFile.toString()};
			if (execute) {
				run(argsToRun, sc);
			}
		}
		if (redo) {
			File redoFile = new File(new EnviormentVariable().getStorageLocation() + File.separator + "redo_last.csv");
			displayFileContents(redoFile);
			boolean execute = new UserChoice().yesNo("do you want to execute this file? Note that it will overwrite the most recent undo/redo file set", sc);
			String[] argsToRun = {"-f", redoFile.toString()};
			if (execute) {
				run(argsToRun, sc);
			}
		}
		if (openRedo) {
			File redoFile = new File(new EnviormentVariable().getStorageLocation() + File.separator + "redo_last.csv");
			Desktop.getDesktop().open(redoFile);
		}
		if (openUndo) {
			File undoFile = new File(new EnviormentVariable().getStorageLocation() + File.separator + "undo_last.csv");
			Desktop.getDesktop().open(undoFile);
		}
		if (help) {
			System.out.println(Strings.optionsMsg);
		}
		resetAll();
		run(new String[0], sc);
	}
	
	private static void displayFileContents(File file) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		while(sc.hasNext()) {
			System.out.println(sc.nextLine());
		}
		sc.close();
	}
	
	private static void setUndoRedoFiles(String redoCSVasString, String undoCSVasString) throws IOException {
		File storageLocation = new EnviormentVariable().getStorageLocation();
		File redoFile = writeToFileOverwrite(redoCSVasString, storageLocation + File.separator + "redo_last.csv");
		File undoFile = writeToFileOverwrite(undoCSVasString, storageLocation + File.separator + "undo_last.csv");
		
		String formattedTime = getFormattedTime(System.currentTimeMillis());
		File copyStorage = new File(new EnviormentVariable().getHistoryLocation().toString() + File.separator + "storedAt_" + formattedTime);
		copyStorage.mkdirs();
		Files.copy(redoFile, new File(copyStorage + File.separator + "redo.csv"));
		Files.copy(undoFile, new File(copyStorage + File.separator + "undo.csv"));
	}
	
	private static String getFormattedTime(long timestamp) {
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd_hh-mm-ss");
		return df.format(timestamp);
	}
	
	private static File writeToFileOverwrite(String text, String location) throws FileNotFoundException {
		File file = new File(location);
		PrintWriter writer = new PrintWriter(file);
		writer.write(text);
		writer.close();
		return file;
	}
	
	private static void  parseLine(String[] inputAry) {
		for (String str : inputAry) {
			if (str.equals("-l") || str.equals("--login")) {
				login = true;
			}
			if (str.equals("-a")  || str.equals("--analyze")) {
				analyze = true;
			}
			if (str.equals("-f")  || str.equals("--file")) {
				file = true;
			}
			if (str.equals("-h")  || str.equals("--history")) {
				history = true;
			}
			if (str.equals("-u")  || str.equals("--undo")) {
				undo = true;
			}
			if (str.equals("-r")  || str.equals("--redo")) {
				redo = true;
			}
			if (str.equals("-or") || str.equals("--open-redo")) {
				openRedo = true;
			}
			if (str.equals("-ou") || str.equals("--open-undo")) {
				openUndo = true;
			}
			if (str.equals("-h")  || str.equals("--help") || str.equals("?") || str.equals("-?")) {
				help = true;
			}			
			
		}
	}
	
	private static Que<ExecuteCommand> parseFileAndPrintInfo(String[] input, Scanner sc) throws FileNotFoundException, IllegalDataException {
		Que<ExecuteCommand> commandQue = new Que<ExecuteCommand>();
		int filePath = 0;
		for (int x = 0; x < input.length; x++) {
			if (input[x].equals("-a") ||
				input[x].equals("-f") ||
				input[x].equals("--analyze") ||
				input[x].equals("--file") ){
				filePath = x+1;
			}
		}
		File file = null;
		file = new File(input[filePath]);
		Scanner fileSc = null;
		fileSc = new Scanner(file);
		String topLine = fileSc.nextLine();
		CSVParser csvp = new CSVParser();
		try {
			csvp.parseCSVHeader(topLine);
		} catch (IllegalHeaderException e) {
			System.out.println("column: " + e.getColumn() + ": " + e.getIllegalHeader() + ". " + e.getMessage());
			e.printStackTrace();
		}
		while (fileSc.hasNextLine()) {
			String nextLine = fileSc.nextLine();
			ExecuteCommand cmd = csvp.parseLine(nextLine);
			System.out.println(cmd.getCommandInfo());
			commandQue.enque(cmd);
		}
		fileSc.close();
		return commandQue;
	}
	
	private static void resetAll() {
		login = false;
		analyze  = false;
		file = false;
		history = false;
		undo = false;
		redo = false;
		openRedo = false;
		openUndo = false;
		help = false;
	}
	
	
	private static boolean verify() {
		status = verify.VERIFIED;
		if (status == verify.VERIFIED) {
			return true;
		} else {
			return false;
		}
	}
}
