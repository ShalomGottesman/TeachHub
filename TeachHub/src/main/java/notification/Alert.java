package notification;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

public class Alert {
	private TrayIcon trayIcon;
	private String title;
	private String messege;
	private MessageType msgType;
	
	public Alert(MessageType type) {
		this.msgType = type;
		if (SystemTray.isSupported()) {
			SystemTray sysTray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
			trayIcon = new TrayIcon(image, "Tray Demo");
			this.trayIcon.setImageAutoSize(true);
			this.trayIcon.setToolTip("System tray icon demo");
			try {
				sysTray.add(trayIcon);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
	public boolean isSupported() {
		return SystemTray.isSupported();
	}
	
	public void setMessege(String title, String messege) {
		this.title = title;
		this.messege = messege;
	}
	
	public void execute() {
		trayIcon.displayMessage(title, messege, msgType);
	}
	
	
	/*
	public static void main(String[] args) {
		Alert a1 = new Alert(MessageType.NONE);
		Alert a2 = new Alert(MessageType.WARNING);
		Alert a3 = new Alert(MessageType.ERROR);
		Alert a4 = new Alert(MessageType.INFO);
		
		a1.setMessege("blah1", "foo1");
		a2.setMessege("blah2", "foo2");
		a3.setMessege("blah3", "foo3");
		a4.setMessege("blah4", "foo4");
		
		a1.execute();
		a2.execute();
		a3.execute();
		a4.execute();
	}
	*/
	
	
}