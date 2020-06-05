package utilities;

import java.util.Scanner;

import authentication.Credential;
import authentication.InvalidCredentialException;

public class ReadCredentials {
	public static Credential readCredential(String msg, Scanner sc) {
		System.out.println(msg);
		System.out.print("user name: ");
		String username = sc.nextLine();
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
		Credential cred = null;
		try {
			cred = new Credential(username, password);
			return cred;
		} catch (InvalidCredentialException e) {
			System.out.println("credentials were invalid, try again");
			return readCredential(msg, sc);
		}
	}
	
	/**
	 * Reads username and token from scanner to be used in the PAT Manger
	 * @param sc the scanner from which to read
	 * @return a string array with the information [0] the username, [1] the token
	 */
	public static String[] readCredentialForPAT(Scanner sc) {
		System.out.print("user name: ");
		String username = sc.nextLine();
		String password = "";
		java.io.Console console = System.console();
		if (console != null) {
			System.out.println("Please be aware that your token will NOT appear on screen as you type/copy");
			char[] pwd = console.readPassword("token: ");
			password = new String(pwd);
			if (password.length() == 0) {
				System.out.println("read password was on length zero, please try again");
				return readCredentialForPAT(sc);
			}
		} else {
			System.out.println("could not get instance of System console, token will be printed");
			System.out.print("token: ");
			password = sc.nextLine();
		}
		try {
			new Credential(username, password);
			String[] ret = {username, password};
			return ret;
		} catch (InvalidCredentialException e) {
			System.out.println("credentials were invalid, try again");
			return readCredentialForPAT(sc);
		}
	}
}
