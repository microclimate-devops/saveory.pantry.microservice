package application.rest.v1.CustomExceptions;

public class UnexpectedUnitException extends Exception{
	public UnexpectedUnitException(String msg){
		super(msg);
	}
}
