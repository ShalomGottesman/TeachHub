package authentication;

public class Credential {
	private String userName;
	private String password;
	
	public Credential(String username, String password) {
		this.userName = username;
		this.password = password;
	}
	
	public String getUsername() {
		return this.userName;
	}
	
	public String getPassword() {
		return this.password;
	}
}
