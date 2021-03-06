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

import authentication.Authentication;
import command.CLICommandRunner;
import command.Command;
import command.ExecuteCommand;
import command.UndoCommand;
import data_structures.Queue;
import parser.CSVCreator;
import parser.CSVParser;
import parser.IllegalDataException;
import parser.IllegalHeaderException;
import pat_manager.PAT_Manager;
import utilities.EnviormentVariable;
import utilities.InternetConnection;
import utilities.ReadCredentials;
import utilities.Strings;
import utilities.UserChoice;

public class Main {
	
	private static enum verify{VERIFIED, UNVERIFIED};
	private static verify status = verify.UNVERIFIED;
		
	private static boolean login;
	private static boolean invalidate;
	private static boolean analyze;
	private static boolean file;
	private static boolean openManager;
	private static boolean history;
	private static boolean undo;
	private static boolean redo;
	private static boolean openRedo;
	private static boolean openUndo;
	private static boolean help;
	private static boolean exit;
	
	private static Authentication credentials = null;
	
	private static void precheck() {
		if (verify()) {
			System.out.println(utilities.Strings.openMsg);
			if (!new EnviormentVariable().doesSysVarExist()) {
				System.out.println(utilities.Strings.noTeachHubVar);
			}
			System.out.println(utilities.Strings.optionsMsg);
			if (new PAT_Manager().numberOfPatFiles() == 0) {
				System.out.println(utilities.Strings.PAT_STRINGS.PAT_MangerInfo);
			}
		} else {
			System.out.println("verification failed, has your license expired?");
			System.exit(0);
		}
	}
	
	public static void main(String[] args) throws IllegalDataException, IOException {
		precheck();
		Scanner sc = new Scanner(System.in);
		run(new String[0], sc);
	}
	
	/**
	 * Run main program with file already passed in
	 * @param file the file to run
	 * @param sc a scanner for any user input (should probably be Scanner(System.in)
	 * @throws IllegalDataException
	 * @throws IOException
	 */
	public static void mainW_File(File file, Scanner sc) throws IllegalDataException, IOException {
		precheck();
		String[] args = new String[2];
		args[0] = "-f";
		args[1] = file.toString();
		run(args, sc);
	}
	
	/**
	 * Run mian program with file and authentication object (likely retreived earlier from the PAT Manger)
	 * @param file The file to run
	 * @param creds The authentication object
	 * @param sc A scanner for any user input (should probably be Scanner(System.in)
	 * @throws IllegalDataException
	 * @throws IOException
	 */
	public static void mainW_FileW_Creds(File file, Authentication creds, Scanner sc) throws IllegalDataException, IOException {
		precheck();
		String[] args = new String[2];
		args[0] = "-f";
		args[1] = file.toString();
		credentials = creds;
		run(args, sc);
	}
	
