package application.rest.v1.DataAccess;

/**************************/
//Custom Exceptions
import application.rest.v1.CustomExceptions.PantryException;
/**************************/
import application.rest.v1.CustomExceptions.UnexpectedUnitException;
/**************************/
//JSON classes
import application.rest.v1.JsonClasses.Ingredient;
import application.rest.v1.JsonClasses.Pantry;
/**************************/
import application.rest.v1.UnitConversion.Units;

/**************************/
//External Libs
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonReader;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.ObjectWriter; 
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mongojack.DBCursor;
import org.mongojack.DBUpdate;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.types.ObjectId;
/**************************/



public class PantryDatabase {
	private static MongoClient mongo_client;
	private static String db_name = "saveory_app";
	private static String db_client_uri = "mongodb://sapphires:saveoryArmory@sapphires-db.rtp.raleigh.ibm.com/saveory_app";
	private static String collection_name = "pantries";
		
	//Access the database to return the user's pantry in JSON
	public static Pantry getPantryObject(String user){
		Pantry userPantry = null;

		//Get the pantries collection and search for pantry
		JacksonDBCollection<Pantry, String> coll = PantryDatabase.getJacksonCollection();
		//DBCursor<Pantry> cursor = coll.find().is("user", user);
		userPantry = coll.findOne(DBQuery.is("user", user));
		//assign first search result to Pantry object
		/*if(cursor.hasNext()){
			userPantry = cursor.next();
		}*/


		return userPantry;
	}

	public static String getPantry(String user) throws PantryException{
		String foundPantry = "";
		ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

		//Attempt to serialize the result
		try{
			Pantry userPantry = PantryDatabase.getPantryObject(user);	

			if(userPantry != null){
				foundPantry = writer.writeValueAsString(userPantry);
			}
		} catch (JsonProcessingException e){
			throw new PantryException("Could not process database results while trying to find pantry");
		} catch (IOException e){
			throw new PantryException("Unkown exception. Error message: "+e.getMessage());
		} finally {
			//If the pantry string is empty, throw exception to indicate the pantry could not be found
			if(foundPantry == ""){
				throw new PantryException("The user's pantry could not be found in the database");	
			}
		}

		return foundPantry;
		
	}
	
	//Create a list of ingredient names in the user's pantry
	public static String getPantryIngredientNames(String user) throws PantryException{
		ArrayList<String> ingredients = new ArrayList<String>();
		try {
			for(Ingredient ingred : getPantryObject(user).getPantry()) {
				ingredients.add("\""+ingred.getIngredient()+"\"");
			}
		} catch (Exception e) {
			throw new PantryException("Could not get the user's list of ingredients");
		}
		return ingredients.toString();
	}
	
	//Create a user's pantry from a JSON string
	public static void createPantry(String user, String jsonPantry){
		//Get pantries collection
		MongoCollection<Document> collection = PantryDatabase.getMongoCollection();

		//Create mongo document out of json
		Document newPantry = Document.parse(jsonPantry);
		
		//Insert document into collection
		collection.insertOne(newPantry);
	}

	//Create a user's pantry with their first Ingredient
	public static void createPantry(String user, Ingredient ingred) throws PantryException{
		Pantry userPantry = new Pantry(user, ingred);
		PantryDatabase.addPantry(userPantry);
	}

	//Add pantry to database
	public static void addPantry(Pantry userPantry) throws PantryException{
		//Get pantry collection and insert new pantry
		JacksonDBCollection<Pantry, String> coll = PantryDatabase.getJacksonCollection();
		WriteResult<Pantry, String> result = coll.insert(userPantry);	

		//Check that the pantry was added to the database
		if(!userPantry.equals(PantryDatabase.getPantryObject(userPantry.getUser()))){
			throw new PantryException("The user's pantry in the database does not match the one just created");
		}

	}

	//Deletes the old version and replaces it with new
	public static void replacePantry(Pantry newPantry) throws PantryException{
		PantryDatabase.removePantry(newPantry.getUser());
		PantryDatabase.addPantry(newPantry);
	}

