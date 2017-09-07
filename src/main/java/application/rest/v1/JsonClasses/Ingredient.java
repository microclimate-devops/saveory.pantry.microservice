package application.rest.v1.JsonClasses;

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
	 
		/* Check if o is an instance of Complex or not
		  "null instanceof [type]" also returns false */
		if (!(o instanceof Ingredient)) {
		    return false;
		}
		 
		// typecast o to Ingredient so that we can compare data members 
		Ingredient i = (Ingredient) o;
		
		//Compare the identifiers
		return this.item == i.item;
	}
}
