package utilities;

import java.io.File;

public class Strings {
	public static String openMsg = 
			"Welcome to TeachHub! An abstraction of the Github api, written by Shalom Gottesman";
	
	public static String noTeachHubVar = 
			"\n" +
			"********\n"+         
			"Cannot find enviorment variable labled \"TeachHub\", any files this application stores will be \n" +
			"stored in the <HomeDir>/TeachHub directory. To change this, create an enviorment variable titled \n" +
			"\"TeachHub\" with the value as the location you would like the files to be stored. The application \n" +
			"will create a sub directory \"" + File.separator + "TeachHub\" to store the application files. To avoid this \n" +
			"message from being printed, create the enviorment variabl with the value pointing to the Home Directory \n" +
			"********\n"; 
	
	public static String optionsMsg = 
			"Usage : [-l  | --login]                  -> enter login credentials for the file you want to execute \n" +
			"        [-a  | --analyze <path-to-file>] -> analyze a file without executing it \n" +
			"        [-f  | --file <path-to-file>]    -> a file to execute (implicitly calls -a on the file also) \n" +
			"        [-h  | --history]                -> open log file \n" +
			"        [-u  | --undo]                   -> undo last done execution \n" +
			"        [-r  | --redo]                   -> redo last execution \n"+
			"        [-or | --open-redo]              -> open copy of last executed file \n"+
			"        [-ou | --open-undo]              -> open generated undo file of last executed file \n" +
			"        [-H  | --help | ? | -?]          -> reprint options message \n" +
			"        [-e  | --exit]                   -> exit program \n"+
			"\n" +
			"All options are designed to be executed independantly of each other, but they can be \n"+
			"chained together if desired. The order of execution will be the order printed above. \n"+
			"note that both -a and -f cannot be called in the same execution!";
	
	public static String cloneDetectMsg = 
			"\nDetected that you are trying to clone at least one repository to a provided \n" +
					  "location. Note that this command will call the already cached credentials of \n" +
					  "your LOCAL GIT CONTROLLER, not the credentials you have already provided for the \n" +
					  "execution of the commands. If your controller does not have cached credentials, \n" +
					  "or other credentials are needed to clone with your local git controller please \n" +
					  "provide them. Do credentils have to be provided?";
	
	public static void main (String[] args) {
		System.out.println(optionsMsg);
	}
			
			
			
			
	/*
	 * options: 
	 * -l, --login               ->enter login credentials
	 * -f, --file <path-to-file> ->execute file
	 * -h, --history             ->open log file
	 * -u, --undo                ->undo last file
	 * -r, --redo                ->redo last execution
	 */
}