	public static void removePantry(String user) throws PantryException{
		//Get pantries collection
		MongoCollection<Document> collection = PantryDatabase.getMongoCollection();

		//Delete the pantry matching the user
		collection.deleteOne(eq("user", user));

		//Make sure the pantry is no longer there
		try{
			if(PantryDatabase.getPantryObject(user) != null){
				throw new PantryException("Attempted to delete the pantry but it was found to still be in the database");
			}
		} catch(Exception e){
			throw new PantryException("Could not retrieve the user's pantry from the database. Error message: "+e.getMessage());
		}
	}  	

	//Add an ingredient to a user's panty
	public static void addIngredient(String user, Ingredient ingred) throws PantryException {
		//Get the user's pantry
		Pantry userPantry = PantryDatabase.getPantryObject(user);

		//If the pantry was not found, create a new one 
		if(userPantry == null){
			PantryDatabase.createPantry(user, ingred);
		}else{
			//Make sure the ingredient is not already in the pantry
			if(userPantry.hasIngredient(ingred)){
				throw new PantryException("The ingredient is already in the user's pantry");
			}
			//Add the ingredient to the user's pantry
			userPantry.addIngredient(ingred);
			PantryDatabase.replacePantry(userPantry);
		}

	}

	public static void updateIngredient(String user, Ingredient updatedIngred) throws PantryException{
		//Get the user's pantry
		Pantry userPantry = PantryDatabase.getPantryObject(user);
		if(userPantry == null){
			throw new PantryException("The user's pantry could not be found in the database");
		}

		//remove the old ingredient
		userPantry.deleteIngredient(updatedIngred.objectIdentifier());

		//add the new ingredient
		userPantry.addIngredient(updatedIngred);

		//update pantry
		replacePantry(userPantry);
		
		//make sure ingredient was updated
		Ingredient pantryIngredient = PantryDatabase.getPantryObject(user).getIngredient(updatedIngred.objectIdentifier());
		if(!updatedIngred.equals(pantryIngredient)){
			throw new PantryException("The ingredient update did not take to the database "
					+ "Pantry Ingredient"
					+ "Name: " + pantryIngredient.getIngredient() 
					+ "Quantity: " + pantryIngredient.getQuantity()
					+ "Unit: " + pantryIngredient.getUnit()
					+ "Location: " + pantryIngredient.getLocation()
					+ "Expiration: " + pantryIngredient.getExpiration()
					+ "Updated Ingredient"
					+ "Name: " + updatedIngred.getIngredient() 
					+ "Quantity: " + updatedIngred.getQuantity()
					+ "Unit: " + updatedIngred.getUnit()
					+ "Location: " + updatedIngred.getLocation()
					+ "Expiration: " + updatedIngred.getExpiration());
		}	


	}
	
	public static ArrayList<Ingredient> autoUpdateIngredient(String user, List<Ingredient> updatedIngredientList) throws PantryException{
		//Get the user's pantry
		Pantry userPantry = PantryDatabase.getPantryObject(user);
		if(userPantry == null){
			throw new PantryException("The user's pantry could not be found in the database");
		}
		
		String pantryUnit;
		String recipeUnit;
		Double conversion;
		Ingredient pantryIngredient;
		ArrayList<Ingredient> failedIngredients = new ArrayList<>();
		
		for(Ingredient currentIngredient : updatedIngredientList){
			//Get the units for both the recipe and the pantry ingredient
			pantryIngredient = userPantry.getIngredient(currentIngredient.getIngredient());
			pantryUnit = pantryIngredient.getUnit();
			recipeUnit = currentIngredient.getUnit();
			
			try{
				if(pantryUnit.equals(recipeUnit))
					conversion = currentIngredient.getQuantity();
				else
					conversion = Units.convert(recipeUnit, pantryUnit, currentIngredient.getQuantity());
				
				pantryIngredient.setQuantity(pantryIngredient.getQuantity() - conversion);
				//Double.pantryIngredient.getQuantity();
			
				
				if(pantryIngredient.getQuantity() < 0){
//					removeIngredient(user, currentIngredient.getIngredient());
					pantryIngredient.setQuantity(0);
				}
				
				updateIngredient(user, pantryIngredient);
			}
			
			catch(UnexpectedUnitException e){
				
				failedIngredients.add(pantryIngredient);
			}
			
				
		}
		
		return failedIngredients;
	}
//		//remove the old ingredient
//		userPantry.deleteIngredient(updatedIngred.objectIdentifier());
//
//		//add the new ingredient
//		userPantry.addIngredient(updatedIngred);
//
//		//update pantry
//		replacePantry(userPantry);

