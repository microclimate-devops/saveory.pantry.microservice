package application.rest.v1.JsonClasses;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Ingredients{
	private Ingredient[] ingredientsList;

	public Ingredients(){
		ingredientsList = null;
	}

	public void setIngredients(String ingredients) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		this.ingredientsList = mapper.readValue(ingredients, Ingredient[].class); 
	}

	public Ingredient[] getIngredients(){
		return this.ingredientsList;
	}
	
	@Override
	public String toString(){
		String ingredients = "[";
		for(Ingredient ingred : this.getIngredients()){
			ingredients += "{},"; 
		}
		
		//cut off tailing comma
		ingredients = ingredients.substring(0, ingredients.length());
		return ingredients;
	}
}
