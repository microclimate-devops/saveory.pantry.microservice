package application.rest.v1.API;

/**************************/
//JSON classes
import application.rest.v1.JsonClasses.Ingredient;
import application.rest.v1.JsonClasses.Pantry;
/**************************/
import application.rest.v1.UnitConversion.Units;
/**************************/
//Data Access
import application.rest.v1.DataAccess.PantryDatabase;
/**************************/

/**************************/
//Custom Exceptions
import application.rest.v1.CustomExceptions.PantryException;
/**************************/

/**************************/
//External Libs
import java.util.List;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
/**************************/

@Path("pantry")
public class PantryAPI {

	//Get information on what an ingredient looks like
	@GET
	@Path("/spec/ingredient")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIngredientFields(){
		return Response.ok(Ingredient.exposeFields()).build();
	} 

	@GET
	@Path("/spec/ingredient/id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIngredientIdFieldName(){
		return Response.ok(Ingredient.exposeIdFieldName()).build();	
	} 
	
	@GET
	@Path("/spec/ingredient/types")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIngredientFieldTypes(){
		return Response.ok(Ingredient.exposeFieldTypes()).build();	
	} 

	//Get the edit options for fields in an ingredient
	@GET
	@Path("/spec/ingredient/edits")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIngredientEditableSpec(){
		return Response.ok(Ingredient.exposeEditableFields()).build();	
	} 

	//Get the available options for location field
	@GET
	@Path("/spec/ingredient/location")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIngredientFieldSpec(){
		return Response.ok(Ingredient.exposeLocationOptions()).build();	
	} 
	
	//Get the list of units available for the ingredients
	@GET
	@Path("/spec/ingredient/unit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIngredientUnitsSpec() throws IOException{
		return Response.ok(Units.getUnits()).build();	
	} 

	//Get the pantry of the user
	@GET
	@Path("/{access_token}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPantry( @PathParam("access_token") final String accessToken ){
		//try to respond with the user's pantry
		String resp = "";
		Respond errorResp = new Respond();
		//Attempt to get user's pantry
		try{
			resp =  PantryDatabase.getPantry(accessToken);
		}catch (PantryException e){
			errorResp.setToFailure(e.getMessage());
			errorResp.setCode(204); //Indicate no pantry content
			resp = errorResp.toString();
		} 

		return Response.ok(resp).build();
		
	}
	
	//Get the list of ingredients in the user's pantry
	@GET
	@Path("/{access_token}/ingredients")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIngredients(@PathParam("access_token") final String accessToken) {
		String resp = "";
		Respond errorResp = new Respond();
		//Get the ingredients
		try {
			resp = PantryDatabase.getPantryIngredientNames(accessToken);
		}catch (PantryException e) {
			errorResp.setToFailure(e.getMessage());
			errorResp.setCode(204); //Indicate no pantry content
			resp = errorResp.toString();
		}
		
		return Response.ok(resp).build();
	}
	
	//Delete a user's pantry
	@DELETE
	@Path("/{access_token}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePantry( @PathParam("access_token") final String accessToken){
		Respond respond = new Respond();
		
		//Attempt to remove the pantry
		try{
			PantryDatabase.removePantry(accessToken);
			respond.setToSuccess("Pantry deleted");
		} catch (PantryException e){
			respond.setToFailure(e.getMessage());	
		}	

		return Response.ok(respond.toString()).build();
	}

	//Add ingredient to the user's pantry
	@POST
	@Path("/{access_token}/ingredient")
	@Consumes(MediaType.APPLICATION_JSON) //An ingredient object matching the Ingredient class
	@Produces(MediaType.APPLICATION_JSON) //A message object with status included
	public Response addIngredient(@PathParam("access_token") final String accessToken, Ingredient newIngred){
		Respond respond = new Respond();
		try{
			//Add ingredient to user's pantry in the database
			PantryDatabase.addIngredient(accessToken, newIngred);
			respond.setToSuccess("Added ingredient");
		}
		
		//Any exception while adding the ingredient in the pantry is catched here
		catch(PantryException e){
			respond.setToFailure(e.getMessage());	
		}
		
		//Response is returned
		return Response.ok(respond.toString()).build();
	}

