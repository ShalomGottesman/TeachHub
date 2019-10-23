package utilities;

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
		if (value.equals(System.getProperty("user.home"))) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getVariable() {
		String value = envMap.get("TeachHub");
		if (value == null) {
			return System.getProperty("user.home");
		} else {
			return value;
		}
	}
	
	public static void main (String[] args) {
		System.out.println(new EnviormentVariable().doesSysVarExist());
	}
}
