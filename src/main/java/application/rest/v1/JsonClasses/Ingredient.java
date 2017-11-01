package application.rest.v1.JsonClasses;

/**************************/
//External Libs
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**************************/

public class Ingredient{
	private String ingredient;
	private int quantity;
	private String unit;
	private String expiration;
	private String location;

	public Ingredient(){
		this.ingredient = "";
		this.quantity = 0;
		this.unit = "";
		this.expiration = "";
		this.location = "";
	}

	public Ingredient(String ingredient, int quantity, String unit, String expiration, String location){
		this.ingredient = ingredient;
		this.quantity = quantity;
		this.unit = unit;
		this.expiration = expiration;
		this.location = location;
	}
	
	public static String exposeIdFieldName() {
		return "{\"id\":\"ingredient\"}";
	}

	//Give a list of ingredient fields in the order they should appear
	public static String exposeFields(){
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("\"ingredient\"");
		fields.add("\"quantity\"");
		fields.add("\"unit\"");
		fields.add("\"expiration\"");
		fields.add("\"location\"");
		return fields.toString();
	}

	public static String exposeFieldTypes(){
		ArrayList<String> fieldTypes = new ArrayList<String>();
		fieldTypes.add("\"text\"");
		fieldTypes.add("\"number\"");
		fieldTypes.add("\"text\"");
		fieldTypes.add("\"date\"");
		fieldTypes.add("\"text\"");
		return fieldTypes.toString();
	}

	//Send a list of valid location options
	public static String exposeLocationOptions(){
		ArrayList<String> locations = new ArrayList<String>();
		locations.add("\"Pantry\"");
		locations.add("\"Refrigerator\"");
		return locations.toString();
	}

	//return a list of booleans indicating which fields, according to exposeFields array, are editable
	public static String exposeEditableFields(){
		ArrayList<String> editable = new ArrayList<String>();
		editable.add("false");
		editable.add("true");
		editable.add("true");
		editable.add("true");
		editable.add("true");
		return editable.toString();
	}

	public String objectIdentifier(){
		return this.getIngredient();
	}

	public String getIngredient(){
		return this.ingredient;
	}

	public void setIngredient(String ingredient){
		this.ingredient = ingredient;
	}

	public int getQuantity(){
		return this.quantity;
	}

	public void setQuantity(int quantity){
		this.quantity = quantity;
	}

	public String getUnit(){
		return this.unit;
	}

	public void setUnit(String unit){
		this.unit = unit;
	}

	public String getExpiration(){
		return this.expiration;
	}

	public void setExpiration(String expiration){
		this.expiration = expiration;
	}

	public String getLocation(){
		return this.location;
	}

	public void setLocation(String location){
		this.location = location;
	}

/*
	@Override
	public String toString(){
		return "{\"ingredient\":\""+this.getIngredient()+"\", \"quantity\":"+this.getQuantity()+", \"unit\":\""+this.getUnit()+"\", \"expiration\":\""+this.getExpiration()+"\"}";
	}*/

	@Override
	public boolean equals(Object o){
		// If the object is compared with itself then return true  
		if (o == this) {
		    return true;
		}
	 
		/* Check if o is an instance of Ingredient or not
		   If it's a string will directly compare indentifier with that string
		  "null instanceof [type]" also returns false */
		if (o instanceof Ingredient) {
			// typecast o to Ingredient so that we can compare data members 
			Ingredient i = (Ingredient) o;
			//Compare the identifiers
			return (this.getIngredient().toLowerCase().equals(i.getIngredient().toLowerCase()) && this.getQuantity() == i.getQuantity() && this.getUnit().equals(i.getUnit()) && this.getExpiration().equals(i.getExpiration()));
		}else if(o instanceof String) {
			String i = (String) o;
			return this.getIngredient().toLowerCase().equals(i.toLowerCase());
		}else {
			return false;
		}
		 
	}

}
