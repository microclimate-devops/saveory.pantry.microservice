package application.rest.v1.UnitConversion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;

import application.rest.v1.CustomExceptions.*;

public class Units {
	private static HashMap<String, Double> toFlOz = new HashMap<String, Double>(){
		{
			put("drop", (double)1/(double)576);
			put("pinch", (double)1/(double)128);
			put("tsp", (double)1/(double)6);
			put("tbsp", (double)1/(double)2);
			put("cup", (double) (8));
			put("pint", (double) (16));
			put("quart", (double) (32));
			put("gallon", (double) (128));
		}
	};
	
	private static HashMap<String, Double> toGram = new HashMap<String, Double>(){
		{
			put("oz", (double) 28);
			put("lbs", (double) 454);
			put("gram", (double) 1);
		}
	};
	
//	public static HashMap<String, Double> getConversionMap(){
//		return toFlOz;
//	}
	
	public static Set<String> getUnits() throws IOException{
		Set<String> units = toFlOz.keySet();
		units.addAll(toGram.keySet());
		return units;
	}
	
	
	
	public static Double convert(String unit1, String unit2, Double value) throws UnexpectedUnitException{
		
		double factor1;
		double factor2;
		if(toFlOz.get(unit2) != null && toFlOz.get(unit1) == null)
			throw new UnexpectedUnitException(unit1 + " was not found in our unit conversion table.");
		else if(toGram.get(unit2) != null && toGram.get(unit1) == null)
			throw new UnexpectedUnitException(unit2 + " was not found in our unit conversion table.");
		else if(toFlOz.get(unit2) != null && toFlOz.get(unit1) != null){
			factor1 = toFlOz.get(unit1);
			factor2 = toFlOz.get(unit2);
		}
		else{
			factor1 = toGram.get(unit1);
			factor2 = toGram.get(unit2);
		}
			
		double conversion = value * factor1;
		conversion = conversion / factor2;
		return conversion;
		
		
		
	}
}