	private static void run(String[] args, Scanner sc) throws IllegalDataException, IOException {
		resetAll();
		InternetConnection intCon = new InternetConnection();
		String prompt = "TeachHub> ";
		if(credentials != null) {
			prompt = "TeachHub ("+credentials.getUser()+")> ";
		}
		System.out.print(prompt);
		String[] input = null;
		if (args.length == 0) {
			input = sc.nextLine().split("\\s");
		} else {
			input = args;
		}
		parseLine(input);
		if(openManager) {
			new PAT_Manager().commandLoop(sc);
			resetAll();
		}
		if(invalidate) {
			credentials = null;
		}
		if (login) {
			if (intCon.isConnectionAvailable()) {
				if(new UserChoice().yesNo(utilities.Strings.PAT_STRINGS.StoredPAT_OrUserInput, sc)) {
					credentials = new PAT_Manager().retreiveToken(sc);
					if (credentials == null) {
						System.out.println("PAT not retreived from PAT, please try again");
						run(new String[0], sc); //recall the method
					}
				} else {
					credentials = ReadCredentials.readCredential("user name and password to use to execute the file", sc);
				}
			} else {
				System.out.println("could not connect to " + intCon.getURL().getPath() + ". Do you have an active intenet connection?");
			}
		}
		Queue<ExecuteCommand> commandQue = null;
		Queue<ExecuteCommand> commandQue2 = new Queue<ExecuteCommand>();
		Queue<Command> commandQue3 = new Queue<Command>();
		Queue<Command> undoQue = new Queue<Command>();
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
				System.out.println("there was no input after the tag " + input[filePath-1] + "! Please correct and try again");
				run(new String[0], sc); //recall the method
			} catch (FileNotFoundException e) {
				System.out.println("The File passed in at " + input[filePath] + " does not exist!");
				run(new String[0], sc); //recall the method
			} catch (IllegalArgumentException e) {
				run(new String[0], sc); //recall the method
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
			CSVCreator csvc2 = new CSVCreator();
			undoCSV = csvc2.parseQue(undoQue);
			System.out.println();
			System.out.println("redo csv info: \n" + redoCSV);
			System.out.println("undo csv info: \n" + undoCSV);
			
		}
		if (file) {
			if(new UserChoice().yesNo("Please review the printout above. Do you want to continue?", sc)) {
			//pass the returned stack from the parsing and the credentials to the execution class			
				if (intCon.isConnectionAvailable()) {
					if(credentials == null) {
						if(new UserChoice().yesNo(utilities.Strings.PAT_STRINGS.StoredPAT_OrUserInput, sc)) {
							credentials = new PAT_Manager().retreiveToken(sc);
							if (credentials == null) {
								System.out.println("PAT not retreived from PAT, please try again");
								run(new String[0], sc); //recall the method
							}
						} else {
							credentials = ReadCredentials.readCredential("user name and password to use to execute the file", sc);
						}
					} else {
						System.out.println("Using already provided credentials");
					}
					
					Github github = credentials.authenticate();
					String username = github.users().self().login();
					Authentication cloneCreds = null;
					
					if (isAnyCommandClone) {
						boolean needCredsForClone = new UserChoice().yesNo(utilities.Strings.cloneDetectMsg, sc);
						if (needCredsForClone) {
							boolean useAlreadyProvided = new UserChoice().yesNo("do these credentials happen to be the same as the ones already provided to \n" + 
																		 "execute the commands?", sc);
							if (useAlreadyProvided) {
								cloneCreds = credentials;
							} else {
								cloneCreds = ReadCredentials.readCredential("user name and password to clone the repositories", sc);
							}
						} else {
							cloneCreds = credentials;
						}
					}
					CLICommandRunner cliCR = new CLICommandRunner(credentials, false, username, sc);
					//CLICommandRunner cliCR = new CLICommandRunner(github, false, username, sc);
					cliCR.executeStack(commandQue2, isAnyCommandClone, cloneCreds);
					//now use que3 to create an undo/redo file set
					setUndoRedoFiles(redoCSV, undoCSV);
				} else {
					System.out.println("could not connect to " + intCon.getURL().getPath() + ". Do you have an active intenet connection?");
				}
			}
		}
		if (history) {
			try {
				File historyFolder = new File(new EnviormentVariable().getStorageLocation() + File.separator + "History");
				Desktop.getDesktop().open(historyFolder);
			} catch (IllegalArgumentException e) {
				System.out.println("There is no History folder... has there been a file executed with the current Enviorment Variable?");
			}
		}
		if (undo) {
			//first display the file
			//ask if file should be executed, if yes, execute
			File undoFile = new File(new EnviormentVariable().getStorageLocation() + File.separator + "undo_last.csv");
			displayFileContents(undoFile);
			boolean execute = new UserChoice().yesNo("do you want to execute this file? Note that it will overwrite the most recent undo/redo file set", sc);
			String[] argsToRun = {"-f", undoFile.toString()};
			if (execute) {
				resetAll();
				run(argsToRun, sc);
			}
		}
		if (redo) {
			File redoFile = new File(new EnviormentVariable().getStorageLocation() + File.separator + "redo_last.csv");
			displayFileContents(redoFile);
			boolean execute = new UserChoice().yesNo("do you want to execute this file? Note that it will overwrite the most recent undo/redo file set", sc);
			String[] argsToRun = {"-f", redoFile.toString()};
			if (execute) {
				resetAll();
				run(argsToRun, sc);
			}
		}
		if (openRedo) {
			try {	
				File redoFile = new File(new EnviormentVariable().getStorageLocation() + File.separator + "redo_last.csv");
				Desktop.getDesktop().open(redoFile);
			} catch (java.lang.IllegalArgumentException e){
				System.out.println("There is no last redo file... has there been a file executed with the current Enviorment Variable?");
			}
		}
		if (openUndo) {
			try {
				File undoFile = new File(new EnviormentVariable().getStorageLocation() + File.separator + "undo_last.csv");
				Desktop.getDesktop().open(undoFile);
			} catch (java.lang.IllegalArgumentException e){
				System.out.println("There is no last undo file... has there been a file executed with the current Enviorment Variable?");
			}
		}
		if (help) {
			System.out.println(Strings.optionsMsg);
		}
		if (exit) {
			System.exit(0);
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
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss_a");
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
			if (str.equals("-i")  || str.equals("--invalidate")) {
				invalidate = true;
			}
			if (str.equals("-a")  || str.equals("--analyze")) {
				analyze = true;
			}
			if (str.equals("-f")  || str.equals("--file")) {
				file = true;
			}
			if (str.equals("-p")  || str.equals("-m")  || str.equals("--pat-manager")) {
				openManager = true;
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
			if (str.equals("-H")  || str.equals("--help") || str.equals("?") || str.equals("-?")) {
				help = true;
			}	
			if(str.equals("-e")   || str.equals("--exit")) {
				exit = true;
			}
			
		}
	}
	
	private static Queue<ExecuteCommand> parseFileAndPrintInfo(String[] input, Scanner sc) throws FileNotFoundException, IllegalDataException {
		Queue<ExecuteCommand> commandQue = new Queue<ExecuteCommand>();
		int filePath = 0;
		for (int x = 0; x < input.length; x++) {
			if (input[x].equals("-a") ||
				input[x].equals("-f") ||
				input[x].equals("--analyze") ||
				input[x].equals("--file") ){
				filePath = x+1;
			}
		}
		File file = new File(input[filePath]);
		Scanner fileSc = new Scanner(file);
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
			ExecuteCommand cmd = null;
			try{
				cmd = csvp.parseLine(nextLine);
			} catch (IllegalDataException e) {
				System.out.println("data [" + e.getIllegalData() +"] in column "+e.getColumn()+" does not represent a valid path, please correct");
				fileSc.close();
				throw new IllegalArgumentException();
			}
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
		openManager = false;
		history = false;
		undo = false;
		redo = false;
		openRedo = false;
		openUndo = false;
		help = false;
		exit = false;
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
