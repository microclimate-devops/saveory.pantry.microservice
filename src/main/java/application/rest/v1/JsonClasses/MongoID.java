package application.rest.v1.JsonClasses;


/**************************/
//External Libs
import com.fasterxml.jackson.annotation.JsonProperty;
/**************************/

public class MongoID{
	@JsonProperty("$oid")
	private String oid;
	
	public MongoID(){
		this.oid = "";
	}

	public void setOid(String oid){
		this.oid = oid;
	}

	public String getOid(){
		return this.oid;
	}
}

