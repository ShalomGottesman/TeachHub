package parser;

public class IllegalDataException extends Exception{
	private static final long serialVersionUID = 5210695683817434389L;
	private String illegalData;
	private int columnNum;
	
	public IllegalDataException (int column, String data, String msg) {
		super(msg);
		this.illegalData = data;
		this.columnNum = column;
		
	}
	
	public int getColumn() {
		return this.columnNum;
	}
	
	public String getIllegalHeader() {
		return this.illegalData;
	}

}
