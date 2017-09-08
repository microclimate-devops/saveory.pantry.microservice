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

	//return the ingredient with the given id
	public Ingredient getIngredient(String ingredID) throws PantryException {
		int ingIndex = this.locateIngredient(ingredID);
		if(ingIndex == -1){
			throw new PantryException("The ingredient could not be found in the pantry");
		}

		return this.getPantry().get(ingIndex);
	}

	//Tell if the ingredient is already in the pantry
	public boolean hasIngredient(Ingredient ingred){
		ArrayList<Ingredient> pantry = new ArrayList<Ingredient>(this.getPantry());

		//Loop through and return if found
		for(Ingredient i : pantry){
			if(ingred.equals(i)){
				return true;
			}
		}

		//Not found
		return false;
	}

	//Tell if the ingredient ID is already in the pantry
	public boolean hasIngredient(String ingredID){
		ArrayList<Ingredient> pantry = new ArrayList<Ingredient>(this.getPantry());

		//Loop through and return if found
		for(Ingredient i : pantry){
			if(i.equals(ingredID)){
				return true;
			}
		}

		//Not found
		return false;
	}

	public int locateIngredient(String ingredID){
		ArrayList<Ingredient> pantry = new ArrayList<Ingredient>(this.getPantry());
		int location = -1;
		//loop through pantry and attempt to find the ingredient
		for(int i = 0; i < pantry.size() && location == -1; i++){
			if(pantry.get(i).equals(ingredID)){
				location = i;
			}
		}

		return location;
	}

	//Push an ingredient to the back of the pantry	
	public void addIngredient(Ingredient newIngred){
		ArrayList<Ingredient> pantry = new ArrayList<Ingredient>(this.getPantry());
		pantry.add(newIngred);
		this.setPantry(pantry);
	}


	//attempt to remove an ingredient from the pantry
	public void deleteIngredient(String ingredID) throws PantryException{
		int ingIndex = this.locateIngredient(ingredID);
		if(ingIndex == -1){
			throw new PantryException("The ingredient is not in the user's pantry");
		}

		//Remove the ingredient
		ArrayList<Ingredient> pantry = new ArrayList<Ingredient>(this.getPantry());
		pantry.remove(ingIndex);
		this.setPantry(pantry);
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

	@Override
	public boolean equals(Object o){
		// If the object is compared with itself then return true  
		if (o == this) {
		    	return true;
		}

		//Not even the same class
		if(!(o instanceof Pantry)){
			return false;
		}		

		Pantry p = (Pantry) o;
		return this.getUser().equals(p.getUser());
	}
}

