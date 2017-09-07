package application.rest.v1.API;

/**************************/
//JSON classes
import application.rest.v1.JsonClasses.Ingredient;
import application.rest.v1.JsonClasses.Ingredients;
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
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.ObjectWriter; 
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
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

	//Get the pantry of the user
	@GET
	@Path("/{access_token}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPantry( @PathParam("access_token") final String accessToken ){
		Response resp;

		try{
			//Respond with the user's pantry
			resp = Response.ok(PantryDatabase.getPantry(accessToken)).build(); 
		} catch(PantryException e){
			resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to retrieve user's pantry. Failed with message: "+e.getMessage()).build();
			
		}

		return resp;
	}
	
	@POST
	@Path("/{access_token}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPantry( @PathParam("access_token") final String accessToken, Pantry userPantry ){
		String resp = "";
		Respond respond = new Respond();
		ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

		try{	
			respond.setCode(200);
			respond.setStatus("Success");
			respond.setMsg(writer.writeValueAsString(userPantry));
		} catch (JsonProcessingException e){
			respond.setCode(500);
			respond.setStatus("Error");
			respond.setMsg(e.getMessage());	
		}	

		//create Response
		try{
			resp = writer.writeValueAsString(respond);
		} catch (JsonProcessingException e){
			respond.setMsg(e.getMessage());
			resp = respond.toString();
		}

		return Response.ok(resp).build();
	}

	//Add ingredient to the user's pantry
	@POST
	@Path("/{access_token}/ingredient")
	@Consumes(MediaType.APPLICATION_JSON) //An ingredient object matching the Ingredient class
	@Produces(MediaType.APPLICATION_JSON) //A message object with status included
	public Response addIngredient(@PathParam("access_token") final String accessToken, Ingredient newIngred){
		String resp = "";
		Respond respond = new Respond();
		ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try{
			//Add ingredient to user's pantry in the database
			PantryDatabase.addIngredient(accessToken, newIngred);

			//Set respond values
			respond.setCode(200);
			respond.setStatus("Success");
			respond.setMsg("Added ingredient");

		}catch(PantryException e){
			respond.setCode(500);
			respond.setStatus("Error");
			respond.setMsg(e.getMessage());
		}

		//create Response
		try{
			resp = writer.writeValueAsString(respond);
		} catch (JsonProcessingException e){
			respond.setMsg(e.getMessage());
			resp = respond.toString();
		}

		return Response.ok(resp).build();
	}

	//Update ingredient in user's pantry
	@PUT
	@Path("/{access_token}/ingredient/{ingredient_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateIngredient(@PathParam("access_token") final String accessToken, @PathParam("ingredient_id") final String ingredID, Ingredient updatedIngred){

		return Response.ok("{\"status\":\"finished\"}").build(); 
	}
	
	
	//Delete ingredient in user's pantry
	@DELETE
	@Path("/{access_token}/ingredient/{ingredient_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteIngredient(@PathParam("access_token") final String accessToken, @PathParam("ingredient_id") final String ingredID) {
		PantryDatabase.rmIngredient(accessToken, ingredID);
		
		return Response.ok("{\"status\":\"finished\"}").build(); 
	}

	//TEST post to create a pantry
	@GET
	@Path("testAddIngredient")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAddIngredient( @QueryParam("user") String user, @QueryParam("ingredient") String ingredient ) throws Exception{
		//This will be the response string
		String testAddIngredientResponse;

		//Create a client and a request entity using the pantry query parameter as the JSON body
		HttpClient httpclient = HttpClientBuilder.create().build();
		StringEntity requestEntity = new StringEntity(
		    ingredient,
		    ContentType.APPLICATION_JSON);

		//setup post request to backend to the backend
		HttpPost postMethod = new HttpPost("http://localhost:9080/Pantry/pantry/" + user + "/ingredient");
		postMethod.setEntity(requestEntity);

		//Submit post and receive response back
		HttpResponse rawResponse;
		try{
			rawResponse = httpclient.execute(postMethod);
			HttpEntity responseEntity = rawResponse.getEntity();

			//Make sure a response entity was sent
			if(responseEntity == null){
				testAddIngredientResponse = "No entity found";
			}else{
				testAddIngredientResponse = EntityUtils.toString(responseEntity);
			}
		} catch (Exception e) {
			testAddIngredientResponse = "{\"status\":\"failed while executing POST request to the backend to insert the pantry\", \"error\":\""+e.getMessage()+"\"}";
		}

		//Simply forward response from the post request
		return Response.ok(testAddIngredientResponse).build();
	}
	@POST
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
	}	

	//TEST post to create a pantry
	@POST
	@Path("testRemoveIngredient")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testRemoveIngredient( @QueryParam("user") String user, Ingredient ingredient) throws Exception{
		//This will be the response string
		/*String testRemoveIngredientResponse;

		//Create a client and a request entity using the pantry query parameter as the JSON body
		HttpClient httpclient = HttpClientBuilder.create().build();
		StringEntity requestEntity = new StringEntity(
		    ingredient,
		    ContentType.APPLICATION_JSON);

		//setup post request to backend to the backend
		HttpPost postMethod = new HttpPost("http://localhost:9080/Pantry/pantry/" + user + "/ingredient/" + ingredient );
		postMethod.setEntity(requestEntity);

		//Submit post and receive response back
		HttpResponse rawResponse;
		try{
			rawResponse = httpclient.execute(postMethod);
			HttpEntity responseEntity = rawResponse.getEntity();

			//Make sure a response entity was sent
			if(responseEntity == null){
				testRemoveIngredientResponse = "No entity found";
			}else{
				testRemoveIngredientResponse = EntityUtils.toString(responseEntity);
			}
		} catch (Exception e) {
			testRemoveIngredientResponse = "{\"status\":\"failed while executing POST request to the backend to insert the pantry\", \"error\":\""+e.getMessage()+"\"}";
		}

		//Simply forward response from the post request
		return Response.ok(testRemoveIngredientResponse).build();*/
		return Response.ok(ingredient).build();
	}
}
