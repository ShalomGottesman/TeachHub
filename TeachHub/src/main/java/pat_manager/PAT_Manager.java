package pat_manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;


import authentication.InvalidCredentialException;
import authentication.PAT;
import authentication.PAT.securityLevel;
import authentication.PAT_Token;
import utilities.EnviormentVariable;
import utilities.ReadCredentials;
import utilities.UserChoice;

public class PAT_Manager {
	private String patVault;
	private PAT_Serializer serializer;
	private String defaultPassword = "cNsAAF3aEwEVVn!7";
	
	public PAT_Manager() {
		patVault = new EnviormentVariable().getVariable() + File.separator + "vault";
		new File(patVault).mkdirs();
		serializer = new PAT_Serializer();
	}
	
	public String[] getAllFilesInVaultDir() {
		return new File(patVault).list();
	}
	
	protected boolean nameTypeMatch(String fileName) {
		if(fileName.charAt(0) == '[' && getExtention(fileName)!= null && getExtention(fileName).equals(".json")) {
			String strippedName = fileName.substring(1, fileName.length()-6);//strip "].json" from name
			String[] parts = strippedName.split("\\]-\\[");//should split over "]-["
			if(parts.length == 3 && (
					parts[2].equals(securityLevel.NONE.toString()) ||
					parts[2].equals(securityLevel.LOW.toString()) ||
					parts[2].equals(securityLevel.HIGH.toString())
					)){
				return true;
			}
		}
		return false;
	}
	
	public String getExtention(String str) {
		for(int x = str.length()-1; x >= 0; x--) {
			if(str.charAt(x) == '.') {
				return str.substring(x);
			}
		}
		return null;
	}
	
	public int numberOfPatFiles() {
		return getAllValidFileNames().length;
	}
	
	private String[] breakDownName(String fileName) {
		if(nameTypeMatch(fileName)) {
			String strippedName = fileName.substring(1, fileName.length()-6);
			return strippedName.split("\\]-\\[");
		} 
		return null;
	}
	
	private String compileName(String[] parts) {
		if(parts.length == 3 && (
				parts[2].equals(securityLevel.NONE.toString()) ||
				parts[2].equals(securityLevel.LOW.toString()) ||
				parts[2].equals(securityLevel.HIGH.toString())
				)){
			return "["+parts[0]+"]-["+parts[1]+"]-["+parts[2]+"].json";
		}
		return null;		
	}
	
	public String[] getAllValidFileNames() {
		String[] files = new File(patVault).list();
		int validCount = 0;
		for (String str : files) {
			if(nameTypeMatch(str)) {
				validCount++;
			}
		}
		String[] valids = new String[validCount];
		validCount = 0;
		for(String str : files) {
			if(nameTypeMatch(str)) {
				valids[validCount] = str;
				validCount++;
			}
		}
		return valids;
	}
	
	private void formatPrintAllPATs() {
		String[] valids = getAllValidFileNames();
		System.out.println("There are "+valids.length+" PATs on file");
		System.out.printf     ("    X: %-20s %-20s %-5s\n", "PAT Username", "PAT Name", "Security Level");
		for(int x = 0; x < valids.length; x++) {
			String[] breakdown = breakDownName(valids[x]);
			System.out.printf("PAT %d: %-20s %-20s %-5s\n", x, breakdown[0], breakdown[1], breakdown[2]);
		}
	}
	
