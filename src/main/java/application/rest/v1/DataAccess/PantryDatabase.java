package application.rest.v1.DataAccess;

/**************************/
//Custom Exceptions
import application.rest.v1.CustomExceptions.PantryException;
/**************************/

/**************************/
//JSON classes
import application.rest.v1.JsonClasses.Ingredient;
import application.rest.v1.JsonClasses.Pantry;
/**************************/

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
		//Get the pantries collection and search for pantry
		JacksonDBCollection<Pantry, String> coll = PantryDatabase.getJacksonCollection();
		//DBCursor<Pantry> cursor = coll.find().is("user", user);
		Pantry userPantry = coll.findOne(DBQuery.is("user", user));
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

			//Update to an empty object if null
			if(userPantry == null){
				userPantry = new Pantry();
			}
			
			foundPantry = writer.writeValueAsString(userPantry);
		} catch (JsonProcessingException e){
			throw new PantryException("Could not process database results while trying to find pantry");
		}

		return foundPantry;
		
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

		//Get pantry collection and insert new pantry
		JacksonDBCollection<Pantry, String> coll = PantryDatabase.getJacksonCollection();
		WriteResult<Pantry, String> result = coll.insert(userPantry);	

		//Check that the pantry was added to the database
		try{
			if(!getPantryObject(user).getPantry().get(0).equals(ingred)){
				throw new PantryException("The user's pantry in the database does not match the one just created");
			}
		} catch(Exception e){
			throw new PantryException("Could not retrieve the user's new pantry from the database. Error message: "+e.getMessage());
		}
	}

	//Update the user's pantry with a new version
	public static void updatePantry(Pantry newPantry){
		//Get pantry collection and update the correct entry
		JacksonDBCollection<Pantry, String> coll = PantryDatabase.getJacksonCollection();
		DBUpdate.Builder builder = new DBUpdate.Builder();

		//Updated result
		//Pantry updatedPantry = coll.findAndModify(DBQuery)

	}

	public static void removePantry(String user){
		//Get pantries collection
		MongoCollection<Document> collection = PantryDatabase.getMongoCollection();

		//Delete the pantry matching the user
		collection.deleteOne(eq("user", user));
	}  	

	//Add an ingredient to a user's panty
	public static void addIngredient(String user, Ingredient ingred) throws PantryException {	
		//Get the user's pantry
		Pantry userPantry = PantryDatabase.getPantryObject(user);

		//If the pantry was not found, create a new one 
		if(userPantry == null){
			PantryDatabase.createPantry(user, ingred);
		}else{
			//Add the ingredient to the user's pantry
			//userPantry.addIngredient(ingred)
			//PantryDatabase.updatePantry(userPantry);
		}
		/*Document matchedDoc = new Document();

		//Flag for whether the ingredient is a duplicate
		boolean duplicate = false;

		//Get the pantries collection
		MongoCollection<Document> collection = PantryDatabase.getMongoCollection();

		//Find documents that match the user and prepare to iterate through them
		MongoCursor<Document> iterator = collection.find(eq("user", user)).iterator();

		//Convert the ingredient into json
		JsonReader reader = Json.createReader(new StringReader(jsonIngredient));
		JsonObject ingredientJson = reader.readObject();
		reader.close();

		//Save the name of the ingredient that should be added
		String ingredientName = ingredientJson.getString("item");

		//Converting the ingredient string to a DBObject
		DBObject ingredient = (DBObject)JSON.parse(jsonIngredient);

		while(iterator.hasNext()){
			Document doc = iterator.next();

			//Save the id
			ObjectId id = doc.getObjectId("_id");

			//Convert the document into json
			JsonReader reader2 = Json.createReader(new StringReader(doc.toJson()));
			JsonObject jsonOutput = reader2.readObject();
			reader2.close();

			//Getting the array of the ingredients
			JsonArray pantryJsonArray = jsonOutput.getJsonArray("pantry");
			BasicDBList newPantryList = new BasicDBList();

			for(int i = 0; i <  pantryJsonArray.size(); i++){
				//Create a new object
				DBObject newPantry = new BasicDBObject();
	
				//Save the ingredient information
				String item = pantryJsonArray.getJsonObject(i).getString("item");
				int qty = pantryJsonArray.getJsonObject(i).getInt("qty");
				String qtyUnit = pantryJsonArray.getJsonObject(i).getString("qtyUnit");
				String expDate = pantryJsonArray.getJsonObject(i).getString("expDate");

				if(item.equals(ingredientName))
					duplicate = true;

				//Insert the saved information into the new object
				newPantry.put("item", item);
				newPantry.put("qty", qty);
				newPantry.put("qtyUnit", qtyUnit);
				newPantry.put("expDate", expDate);

				//Add the ingredient object to the new ingerdient list
				newPantryList.add(newPantry);
			}

			//Add the new ingredient to the list
			newPantryList.add(ingredient);
		
			//Create a new document with the saved information and new pantry
			Document updatedPantry = new Document();
			updatedPantry.put("_id", id);
			updatedPantry.put("user", user);
			updatedPantry.put("pantry", newPantryList);
			
			if(!duplicate){
				//Update the collection if the ingredient is not a duplicate
				collection.findOneAndReplace(eq("user", user), updatedPantry);
			}
		}*/
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
				int qty = pantryJsonArray.getJsonObject(i).getInt("qty");
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
