package utilities;

import java.io.File;
import java.util.Map;

public class EnviormentVariable {
	private Map<String, String> envMap;
	
	public EnviormentVariable() {
		this.envMap = System.getenv();
	}
	
	public boolean doesSysVarExist() {
		String value = envMap.get("TeachHub");
		if (value == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isVarDefault() {
		String value = this.getVariable();
		if (value.equals(System.getProperty("user.home") + File.separator + "TeachHub")) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getVariable() {
		String value = envMap.get("TeachHub");
		if (value == null) {
			return System.getProperty("user.home") + File.separator + "TeachHub";
		} else {
			return value  + File.separator + "TeachHub";
		}
	}
	
	public File getStorageLocation() {
		File file = new File(this.getVariable());
		file.mkdirs();
		return file;
	}
	
	public File getHistoryLocation() {
		File file = new File(this.getStorageLocation() + File.separator + "History");
		file.mkdirs();
		return file;
	}
	
	public static void main (String[] args) {
		
	}
	
	/*
	 * TLD : the undo, redo, and log files
	 * History dir: sub folder with timestamp, inside the execution file and undo file
	 */
}
