package application.rest.v1.UnitConversion;

import java.util.ArrayList;
import java.util.HashMap;
import application.rest.v1.CustomExceptions.*;

public class Units {
	private static HashMap<String, Double> toFlOz = new HashMap<String, Double>(){
		{
			put("drop", (double) (1/576));
			put("pinch", (double) (1/128));
			put("tsp", (double) (1/6));
			put("tbsp", (double) (1/2));
			put("cup", (double) (8));
			put("pint", (double) (16));
			put("quart", (double) (32));
			put("gallon", (double) (128));
		}
	};
	
//	public static HashMap<String, Double> getConversionMap(){
//		return toFlOz;
//	}
	
	public static ArrayList<String> getUnits(){
		return (ArrayList<String>) toFlOz.keySet();
	}
	
	public static Double convert(String unit1, String unit2, Double value) throws UnexpectedUnitException{
		
		if(toFlOz.get(unit1) == null)
			throw new UnexpectedUnitException(unit1 + " was not found in our unit conversion table.");
		if(toFlOz.get(unit2) == null)
			throw new UnexpectedUnitException(unit2 + " was not found in our unit conversion table.");
		
		Double conversion = (Double) value * toFlOz.get(unit1);
		conversion = (Double) (conversion / toFlOz.get(unit2));
		return conversion;
		
		
		
	}
}
