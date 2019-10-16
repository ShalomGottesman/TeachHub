package archive;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class HistoryArchive {
	private String ProgramFilesLocationString;
	private File ProgramFilesLocationFile;
	private File logHistDir;
	private File ExecHistDir;
	private File logSummeryFile;
	
	public HistoryArchive() {
		String location = System.getProperty("user.home");
		this.createFolderAndZipFile(location);
	}
	
	public HistoryArchive(String baseLocation) {
		this.createFolderAndZipFile(baseLocation);
	}
	
	private void createFolderAndZipFile(String location) {
		this.ProgramFilesLocationString = location + File.separator + "TeachHub";
		this.ProgramFilesLocationFile = new File(ProgramFilesLocationString);
		this.ProgramFilesLocationFile.mkdir();
		//create sub folders ExecutionHistory and LogHistory
		File execHist = new File(ProgramFilesLocationString + File.separator + "ExecutionHistory");
		this.ExecHistDir = execHist;
		if (!execHist.exists()) {
			execHist.mkdir();
		}
		File logHist = new File(ProgramFilesLocationString + File.separator + "LogHistory");
		this.logHistDir = logHist;
		if (!logHist.exists()) {
			logHist.mkdir();
		}
		//check if the log summery file has already been created
		File logSummeryFile = new File(ProgramFilesLocationString + File.separator + "Log_Summery.txt");
		this.logSummeryFile = logSummeryFile;
		if (!logSummeryFile.exists()) {
			logSummeryFile.createNewFile();
		}
	}
	
	public void newLogEntry(File newLog) {
		//add new log file to folder /LogHistory and note new entry in log_summery.txt
		Date date = new Date(System.currentTimeMillis());
		String text = "new log entry at " + date;
	    Files.write(logSummeryFile.toPath(), text.getBytes(), StandardOpenOption.APPEND);
	    
	    String logName = newLog.getName();
	    Path dest = Paths.get(logHistDir.toPath().toString() + File.separator + logName);
	    if (dest.toFile().exists()) {
	    	dest = Paths.get(dest.toString() + "(1)");
	    }
	    Files.copy(newLog.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * 
	 * @param execFile a copy of the data that was used to execute the program (or one generated from the input given), should be a CSV file
	 * @param undoFile a CSV file generated as a mirror of the execution data, used for undo
	 * @param timeExecuted the time that the execution of the data started, this information will by in file name of both the stored execution file and the redo file, thus "linking" them
	 */
	public void newHistoryEntry(File execFile, File undoFile, Date timeExecuted) {
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String formattedTime = df.format(timeExecuted);
		String execFileName = execFile.getName();
		String undoFileName = undoFile.getName();
		//assumes file extention is .csv
		String text = "executed at " + formattedTime + "-";
		execFileName = text + execFileName + ".csv";
		undoFileName = text  + "-undo-" + undoFileName + ".csv";
		
		Path execCopyPath = Paths.get(ExecHistDir.toString() + File.separator + execFileName);
		Path undoCopyPath = Paths.get(ExecHistDir.toString() + File.separator + undoFileName);
		
		Files.copy(execFile.toPath(), execCopyPath, StandardCopyOption.REPLACE_EXISTING);
		Files.copy(undoFile.toPath(), undoCopyPath, StandardCopyOption.REPLACE_EXISTING);		
	}
	
	public static void main(String[] args) {
		Date date = new Date(System.currentTimeMillis());
		System.out.println(date);
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		System.out.println(df.format(date));
	}
	
	
	/*
	 * .zip file
	 * |	mostRecentUndoFile.csv
	 * |    summerOfLogFiles
	 * |
	 * |-------history
	 * |       		history of files used...
	 * |-------LogHistory
	 * |       		history of log files...
	 */

}
