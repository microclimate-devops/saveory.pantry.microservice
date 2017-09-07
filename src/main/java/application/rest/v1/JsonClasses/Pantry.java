package application.rest.v1.JsonClasses;

/**************************/
//Custom Exceptions
import application.rest.v1.CustomExceptions.PantryException;
/**************************/

/**************************/
//External Libs
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.core.type.TypeReference;
/**************************/

@JsonIgnoreProperties
public class Pantry{
	private String id;
	private String user;
	private List<Ingredient> pantry;
	public Pantry(){
		this.id = "";
		this.user = "";
		this.pantry = null;
	}

	public Pantry(String user, Ingredient firstIngred){
		List<Ingredient> pantry = new ArrayList<Ingredient>();
		pantry.add(firstIngred);
	
		this.user = user;
		this.pantry = pantry;
	}

	@JsonProperty("_id")
	public void setId(String id){
		this.id = id;
	}

	@JsonProperty("_id")
	public String getId(){
		return this.id;
	}

	public void setUser(String user){
		this.user = user;
	}	

	public String getUser(){
		return this.user;
	}

	public void setPantry(List<Ingredient> pantry) /*throws PantryException*/{	
		this.pantry = pantry;
		/*try{
			ObjectMapper mapper = new ObjectMapper();
			this.pantry = mapper.readValue(pantry, new TypeReference<List<Ingredient>>(){}); 
		} catch (IOException e){
			throw new PantryException("Could not set pantry to "+pantry+". Failed with error: "+e.getMessage());
		}*/
	}

	public List<Ingredient> getPantry(){
		return this.pantry;
		//return this.pantry.toString();
	}		
}

