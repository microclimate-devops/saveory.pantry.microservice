package application.rest.v1.JsonClasses;

/**************************/
//External Libs
import com.fasterxml.jackson.annotation.JsonIgnore;
/**************************/

public class Ingredient{
	private String item;
	private int qty;
	private String qtyUnit;
	private String expDate;
	private String contains;
	/*private String hasEnough;*/

	public Ingredient(){
		this.item = "";
		this.qty = 0;
		this.qtyUnit = "";
		this.expDate = "";
		this.contains = "false";
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

	public int getQty(){
		return this.qty;
	}

	public void setQty(int qty){
		this.qty = qty;
	}

	public String getQtyUnit(){
		return this.qtyUnit;
	}

	public void setQtyUnit(String qtyUnit){
		this.qtyUnit = qtyUnit;
	}

	public String getExpDate(){
		return this.expDate;
	}

	public void setExpDate(String expDate){
		this.expDate = expDate;
	}

	public String getContains(){
		return this.contains;
	}

	public void setContains(String contains){
		this.contains = contains;
	}

/*
	@Override
	public String toString(){
		return "{\"item\":\""+this.getItem()+"\", \"qty\":"+this.getQty()+", \"qtyUnit\":\""+this.getQtyUnit()+"\", \"expDate\":\""+this.getExpDate()+"\"}";
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
			return (this.getItem().toLowerCase().equals(i.getItem().toLowerCase()) && this.getQty() == i.getQty() && this.getQtyUnit().equals(i.getQtyUnit()) && this.getExpDate().equals(i.getExpDate()));
		}else if(o instanceof String) {
			String i = (String) o;
			return this.getItem().toLowerCase().equals(i.toLowerCase());
		}else {
			return false;
		}
		 
	}

}