		//make sure ingredient was updated
//		if(!updatedIngredientList.equals(PantryDatabase.getPantryObject(user).getIngredient(updatedIngred.objectIdentifier()))){
//			throw new PantryException("The ingredient update did not take to the database");
//		}	

	public static void manualUpdateIngredient(String user, List<Ingredient> updatedIngredientList) throws PantryException{
		//Get the user's pantry
		Ingredient pantryIngredient;
		Pantry userPantry = PantryDatabase.getPantryObject(user);
		if(userPantry == null){
			throw new PantryException("The user's pantry could not be found in the database");
		}
		
		for(Ingredient current : updatedIngredientList){
			if(current.getQuantity() == 0)
				removeIngredient(user, current.getIngredient());
			else{ 
				pantryIngredient = userPantry.getIngredient(current.getIngredient());
				pantryIngredient.setQuantity(current.getQuantity());
				updateIngredient(user, pantryIngredient);
			}
		}
				
	}

	public static void removeIngredient(String user, String ingredID) throws PantryException{
		//Get the user's pantry
		Pantry userPantry = PantryDatabase.getPantryObject(user);
		if(userPantry == null){
			throw new PantryException("The user's pantry could not be found in the database");
		}

		//Remove the ingredient
		userPantry.deleteIngredient(ingredID);

		//Update pantry
		replacePantry(userPantry);	

		//Make sure the ingredient was deleted
		if(PantryDatabase.getPantryObject(user).hasIngredient(ingredID)){
			throw new PantryException("After attempt to delete the ingredient, it was found in the database");
		}
	}


//	//Combine two jsonArrays into one
//	private static JsonArray concatArray(JsonArray... arrs)
//		throws Exception {
//	    JsonArrayBuilder result = Json.createArrayBuilder();
//	    for (JsonArray arr : arrs) {
//		for (int i = 0; i < arr.size(); i++) {
//		    result.add(arr.get(i));
//		}
//	    }
//	    return result.build();
//	}

//	public static void addIngredient(String user, String ingredient) throws Exception{
//		System.out.println("Entered processing");
//		//Get pantries collection
//		MongoCollection<Document> collection = PantryDatabase.getMongoCollection();
//
//		//Attempt to get user's pantry
//		String pantryResultsString = PantryDatabase.getPantry(user);
//		System.out.println("pantry: "+pantryResultsString);
//		JsonReader pantryJsonReader = Json.createReader(new StringReader(pantryResultsString));
//		JsonReader ingredientJsonReader = Json.createReader(new StringReader(ingredient));
//		JsonArray pantryResults = pantryJsonReader.readArray();
//		JsonObject pantryObj = Json.createObjectBuilder().build();
//		JsonArray userPantry = Json.createArrayBuilder().add(ingredientJsonReader.readObject()).build();
//
//		//if the array is not empty add the ingredient
//		if(pantryResults != null && pantryResults.size() > 0){
//			System.out.println("pantryResults: "+pantryResults.toString());
//			//Get pantry from results
//			pantryObj = pantryResults.getJsonObject(0);
//			//Combine old and new ingredients
//			userPantry = PantryDatabase.concatArray(pantryObj.getJsonArray("pantry"), userPantry);
//			//Put the pantry back into pantryObj
//			pantryObj.put("pantryResults", pantryResults);
//
//			//Update the db with the new pantry document
//			Document pantryDoc = Document.parse(pantryObj.toString());
//			collection.updateOne(eq("user", user), pantryDoc);
//			System.out.println("new pantry: "+pantryObj.toString());
//		}else{ //otherwise create a new pantry for the user with its first ingredient
//			Pantry.createPantry(user, userPantry.toString());
//		}
//	}
	
