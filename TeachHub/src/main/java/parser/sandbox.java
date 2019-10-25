package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import command.ExecuteCommand;
import data_structures.Que;

public class sandbox {
	public static void main(String[] args) throws FileNotFoundException, IllegalDataException {
		File file = new File ("C:\\Users\\Administrator\\MYGIT\\CodingProjects\\TeachHub\\TeachHub\\src\\main\\resources\\testing_CSVs\\Book1.csv");
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
			System.out.println(cmd.getCommandInfo());
			commandQue.enque(cmd);
		}
		sc.close();
		
		
		CSVCreator csvc = new CSVCreator();
		csvc.parseQue(commandQue);
		
		
		
		CSVTags.CREATEREPO.get;
	}
}
