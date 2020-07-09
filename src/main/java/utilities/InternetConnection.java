package utilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class InternetConnection {
	private URL urlToTest;
	
	public InternetConnection() {
		try {
			this.urlToTest = new URL ("https://google.com");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public InternetConnection(URL urlToTest) {
		this.urlToTest= urlToTest;
	}
	
	public boolean isConnectionAvailable() {
		try {
			final URLConnection con = this.urlToTest.openConnection();
			con.connect();
			con.getInputStream().close();
			return true;
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	
	public URL getURL() {
		return this.urlToTest;
	}
}
