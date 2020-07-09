package utilities;

import java.util.Scanner;

public class UserChoice {
	
	public UserChoice(){}

	/**
	 * Derive boolean value from user. Note the input is not case sensitive
	 * @param msg the prompt (method will append [Yes/No] to message, no need to include it)
	 * @param sc the scanner to scan the response from
	 * @return boolean if user says yes or no
	 */
	public boolean yesNo(String msg, Scanner sc) {
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
		return yesNo(msg, sc);
	}
}
