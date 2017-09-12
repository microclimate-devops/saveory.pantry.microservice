package application.rest.v1.JsonClasses;

/**************************/
//External Libs
import com.fasterxml.jackson.annotation.JsonIgnore;
/**************************/

public class Ingredient{
	private String item;
	private int quantity;
	private String unit;
	private String expiration;
	private boolean refrigerated;

	public Ingredient(){
		this.item = "";
		this.quantity = 0;
		this.unit = "";
		this.expiration = "";
		this.refrigerated = false;
	}

	public Ingredient(String item, int quantity, String unit, String expiration, boolean refrigerated){
		this.item = item;
		this.quantity = quantity;
		this.unit = unit;
		this.expiration = expiration;
		this.refrigerated = refrigerated;
	}


	public String objectIdentifier(){
		return this.getItem();
	}

	public String getItem(){
		return this.item;
	}

	public void setItem(String item){
		this.item = item;
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

	public boolean getRefrigerated(){
		return this.refrigerated;
	}

	public void setRefrigerated(boolean refrigerated){
		this.refrigerated = refrigerated;
	}

/*
	@Override
	public String toString(){
		return "{\"item\":\""+this.getItem()+"\", \"quantity\":"+this.getQuantity()+", \"unit\":\""+this.getUnit()+"\", \"expiration\":\""+this.getExpiration()+"\"}";
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
			return (this.getItem().toLowerCase().equals(i.getItem().toLowerCase()) && this.getQuantity() == i.getQuantity() && this.getUnit().equals(i.getUnit()) && this.getExpiration().equals(i.getExpiration()));
		}else if(o instanceof String) {
			String i = (String) o;
			return this.getItem().toLowerCase().equals(i.toLowerCase());
		}else {
			return false;
		}
		 
	}

}
