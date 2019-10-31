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
}