	//Update ingredient in user's pantry
	@PUT
	@Path("/{access_token}/ingredient/{ingredient_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateIngredient(@PathParam("access_token") final String accessToken, @PathParam("ingredient_id") final String ingredID, Ingredient updatedIngred){
		Respond respond = new Respond();
		try{
			//Update ingredient to user's pantry in the database
			PantryDatabase.updateIngredient(accessToken, updatedIngred);
			respond.setToSuccess("Updated ingredient");
			
		
		}
		
		//Any exception while updating the ingredient in the pantry is catched here
		catch(PantryException e){
			respond.setToFailure(e.getMessage());	
		}
		
		//Response is returned
		return Response.ok(respond.toString()).build();
	}
	
	//Auto update ingredients in user's pantry when using a recipe
	@PUT
	@Path("/{access_token}/ingredients/auto")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response autoUpdateIngredients(@PathParam("access_token") final String accessToken, List<Ingredient> updatedIngredList){
		Respond respond = new Respond();
		BasicDBObject jsonResponse = new BasicDBObject();
		ArrayList<Ingredient> failedIngredients = new ArrayList<>();
		try{
			//This holds the list of ingredients that failed to autoupdate
			failedIngredients = PantryDatabase.autoUpdateIngredient(accessToken, updatedIngredList);
			
			//The list of failed ingredients is converted into a basic db object
			jsonResponse = new BasicDBObject("failed", failedIngredients);
		}
		
		//Any exception while updating the pantry is catched here
		catch(PantryException e){
			respond.setToFailure(e.getMessage());
			return Response.ok(respond.toString()).build();
		}
		
		//If there were no failed ingredient updates we respond with a ok response
		if(failedIngredients.isEmpty()){
			respond.setToSuccess("All ingredients were updated successfully automatically");
			Response.ok(respond.toString());
		}
		
		//Otherwise we return a Json with the failed ingredient list
		return Response.ok(jsonResponse).build();
	}
	
	//Manual update ingredients in user's pantry when using a recipe
	@PUT
	@Path("/{access_token}/ingredients/manual")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response manualUpdateIngredients(@PathParam("access_token") final String accessToken, List<Ingredient> updatedIngredList){
		Respond respond = new Respond();
		try{
			//Failed ingredient list is passed into this method for manual update
			PantryDatabase.manualUpdateIngredient(accessToken, updatedIngredList);
			
			//A successful message is added to the response if no exceptions occur
			respond.setToSuccess("All ingredients were updated successfully");
		}
		
		//Any exception while updating the pantry is catched
		catch(PantryException e){
			respond.setToFailure(e.getMessage());
		}
		
		//Response is returned
		return Response.ok(respond.toString()).build();
	}
	
	//Delete ingredient in user's pantry
	@DELETE
	@Path("/{access_token}/ingredient/{ingredient_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteIngredient(@PathParam("access_token") final String accessToken, @PathParam("ingredient_id") final String ingredID) {
		Respond respond = new Respond();
		try{
			//Delete ingredient to user's pantry in the database
			PantryDatabase.removeIngredient(accessToken, ingredID);
			respond.setToSuccess("Deleted ingredient");
		
		//Exception during the deletion process are catched here
		}catch(PantryException e){
			respond.setToFailure(e.getMessage());	
		}
		
		//Response is returned
		return Response.ok(respond.toString()).build();
	}

	/*@POST
	@Path("/ingredients")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deserializeIngredients(String ingredients) throws Exception{
		String resp = "";
		Respond respond = new Respond();
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Ingredient test = mapper.readValue("{\"item\": \"banana\",\"qty\": 4,\"qtyUnit\": \"piece\",\"expDate\": \"08-04-2017\"}", Ingredient.class);
		try{
			List<Ingredient> ingredientsList = mapper.readValue(ingredients, new TypeReference<List<Ingredient>>(){}); 
			respond.setCode(200);
			respond.setStatus("Success");
			respond.setMsg(writer.writeValueAsString(ingredientsList));
			//resp = writer.writeValueAsString(ingredientsList);
			//resp = PantryDatabase.testObjMap(srcIngredient.toString());
		}
		catch(JsonProcessingException e){
			respond.setMsg("(List<Ingredient>)"+e.getMessage());
		}
		catch(Exception e){
			respond.setMsg("(Unknown Exception)"+e.getMessage());
		}

		
		//create Response
		try{
			resp = writer.writeValueAsString(respond);
		} catch (JsonProcessingException e){
			respond.setMsg(e.getMessage());
			resp = respond.toString();
		}
		//return Response.ok(writer.writeValueAsString(test)).build();
		return Response.ok(resp).build();
	}*/	
}
