package application.rest.v1.UnitConversion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;

import application.rest.v1.CustomExceptions.*;

public class Units {
	
	//HashMap containing conversions from different Volumetric Units to FL Oz
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
	
	//HashMap containing conversions from different Weight Units to Gram
	private static HashMap<String, Double> toGram = new HashMap<String, Double>(){
		{
			put("oz", (double) 28);
			put("lbs", (double) 454);
			put("gram", (double) 1);
		}
	};
	
	public static Set<String> getUnits() throws IOException{
		//A HashSet is created to hold the keyset of both HashMaps
		HashSet<String> units = new HashSet<String>();
		//Volumetric units are added into the hashset
		units.addAll(toFlOz.keySet());
		//Weight units are added into the hashset
		units.addAll(toGram.keySet());
		//Hashset is returned
		return units;
	}
	
	
	
	public static Double convert(String unit1, String unit2, Double value) throws UnexpectedUnitException{
		//Factors will be obtained to do the required conversions
		double factor1;
		double factor2;
		
		//If unit2 is found in toFlOz and unit1 is not then the user is converting from volume to weight (not allowed)
		if(toFlOz.get(unit2) != null && toFlOz.get(unit1) == null)
			throw new UnexpectedUnitException(unit1 + " was not found in our unit conversion table.");
		
		//If unit2 is found in toFlOz and unit1 is not then the user is converting from weight to volume (not allowed)
		else if(toGram.get(unit2) != null && toGram.get(unit1) == null)
			throw new UnexpectedUnitException(unit2 + " was not found in our unit conversion table.");
		
		//If both units are found in the Volumetric Units Hashmap
		else if(toFlOz.get(unit2) != null && toFlOz.get(unit1) != null){
			factor1 = toFlOz.get(unit1);
			factor2 = toFlOz.get(unit2);
		}
		//If both units are found in the Weight Units Hashmap
		else{
			factor1 = toGram.get(unit1);
			factor2 = toGram.get(unit2);
		}
		
		//First we convert to the "universal" unit
		double conversion = value * factor1;
		
		//Then we convert to the unit we want
		conversion = conversion / factor2;
		
		//Resulting conversion is returned
		return conversion;
	}
}
