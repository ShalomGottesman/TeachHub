package utilities;

import java.util.Scanner;

import authentication.Credential;
import authentication.InvalidCredentialException;

public class ReadCredentials {
	public static Credential readCredential(String msg, Scanner sc) {
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
