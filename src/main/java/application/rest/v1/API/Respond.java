package application.rest.v1.API;

/**************************/
//External Libs
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.ObjectWriter; 
import com.fasterxml.jackson.core.JsonProcessingException;
/**************************/

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

	public void setToSuccess(String msg){
		this.setCode(200);
		this.setStatus("Success");
		this.setMsg(msg);
	}

	public void setToFailure(String msg){
		this.setCode(500);
		this.setStatus("error");
		this.setMsg(msg);
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
		ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String res = "";

		//create Response
		try{
			res = writer.writeValueAsString(this);
		} catch (JsonProcessingException e){
			//Should use ObjectWriter to convert an instance to JSON, this is a backup failure message if the object writer is unable to serialize
			res = "{\"code\":500, \"status\":\"Error\", \"msg\":\"Writer could not convert target object to JSON string. Failed with this message: "+e.getMessage()+"\"}";
		}

		return res;
	}
}

