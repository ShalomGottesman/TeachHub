package parser;

public class IllegalHeaderException extends Exception {
	private static final long serialVersionUID = -8583808482005361603L;
	private String illegalHeader;
	private int columnNum;
	
	public IllegalHeaderException (int column, String illegalheader, String msg) {
		super(msg);
		this.illegalHeader = illegalheader;
		this.columnNum = column;
		
	}
	
	public int getColumn() {
		return this.columnNum;
	}
	
	public String getIllegalHeader() {
		return this.illegalHeader;
	}

}
