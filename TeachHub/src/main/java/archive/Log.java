package archive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class Log {
	private File logFile;
	
	public Log(String fileName) throws IOException {
		this.logFile = File.createTempFile(fileName, ".txt");
		
		this.logFile.deleteOnExit();
	}
	
	public void addLine(String lineToAdd) throws IOException {
		lineToAdd = lineToAdd + "\n";
		Files.write(logFile.toPath(), lineToAdd.getBytes(), StandardOpenOption.APPEND);
	}
	
	public File retreiveFile() {
		return this.logFile;
	}

}