	private void addNewPATDialogue(Scanner sc) {
		System.out.println(utilities.Strings.AddNewPATInfo);
		String[] userAndToken = ReadCredentials.readCredentialForPAT(sc);
		String password = "";
		securityLevel secLvl = null;
		String pat_name = "";
		while(true) {
			System.out.println("Please select a security level, NONE, LOW, or HIGH. (not case sensitive)");
			System.out.print("security level: ");
			String input = sc.nextLine();
			if(input.toLowerCase().equals("none")) {
				secLvl = securityLevel.NONE;
				break;
			}
			if(input.toLowerCase().equals("low")) {
				secLvl = securityLevel.LOW;
				while(true) {
					System.out.println("please provide a password for LOW encryption, must be at least one charachter");
					System.out.print("password: ");
					String input2 = sc.nextLine();
					if(input.length() >= 1) {
						password = input2;
						break;
					}
					System.out.println("invalid entry, try again");
				}
				break;
			}
			if(input.toLowerCase().equals("high")) {
				secLvl = securityLevel.HIGH;
				while(true) {
					System.out.println("please provide a password for HIGH encryption, must be at least 8 charachters, contain capital and lowercase letters, and a number");
					System.out.print("password: ");
					String input2 = sc.nextLine();
					if (input2.length() >= 8) {
						boolean num = input2.matches(".*\\d.*");
						boolean cap = !input2.equals(input2.toUpperCase());
						boolean low = !input2.equals(input2.toLowerCase());
						if(num  && cap && low) {
							password = input2;
							break;
						}
						if(!num) {System.out.println("Must contain a number!");}
						if(!cap) {System.out.println("Must contain a uppercase letter!");}
						if(!low) {System.out.println("Must contain a lowercase letter!");}
						continue;
					}
					System.out.println("input must be at least 8 charachters!");
				}
				break;
			}
			System.out.println("input not recognized, please try again");
		}
		while(true) {
			String illegalCharSet = "\\/?%*:|\"><. ";
			System.out.println("please provide a name for this PAT this is not longer than 10 charachters, no spaces, and does not contain:" + illegalCharSet);
			System.out.print("PAT name: ");
			String input = sc.nextLine();
			if(input.length() <= 10) {
				boolean illegalCharFlag = false;
				for(int x = 0; x < input.length(); x++) {
					if (illegalCharSet.contains(""+input.charAt(x))){
						System.out.println("illegal char in name ["+input.charAt(x)+"], please provide a different name");
						illegalCharFlag = true;
						break;
					}
				}
				if (illegalCharFlag) {
					continue;
				} else {
					pat_name = input;
					break;
				}
			}
			System.out.println("input length was greater than 10");
		}
		try {
			PAT_Token newToken = new PAT_Token(userAndToken[0], userAndToken[1], secLvl, pat_name);
			if (newToken.getSecLevel() == PAT.securityLevel.NONE) {
				newToken.encryptToken(defaultPassword);
			} else {
				newToken.encryptToken(password);
			}
			String ret = serializer.serialize(newToken);
			System.out.println("succesful storage of new token in file: " + ret);
		} catch (InvalidCredentialException e) {
			e.printStackTrace();
			boolean tryAgain = new UserChoice().yesNo("Provided info did not check out, would you like to try again", sc);
			if(tryAgain) {
				addNewPATDialogue(sc);
			} else {
				System.out.println("returning from dialogue");
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			boolean tryAgain = new UserChoice().yesNo("Error storing token on disk, would you like to try again?", sc);
			if(tryAgain) {
				addNewPATDialogue(sc);
			} else {
				System.out.println("returning from dialogue");
				return;
			}
		}
		
	}
		
	private void deletePatDialogue(Scanner sc) {
		System.out.println("All PATs on file:");
		formatPrintAllPATs();
		while(true) {
			System.out.print("PAT number to delete: ");
			int index = 0;
			try {
				index = Integer.parseInt(sc.nextLine().trim());
			} catch (Exception e){
				System.out.println("Input must be an integer number");
				continue;
			}
			String[] valids = getAllValidFileNames();
			if(index >= valids.length || index < 0) {
				System.out.println("please provide a number within the range of 0 and " + (valids.length -1) + " inclusive");
				continue;
			}
			String fileName = valids[index];
			if (new UserChoice().yesNo("Are you sure you want to delete the file [" + fileName+"]?", sc)) {
				String fullName = patVault + File.separator + fileName;
				System.out.println("Deleteing: " + fullName);
				try {
					Files.deleteIfExists(new File(fullName).toPath());
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("error deleting file from system, going back to menu");
				}
				return;
			} else {
				System.out.println("Not deleting, returning to menu");
				return;
			}
		}
	}
	
	public PAT_Token retreiveToken(String request, String password) {
		String[] valids = getAllValidFileNames();
		String fileName = null;
		for (int x = 0; x < valids.length; x++) {
			if(valids.equals(request)) {
				fileName = valids[x];
			}
		}
		if(fileName == null) {
			System.out.println("request did not match");
			return null;
		}
		PAT_Token token;
		try {
			token = serializer.deserialize(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("unable to read PAT [" + fileName+"] into memory, please try again");
			return null;
		}
		if(token.getSecLevel() == PAT.securityLevel.NONE) {
			System.out.println("decrypting PAT with default encrpytion");
			token.decryptToken(defaultPassword);
			return token;
		} else {
			System.out.println("PAT "+fileName+" has a "+ token.getSecLevel().toString() + " level of security, please provide the password");
			if(token.hashCompare(password.hashCode())) {
				token.decryptToken(password);
				if (token.isDecrypted()) {
					return token;
				}
				System.out.println("Password did not decrypt the token to match the username");
				return null;
			}
			System.out.println("password does not match, plese try again");
			return null;
		}			
	}
	
	public PAT_Token retreiveToken(int index, String password) {
		String[] valids = getAllValidFileNames();
		if(index >= valids.length || index < 0) {
			System.out.println("please provide a number within the range of 0 and " + (valids.length -1) + " inclusive");
			return null;
		}
		String fileName = valids[index];
		PAT_Token token;
		try {
			token = serializer.deserialize(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("unable to read PAT [" + fileName+"] into memory, please try again");
			return null;
		}
		if(token.getSecLevel() == PAT.securityLevel.NONE) {
			System.out.println("decrypting PAT with default encrpytion");
			token.decryptToken(defaultPassword);
			return token;
		} else {
			System.out.println("PAT "+fileName+" has a "+ token.getSecLevel().toString() + " level of security, please provide the password");
			if(token.hashCompare(password.hashCode())) {
				token.decryptToken(password);
				if (token.isDecrypted()) {
					return token;
				}
				System.out.println("Password did not decrypt the token to match the username");
				return null;
			}
			System.out.println("password does not match, plese try again");
			return null;
		}			
	}
		
	public PAT_Token retreiveToken(Scanner sc) {
		System.out.println("All PATs on file:");
		formatPrintAllPATs();
		while(true) {
			System.out.println("PAT number to retreive: ");
			int index;
			try {
				index = Integer.parseInt(sc.nextLine().trim());
			} catch (Exception e){
				System.out.println("Input must be an integer number");
				continue;
			}
			String[] valids = getAllValidFileNames();
			if(index >= valids.length || index < 0) {
				System.out.println("please provide a number within the range of 0 and " + (valids.length -1) + " inclusive");
				continue;
			}
			String fileName = valids[index];
			if (new UserChoice().yesNo("Are you sure you want to use the PAT [" + fileName+"]?", sc)) {
				PAT_Token token;
				try {
					token = serializer.deserialize(fileName);
				} catch (IOException e) {
					e.printStackTrace();
					if (new UserChoice().yesNo("unable to read PAT [" + fileName+"] into memory, try again?", sc)) {
						continue;
					}
					return null;
				}
				if(token.getSecLevel() == PAT.securityLevel.NONE) {
					System.out.println("decrypting PAT with default encrpytion");
					token.decryptToken(defaultPassword);
					return token;
				} else {
					while(true) {
						System.out.println("PAT "+fileName+" has a "+ token.getSecLevel().toString() + " level of security, please provide the password");
						String password = "";
						java.io.Console console = System.console();
						if (console != null) {
							System.out.println("Please be aware that your password will NOT appear on screen as you type");
							char[] pwd = console.readPassword("password: ");
							password = new String(pwd);
						} else {
							System.out.println("could not get instance of System console, password will be printed");
							System.out.print("password: ");
							password = sc.nextLine();
						}
						if(token.hashCompare(password.hashCode())) {
							System.out.println("decrypting with: " + password);
							token.decryptToken(password);
							if (token.isDecrypted()) {
								return token;
							}
							System.out.println("Password did not decrypt the token to match the username");
							continue;
						}
						System.out.println("password does not match, plese try again");
						continue;
					}	
				}
			} 
		}
			
	}
	//file name syntax: [name]-[PAT_Name]-[security level].json
	//example: [ShalomGottesman]-[main]-[NONE].json
	public void commandLoop(Scanner sc) {
		System.out.println(utilities.Strings.PAT_MangerInfo);
		System.out.println(utilities.Strings.PAT_MangerOptions);
		while(true) {
			System.out.print("TeachHub PAT Manager> ");
			String[] input = sc.nextLine().split("\\s");
			if(input[0].equals("-l") || input[0].equals("--list")) {
				formatPrintAllPATs();
				continue;
			}
			if(input[0].equals("-a") || input[0].equals("--add")) {
				addNewPATDialogue(sc);
				continue;
			}
			if(input[0].equals("-d") || input[0].equals("--delete")) {
				deletePatDialogue(sc);
				continue;
			}
			if(input[0].equals("-h") || input[0].equals("--help")) {
				System.out.println(utilities.Strings.PAT_MangerOptions);
				continue;
			}
			if(input[0].equals("-e") || input[0].equals("--exit")) {
				break;
			}
			System.out.println("Command not recognized");
		}
		return;
	}
	
	public static void main(String[] args) {
		PAT_Manager ptm = new PAT_Manager();
		Scanner sc = new Scanner(System.in);
		ptm.commandLoop(sc);
		ptm.retreiveToken(sc);
		ptm.commandLoop(sc);
		
	}
}
