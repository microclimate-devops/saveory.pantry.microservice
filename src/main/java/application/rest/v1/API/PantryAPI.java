package application.rest.v1.API;

/**************************/
//JSON classes
import application.rest.v1.JsonClasses.Ingredient;
import application.rest.v1.JsonClasses.Pantry;
/**************************/

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
/**************************/

@Path("pantry")
public class PantryAPI {

	//Get information on what an ingredient looks like
	@GET
	@Path("/spec/ingredient")
	@Produces(MediaType.APPLICATION_JSON)
	public Ingredient getIngredientSpec(){
		return new Ingredient();	
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

		}catch(PantryException e){
			respond.setToFailure(e.getMessage());	
		}

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
			//Add ingredient to user's pantry in the database
			PantryDatabase.updateIngredient(accessToken, updatedIngred);
			respond.setToSuccess("Updated ingredient");

		}catch(PantryException e){
			respond.setToFailure(e.getMessage());	
		}

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
			//Add ingredient to user's pantry in the database
			PantryDatabase.removeIngredient(accessToken, ingredID);
			respond.setToSuccess("Deleted ingredient");

		}catch(PantryException e){
			respond.setToFailure(e.getMessage());	
		}

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
