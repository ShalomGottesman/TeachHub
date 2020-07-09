package parser;

public class IllegalHeaderException extends Exception {
	private static final long serialVersionUID = -8583808482005361603L;
	private String illegalHeader;
	private int columnNum;
	
	/**
	 * used when there is an error in the header of a file being passed in (locally called for the CSV parser) 
	 * @param column column the error is in
	 * @param illegalheader the information in that column
	 * @param msg the message to accompany any standard exception
	 */
	public IllegalHeaderException (int column, String illegalheader, String msg) {
		super(msg);
		this.illegalHeader = illegalheader;
		this.columnNum = column;
		
	}
	
	/**
	 * 
	 * @return the column where the error occurred
	 */
	public int getColumn() {
		return this.columnNum;
	}
	
	/**
	 * 
	 * @return the information at the column there was an error
	 */
	public String getIllegalHeader() {
		return this.illegalHeader;
	}

}