	//Remove an ingredient from a user's pantry
	public static void rmIngredient(String user, String ingredient){
		Document matchedDoc = new Document();

		//Get the pantries collection
		MongoCollection<Document> collection = PantryDatabase.getMongoCollection();
	
		//Find documents that match the user and prepare to iterate through them
		MongoCursor<Document> iterator = collection.find(eq("user", user)).iterator();

		while(iterator.hasNext()) {
			Document doc = iterator.next();
		
			//Save the id	
			ObjectId id = doc.getObjectId("_id");

			//Convert the document into Json
			JsonReader reader = Json.createReader(new StringReader(doc.toJson()));
			JsonObject jsonOutput = reader.readObject();
			reader.close();

			//Getting the array of the ingredients
			JsonArray pantryJsonArray = jsonOutput.getJsonArray("pantry");
			BasicDBList newPantryList = new BasicDBList();

			for(int i = 0; i < pantryJsonArray.size(); i++){
				//Create a new object
				DBObject newPantry = new BasicDBObject();

				//Save the ingredient information
				String item = pantryJsonArray.getJsonObject(i).getString("item");
				double qty = pantryJsonArray.getJsonObject(i).getInt("qty");
				String qtyUnit = pantryJsonArray.getJsonObject(i).getString("qtyUnit");
				String expDate = pantryJsonArray.getJsonObject(i).getString("expDate");

				//Only copy the ingredient to the new Pantry if it;s not the ingredient to be removed
				if(!ingredient.equals(item)){
					newPantry.put("item", item);
					newPantry.put("qty", qty);
					newPantry.put("qtyUnit", qtyUnit);
					newPantry.put("expDate", expDate);
				}
			}

			//Add the new ingerdient to the list
			newPantryList.add(ingredient);

			//Create a new document with the saved information and new pantry
			Document updatedPantry = new Document();
			updatedPantry.put("_id", id);
			updatedPantry.put("user", user);
			updatedPantry.put("pantry", newPantryList);
		}
	}
	
	private static void setMongoClient(MongoClientURI connection_string){
		PantryDatabase.mongo_client = new MongoClient(connection_string);
	}

	//Setup connection to database
	private static MongoClient getMongoClient(){
		//Check if the client already exists
		if(PantryDatabase.mongo_client == null){
			//Create a new client connection if none exists	
			MongoClientURI connectionString = new MongoClientURI(PantryDatabase.getDbClientUri());
			setMongoClient(connectionString);
		}

		return PantryDatabase.mongo_client;

	}
	
	public static String getDbClientUri(){
		return PantryDatabase.db_client_uri;
	}
	
	public static String getDbName(){
		return PantryDatabase.db_name;
	}
	
	public static String getMongoCollectionName(){
		return PantryDatabase.collection_name;
	}

	public static MongoCollection<Document> getMongoCollection(){	
		MongoDatabase database = PantryDatabase.getMongoClient().getDatabase(PantryDatabase.getDbName());
		return database.getCollection(PantryDatabase.getMongoCollectionName());
	}
	
	public static DBCollection getCollection(){	
		DB database = PantryDatabase.getMongoClient().getDB(PantryDatabase.getDbName());
		return database.getCollection(PantryDatabase.getMongoCollectionName());
	}

	public static JacksonDBCollection<Pantry, String> getJacksonCollection(){
		//Get the pantries collection and search for pantry
		DBCollection collection = PantryDatabase.getCollection();
		return JacksonDBCollection.wrap(collection, Pantry.class, String.class);

	}
}
