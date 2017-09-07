package application.rest.v1.API;

public class Respond{
	private int code;
	private String status;
	private String msg;

	public Respond(){
		this.code=0;
		this.status = "";
		this.msg="";
	}

	public Respond(int code, String status, String msg){
		this.code = code;
		this.status = status;
		this.msg = msg;
	}

	public void setCode(int code){
		this.code = code;
	}

	public int getCode(){
		return this.code;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return this.status;
	}

	public void setMsg(String msg){
		this.msg = msg;
	}

	public String getMsg(){
		return this.msg;
	}

	@Override
	public String toString(){
		//Should use ObjectWriter to convert an instance to JSON, this is a backup failure message if the object writer is unable to serialize
		return "{\"code\":500, \"status\":\"Error\", \"msg\":\"Writer could not convert target object to JSON string. Failed with this message: "+this.getMsg()+"\"}";
	}
}

