package com.example.cookbookapp;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class Ingredients {

	public ArrayList<Ingredient> mIngredients;

	
	Ingredients () {
		mIngredients = new ArrayList<Ingredient>();
	}
	
	
	public void addIngredient (Float quantity, String units, String object, String unitsAlt, String baseObject, String group, Integer isMeatFish) {

		Ingredient ingredient = new Ingredient (quantity, units, object, unitsAlt, baseObject, group, isMeatFish);
		mIngredients.add (ingredient);
	}
	

	public void addIngredient (String quantityStr, String units, String object, String unitsAlt, String baseObject, String group, Integer isMeatFish) {

		Ingredient ingredient = new Ingredient (quantityStr, units, object, unitsAlt, baseObject, group, isMeatFish);
		mIngredients.add (ingredient);
	}

	
	// Check if a string ends with "x2", "x3", etc.
	private Integer endsWithMultiplier (String string) {
		
		int numChars = 0;
		for (int i = string.length()-1; i >= 1; i--) {
			char charString = string.charAt(i);
			if (Character.isDigit(charString)) {
				numChars ++;
			}
			else if (numChars > 0 && charString == 'x' && string.charAt(i-1) == ' ') {
				return i-1;
			}
			else {
				return -1;
			}
		}
		return -1;
	}


	public String capitalizeFirstLetter (String string) {

		/*for (int i = 0; i < string.length(); i++) {
			char charString = string.charAt(i);
			if (Character.isLetter(charString)) {
				charString = Character.toUpperCase(charString);
				string = string.substring(0,i) + charString + string.substring(i+1);
				break;
			}
		}*/

		char charString = string.charAt(0);
		if (Character.isLetter(charString)) {
			charString = Character.toUpperCase(charString);
			string = charString + string.substring(1);
		}
		
		return string;
	}


	public ArrayList<ArrayList<String>> toArrayList () {

		ArrayList<ArrayList<String>> ingredientsList = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < mIngredients.size(); i++) {
			if (mIngredients.get(i).mBaseObject.trim().equals("")) {
				Log.w (MainActivity.logAppNameString, "Empty grocery element for ingredient "+mIngredients.get(i).mObject);
				continue;
			}
			if (mIngredients.get(i).mBaseObject.trim().equals("water")) {
				continue;
			}
			String str1 = mIngredients.get(i).ingredientToString();
			if (i > 0) {
				String str0 = mIngredients.get(i-1).ingredientToString();
				if (str0.equals(str1)) {
					//Log.v(MainActivity.logAppNameString, "grocery list repeat, "+mIngredients.get(i).ingredientToString());					
					ArrayList<String> ingredient = ingredientsList.get(ingredientsList.size()-1);
					String listStr = ingredient.get(0);
					String newStr;
					Integer index = endsWithMultiplier(listStr);
					if (index >= 0) {
						String subString0 = listStr.substring(0, index).trim();
						String subString1 = listStr.substring(index+2).trim();
						//Log.v(MainActivity.logAppNameString, "string0, string1, "+subString0+" "+subString1);
						Integer multiplier = Integer.parseInt(subString1) + 1;
						newStr = subString0.trim() + " x"+multiplier.toString();
					}
					else {
						newStr = listStr.trim();
						//Log.v(MainActivity.logAppNameString, "repeated item = "+newStr);
						if (!newStr.equals("Olive oil")) {
							newStr += " x2";
						}
					}
					//Log.v(MainActivity.logAppNameString, "new entry, "+newStr);

					ingredient.set(0, newStr);
					ingredientsList.set(ingredientsList.size()-1, ingredient);
					continue;
				}
			}
			str1 = capitalizeFirstLetter(str1);
			ArrayList<String> ingredient = new ArrayList<String>();
			ingredient.add(str1);
			ingredient.add(mIngredients.get(i).mGroup);
			ingredientsList.add(ingredient);
		}
		
		/*for (int i = 0; i < ingredientsList.size(); i++) {
			Log.v(MainActivity.logAppNameString, "final grocery list, "+ingredientsList.get(i));					
		}*/
		
		return ingredientsList;
	}
	
	
	public void sort () {

    	// Sort elements by group then baseObject.
		Collections.sort (mIngredients, new Comparator<Ingredient>() {
			public int compare (Ingredient v1, Ingredient v2) {
				//return v1.object.compareTo(v2.object);
				//int compare = Integer.compare(v1.groupEnum, v2.groupEnum);
				int compare = v1.mGroupEnum > v2.mGroupEnum ? +1 : v1.mGroupEnum < v2.mGroupEnum ? -1 : 0;				
				if (compare == 0) {
					return v1.mBaseObject.compareTo(v2.mBaseObject);
				}
				else {
					return compare;
				}
			}
		});
	}

	
	// Process the Ingredient list to combine common elements.

	public void processList () {

		if (mIngredients.size() == 0) {
			return;
		}
		
		ArrayList<Ingredient> holderList = new ArrayList<Ingredient>();
		holderList.add(mIngredients.get(0));

		for (Integer i = 1; i < mIngredients.size(); i++) {

			String object = mIngredients.get(i).mBaseObject.toLowerCase();
			// Need to be careful with words ending in "s" like "asparagus".
			/*if (object.endsWith("s")) {
				object = object.substring(0, object.length()-1);
			}*/
			
			// Check if an entry already exists in holderList.
			boolean addedEntry = false;
			for (Integer j = 0; j < holderList.size(); j++) {

				String holder = holderList.get(j).mBaseObject.toLowerCase();
				//Log.v(MainActivity.logAppNameString, "object and holder: "+object+" "+holder);

				// To avoid problems with e.g. "salt" and "unsalted butter", use base object and require exact match.
				//if (holder.contains(object)) {
				if (holder.equals(object)) {

					Float objectQuantity = mIngredients.get(i).mQuantity;
					Float holderQuantity = holderList.get(j).mQuantity;
					String objectUnits = mIngredients.get(i).mUnits.toLowerCase();
					String holderUnits = holderList.get(j).mUnits.toLowerCase();
					//Log.v(MainActivity.logAppNameString, holderQuantity.toString()+" "+objectQuantity.toString()+", "+holderUnits + " " + objectUnits);

					//Log.v(MainActivity.logAppNameString, "input to combine: "+object+", "+objectQuantity+", "+objectUnits+" -- "+holder+", "+holderQuantity+", "+holderUnits);
					String[] quantityUnits = combineQuantityUnits (objectQuantity, holderQuantity, objectUnits, holderUnits);

					if (quantityUnits.length == 2 && quantityUnits[0] != "") {
						//Log.v(MainActivity.logAppNameString, "output from combine: "+quantityUnits[0]+" "+quantityUnits[1]);
						Ingredient currentElement = holderList.get(j);
						//Float originalQuantity = currentElement.mQuantity;
						currentElement.mQuantity = getQuantityFloat(quantityUnits[0]);
						currentElement.mUnits = quantityUnits[1];
						if (/*originalQuantity <= 1 &&*/ currentElement.mQuantity > 1) {
							if (!currentElement.mUnits.equals("")) {
// FIXME. Will not work for all strings.
								if (currentElement.mUnits.startsWith("large") || currentElement.mUnits.startsWith("small")) {
									currentElement.mUnits = unPluralizeSecondWord (currentElement.mUnits);
								}
								else {
									currentElement.mUnits = unPluralizeFirstWord (currentElement.mUnits);
								}
								if (currentElement.mUnits.startsWith("large") || currentElement.mUnits.startsWith("small")) {
									currentElement.mUnits = pluralizeSecondWord (currentElement.mUnits);
								}
								else if (!currentElement.mUnits.toLowerCase().trim().equals("oz")) {
									currentElement.mUnits = pluralizeFirstWord (currentElement.mUnits);
								}
							}
							/*else {   // Difficult, since could have e.g. "tomato" or "large tomato".
								Log.v(MainActivity.logAppNameString, "in-units2  = "+currentElement.units);
								currentElement.object = pluralizeFirstWord (currentElement.object);
								Log.v(MainActivity.logAppNameString, "out-units2 = "+currentElement.units);
							}*/
						}
						holderList.set(j, currentElement);
						addedEntry = true;
						break;
					}
				}
			}

			// If the entry doesn't exist then add it to the list.
			if (!addedEntry) {
				//Log.v(MainActivity.logAppNameString, "Adding new entry.");
				holderList.add(mIngredients.get(i));
			}
		}

		mIngredients = holderList;

		sort ();
		combineAdditionalRepeats ();
		removeRepeats ();
		
		/*for (Integer i = 0; i < mIngredients.size(); i++) {
			Ingredient ingredient = mIngredients.get(i);
			float quantity = ingredient.mQuantity;
			String units = ingredient.mUnits;
			String baseObject = ingredient.mBaseObject;
			Log.v(MainActivity.logAppNameString, "Grocery item: "+((Float)quantity).toString()+" "+units+" "+baseObject);
		}*/
	}


	private String[] combineQuantityUnits (Float quantity1, Float quantity2, String _unit1, String _unit2) {

		String unit1 = _unit1.toLowerCase().trim();
		String unit2 = _unit2.toLowerCase().trim();

		unit1 = unPluralizeWord (unit1);
		unit2 = unPluralizeWord (unit2);

		if (quantity1 == -1 && quantity2 == -1) {
			return new String[]{ "", _unit1 };				
		}
		else if (quantity1 == -1) {
			return new String[]{ ((Float)(quantity2)).toString(), _unit2 };
		}
		else if (quantity2 == -1) {
			return new String[]{ ((Float)(quantity1)).toString(), _unit1 };
		}
		else if (unit1.equals(unit2)) {
			// Keep any plural.
			if (_unit1.endsWith("es") || _unit1.endsWith("s")) {
				return new String[]{ ((Float)(quantity1+quantity2)).toString(), _unit1 };
			}
			else {
				return new String[]{ ((Float)(quantity1+quantity2)).toString(), _unit2 };				
			}
		}
		else {
			if (unit1.equals("pinch")) {
				return new String[]{ quantity2.toString(), _unit2 };
			}
			else if (unit2.equals("pinch")) {
				return new String[]{ quantity1.toString(), _unit1 };
			}
			else {
				// 1 tablespoon = 3 teaspoon.
				// 1 cup = 16 tablespoon.
				// 1 cup = 8 fluid oz.
				// 1 lb = 16 oz.

				ArrayList<ArrayList<String>> conversion = new ArrayList<ArrayList<String>>();
				ArrayList<String> init1 = new ArrayList<String>(); init1.add("teaspoon");   init1.add("tablespoon"); init1.add("3");    conversion.add(init1);
				ArrayList<String> init2 = new ArrayList<String>(); init2.add("tablespoon"); init2.add("cup");        init2.add("16");   conversion.add(init2);
				ArrayList<String> init3 = new ArrayList<String>(); init3.add("teaspoon");   init3.add("cup");		 init3.add("48");   conversion.add(init3);
				ArrayList<String> init4 = new ArrayList<String>(); init4.add("fl oz");      init4.add("cup");        init4.add("8");    conversion.add(init4);
				ArrayList<String> init6 = new ArrayList<String>(); init6.add("oz");         init6.add("lb");         init6.add("16");   conversion.add(init6);
				ArrayList<String> init7 = new ArrayList<String>(); init7.add("g");          init7.add("kg");         init7.add("1000"); conversion.add(init7);
				ArrayList<String> init8 = new ArrayList<String>(); init8.add("ml");         init8.add("l");          init8.add("1000"); conversion.add(init8);

				for (int i = 0; i < conversion.size(); i++) {
					ArrayList<String> conv = conversion.get(i);
					//Log.v(MainActivity.logAppNameString, "conversion: "+unit1+" "+unit2+" "+conv.get(0)+" "+conv.get(1));
					if (unit1.equals(conv.get(0)) && unit2.equals(conv.get(1))) {
						Float quantity = quantity2 + quantity1/Float.parseFloat(conv.get(2));
						return new String[]{ getQuantityString(quantity), _unit2 };
					}
					else if (unit1.equals(conv.get(1)) && unit2.equals(conv.get(0))) {
						Float quantity = quantity1 + quantity2/Float.parseFloat(conv.get(2));
						return new String[]{ getQuantityString(quantity), _unit1 };
					}
				}

				Log.w (MainActivity.logAppNameString, "Unit conversion required to combine units "+unit1+" and "+unit2);
				return new String[]{"", ""};			
			}
		}
	}
	

	private String pluralizeFirstWord (String string) {
	
		String[] stringArray = string.split(" ");
		stringArray[0] = pluralizeWord (stringArray[0]);

		StringBuilder builder = new StringBuilder();
		for (String s : stringArray) {
		   	builder.append(s + " ");
		}

		return builder.toString();
	}


	private String pluralizeSecondWord (String string) {
		
		String[] stringArray = string.split(" ");
		if (stringArray.length > 1) {
			stringArray[1] = pluralizeWord (stringArray[1]);

			StringBuilder builder = new StringBuilder();
			for (String s : stringArray) {
				builder.append(s + " ");
			}
			return builder.toString();
		}
		else {
			return string;
		}
	}

	
	private String unPluralizeFirstWord (String string) {
		
		String[] stringArray = string.split(" ");
		stringArray[0] = unPluralizeWord (stringArray[0]);

		StringBuilder builder = new StringBuilder();
		for (String s : stringArray) {
		   	builder.append(s + " ");
		}

		return builder.toString();
	}


	private String unPluralizeSecondWord (String string) {
		
		String[] stringArray = string.split(" ");
		if (stringArray.length > 1) {
			stringArray[1] = unPluralizeWord (stringArray[1]);

			StringBuilder builder = new StringBuilder();
			for (String s : stringArray) {
				builder.append(s + " ");
			}

			return builder.toString();
		}
		else {
			return string;
		}
	}

	
	private String pluralizeWord (String word) {

		// Some words don't pluralize, e.g.:
		//    baggage, luggage, pants, sheep, evidence, furniture,
		//    news, pajamas, music, money, jewelry, housework, homework,
		// but we shouldn't encounter those here.

		if (word.endsWith("s") || word.endsWith("sh") || word.endsWith("ch") || 
			word.endsWith("x") || word.endsWith("z")) {
			return word + "es";
		}
		else {
			return word + "s";			
		}
	}


	private String unPluralizeWord (String word) {
		
		if (word.endsWith("ses") || word.endsWith("shes") || word.endsWith("ches") || 
			word.endsWith("xes") || word.endsWith("zes")) {
			return word.substring(0, word.length()-2);
		}
		else if (word.endsWith("s")) {
			return word.substring(0, word.length()-1);
		}
		else {
			return word;			
		}
	}

	
	private void combineAdditionalRepeats () {
		
		// List must already be sorted.
		//sort ();

		ArrayList<String> objectsToCombine = new ArrayList<String>();
		/*ArrayList<String> objectsToCombine1 = new ArrayList<String>();
		objectsToCombine0.add("Juice of");				   objectsToCombine1.add("Eureka lemon");
		objectsToCombine0.add("Juice of");				   objectsToCombine1.add("lemon");
		objectsToCombine0.add("Juice of");				   objectsToCombine1.add("lime");
		objectsToCombine0.add("Juice from");			   objectsToCombine1.add("lemon");
		objectsToCombine0.add("Juice from");			   objectsToCombine1.add("lime");
		objectsToCombine0.add("Grated zest and juice of"); objectsToCombine1.add("lemon");
		objectsToCombine0.add("Grated zest and juice of"); objectsToCombine1.add("lime");*/
		objectsToCombine.add("eureka lemon");
		objectsToCombine.add("lemon");
		objectsToCombine.add("lime");
		objectsToCombine.add("orange");
		
		Pattern pattern = Pattern.compile("[a-zA-Z]+");

		for (int i = 0; i < objectsToCombine.size(); i++) {
			String object = objectsToCombine.get(i).toLowerCase();
			Float quantity = 0f;
			Integer index = -1;

			ArrayList<Integer> indexToRemove = new ArrayList<Integer>();

			for (int j = 0; j < mIngredients.size(); j++) {
				Ingredient ingredient = mIngredients.get(j);
				if (ingredient.mQuantity == -1 && ingredient.mUnits.trim().equals("")) {
					String ingredientString = ingredient.mObject;
					ingredientString = Normalizer.normalize(ingredientString, Normalizer.Form.NFKD);
					ingredientString = ingredientString.toLowerCase();

					//Log.v(MainActivity.logAppNameString, "here0, "+ingredientString+", "+object0+", "+object1);
					/*Log.v(MainActivity.logAppNameString, "          "+((Integer)ingredientString.length()).toString()+" "+
												((Integer)object0.length()).toString()+" "+
												((Integer)object1.length()).toString());*/

					//if (ingredientString.length() >= object0.length()+object1.length()+3) {
					if (ingredientString.contains(object)) {

						//Log.v(MainActivity.logAppNameString, "Object = "+ingredient.mObject);

						//String[] stringSplit = ingredientString.toLowerCase().split(" ");
						String[] stringSplit = ingredientString.toLowerCase().split(object);
						//Log.v(MainActivity.logAppNameString, "Object2 = "+stringSplit[0]);
						stringSplit = (stringSplit[0]).split(" ");
						String objectQuantity = stringSplit[stringSplit.length-1];
						//for (int k = objectLength; k < stringSplit.length; k++) {
						/*Log.v(MainActivity.logAppNameString, "Object2 = "+stringSplit[k]);
						String objectPlural = pluralizeWord(object);
						if (stringSplit[k].equals(object)     || stringSplit[k].equals(objectPlural) ||
						    stringSplit[k].equals(object+",") || stringSplit[k].equals(object+".")) {
							String objectQuantity = stringSplit[k-objectLength];*/
						//Log.v(MainActivity.logAppNameString, "Calculated quantity "+objectQuantity);

						Matcher matcher = pattern.matcher(objectQuantity);
						if (!matcher.find()) {
							//Log.v(MainActivity.logAppNameString, "   good");
							if (quantity == 0) {
								index = j;
							}
							else {
								indexToRemove.add(j);
							}
							quantity += getQuantityFloat (objectQuantity);
						}
						/*else {
							Log.v(MainActivity.logAppNameString, "   bad");
						}*/						
					}
				}
			}
			
			if (quantity != 0) {
				Ingredient ingredient = mIngredients.get(index);
				//String quantityStr = getQuantityString (quantity);
				ingredient.mQuantity = quantity;
				//ingredient.mObject = objectQuantity + " " + object;				
				mIngredients.set(index, ingredient);
			}

			// indexToRemove is already sorted.
			for (int j = indexToRemove.size()-1; j >= 0; j--) {
				//Log.v(MainActivity.logAppNameString, "Removing in combineAdditionalRepeats "+mIngredients.get((int)indexToRemove.get(j)).mBaseObject);
				mIngredients.remove((int)indexToRemove.get(j));
			}
		}
	}


	private void removeRepeats () {

		sort ();

		ArrayList<String> objectEqualsRemove = new ArrayList<String>();
		ArrayList<String> unitStartsRemove = new ArrayList<String>();
		objectEqualsRemove.add("salt and pepper");
		objectEqualsRemove.add("black pepper");
		objectEqualsRemove.add("ground pepper");
		objectEqualsRemove.add("pepper");
		objectEqualsRemove.add("salt and white pepper");
		objectEqualsRemove.add("flaky sea salt, such as Maldon");
		objectEqualsRemove.add("sesame seeds");
		objectEqualsRemove.add("black sesame seeds");
		objectEqualsRemove.add("sunflower seeds");
		objectEqualsRemove.add("cornichons and pickled onions");
		objectEqualsRemove.add("grainy mustard");
		objectEqualsRemove.add("shredded jack cheese, Cheddar cheese, or queso fresco");
		objectEqualsRemove.add("guacamole or avocado");
		objectEqualsRemove.add("baby carrot, mini bell peppers, sugar snap peas, and cherry tomatoes");
		objectEqualsRemove.add("grocery list repeats, butter lettuce leaves");
		objectEqualsRemove.add("chives or green onions");
		objectEqualsRemove.add("fresh flat-leaf parsley");
		objectEqualsRemove.add("fresh mint");
		objectEqualsRemove.add("fresh thyme");
		objectEqualsRemove.add("green cabbage");
		objectEqualsRemove.add("grilled green onions, fennel, or asparagus");
		objectEqualsRemove.add("slices Cheddar cheese");
		objectEqualsRemove.add("ground chile powder");
		objectEqualsRemove.add("maple syrup or honey");
		objectEqualsRemove.add("corn or all-purpose flour");
		objectEqualsRemove.add("sumac or smoked paprika");
		objectEqualsRemove.add("sprinkles");
		objectEqualsRemove.add("salsa");
		objectEqualsRemove.add("salsa fresca");
		objectEqualsRemove.add("small flour or corn tortillas");
		objectEqualsRemove.add("flour or corn tortillas");
		objectEqualsRemove.add("sliced black olives");
		objectEqualsRemove.add("butter lettuce leaves");
		objectEqualsRemove.add("sesame oil for drizzling");
		unitStartsRemove.add("pinch");
		
		// Using iterators to remove current element would be more efficient,
		// but for a small array this will be OK.
		ArrayList<Integer> indexToRemove = new ArrayList<Integer>();

		for (int i = 1; i < mIngredients.size(); i++) {
			Ingredient ingredient0 = mIngredients.get(i-1);
			Ingredient ingredient1 = mIngredients.get(i);
			/*if (ingredient0.mBaseObject.equals(ingredient1.mBaseObject)) {
				float quantity0 = ingredient0.mQuantity;
				float quantity1 = ingredient1.mQuantity;
				Log.v(MainActivity.logAppNameString, "Repeated item: "+						
						((Float)quantity0).toString()+" "+ingredient0.mUnits+" "+ingredient0.mBaseObject+" "+						
						((Float)quantity1).toString()+" "+ingredient1.mUnits+" "+ingredient1.mBaseObject);
			}*/

			if (ingredient0.mQuantity == -1 && ingredient1.mQuantity == -1 &&
				ingredient0.mUnits.trim().equals(ingredient1.mUnits.trim()) &&
				ingredient0.mBaseObject.trim().equals(ingredient1.mBaseObject.trim())) {

				boolean removingIndex = false;
				
				for (int j = 0; j < objectEqualsRemove.size(); j++) {
					if (ingredient0.mBaseObject.equals(objectEqualsRemove.get(j))) {
						//Log.v(MainActivity.logAppNameString, "Removing "+ingredient0.mObject+", "+ingredient0.mBaseObject);
						removingIndex = true;
						break;
					}
				}
				
				if (!removingIndex) {
					for (int j = 0; j < unitStartsRemove.size(); j++) {
						if (ingredient0.mUnits.toLowerCase().startsWith(unitStartsRemove.get(j))) {
							//Log.v(MainActivity.logAppNameString, "Removing "+ingredient0.mUnits.toLowerCase()+" "+ingredient0.mObject+", "+ingredient0.mBaseObject);
							removingIndex = true;
							break;
						}
					}
				}
				
				if (removingIndex) {
					indexToRemove.add(i);
					//Log.v(MainActivity.logAppNameString, "Removing "+ingredient0.object);
				}
			}
		}
		
		// indexToRemove is already sorted.
		for (int i = indexToRemove.size()-1; i >= 0; i--) {
			//Log.v(MainActivity.logAppNameString, "Removing in removeRepeats "+mIngredients.get((int)indexToRemove.get(i)).mBaseObject);
			mIngredients.remove((int)indexToRemove.get(i));
		}
	}
	

	public Float getQuantityFloat (String quantityStr) {
		
		quantityStr = quantityStr.trim();
		if (quantityStr.equals("")) {
			return -1f;
		}

		if (quantityStr.contains(" or ")) {
			quantityStr = (quantityStr.split(" or "))[1];
		}

		if (quantityStr.contains("-")) {
			quantityStr = (quantityStr.split("-"))[1];
		}

		String[] twoParts = quantityStr.split(" ");
		if (twoParts.length == 2) {
			return getQuantityFloat (twoParts[0]) + getQuantityFloat (twoParts[1]);
		}

		// Convert unicode fraction to form a/b.
		quantityStr = Normalizer.normalize(quantityStr, Normalizer.Form.NFKD);
		String[] quantitySplt = quantityStr.split("/");
		if (quantitySplt.length == 1) {
			quantitySplt = quantitySplt[0].split("\u2044");
		}

		Float quantity;
		if (quantitySplt.length == 1) {
			quantity = Float.parseFloat(quantitySplt[0]);
		}
		else if (quantitySplt.length == 2) {
			quantity = Float.parseFloat(quantitySplt[0])/Float.parseFloat(quantitySplt[1]);
		}
		else {
			Log.w (MainActivity.logAppNameString, "getQuantityFloat: Should not be here");
			return 0f;
		}

		return quantity;
	}


	public String getQuantityString (Float value) {
		
		if (value == -1) {
			return "";
		}
		
		if (Math.abs(value - Math.round(value)) < 0.0001) {
			//Log.v(MainActivity.logAppNameString, "Rounding, Input float = "+value.toString()+", Output string = "+((Integer)Math.round(value)).toString());
			return ((Integer)Math.round(value)).toString();
		}

		int factor = 100;

		StringBuilder sb = new StringBuilder();

		if (value < 0) {
			sb.append('-');
			value = -value;
		}

		long valueLong = value.longValue();
		if (valueLong != 0)
			sb.append(valueLong);

		value -= valueLong;

		double error = Math.abs(value);
		int denom = 1;
		for (int i = 2; i <= factor; i++) {
			double error2 = Math.abs(value - (double) Math.round(value * i) / i);
			if (error2 < error) {
				error = error2;
				denom = i;
			}
		}

		if (denom > 1)
			sb.append(' ').append(Math.round(value * denom)).append('/') .append(denom);

		/*String str = sb.toString();
		if (str.endsWith("2/2")) {
			Log.v(MainActivity.logAppNameString, "Input float = "+value.toString()+", Output string = "+str);
		}*/
		
		return sb.toString();
	}


	public class Ingredient {

		public Float mQuantity;  // -1 to not use.
		public String mUnits;
		public String mObject;
		public String mUnitsAlt;
		public String mBaseObject;
		public String mGroup;
		public int mGroupEnum;
		public boolean mIsFish;
		public boolean mIsMeat;


		public Ingredient (Float quantity, String units, String object, String unitsAlt, String baseObject, String group, Integer isMeatFish) {

			mQuantity   = quantity;
			mUnits      = units.trim();
			mObject     = object.trim();
			mUnitsAlt   = unitsAlt.trim();
			mBaseObject = baseObject.trim();
			mGroup      = group.trim();
			mGroupEnum  = groceryListEnum (group);
 			mIsFish     = (isMeatFish == 1 || isMeatFish == 3);
			mIsMeat     = (isMeatFish == 2 || isMeatFish == 3);

			//Log.v(MainActivity.logAppNameString, "Ingredient: "+object);
		}


		public Ingredient (String quantityStr, String units, String object, String unitsAlt, String baseObject, String group, Integer isMeatFish) {
			
			mQuantity   = getQuantityFloat (quantityStr);
			mUnits      = units.trim();
			mObject     = object.trim();
			mUnitsAlt   = unitsAlt.trim();
			mBaseObject = baseObject.trim();
			mGroup      = group.trim();
			mGroupEnum  = groceryListEnum (group);
			mIsFish     = (isMeatFish == 1 || isMeatFish == 3);
			mIsMeat     = (isMeatFish == 2 || isMeatFish == 3);
			
			//Log.v(MainActivity.logAppNameString, "Ingredient: "+object);
		}


		public String ingredientToString () {

			String str = "";
			String quantityStr = getQuantityString(this.mQuantity);
			if (!quantityStr.trim().equals("")) {
				str += quantityStr.trim() + " ";
			}
			if (!mUnits.trim().equals("")) {
				str += mUnits.trim() + " ";
			}
			if (!mBaseObject.trim().equals("")) {
				str += mBaseObject.trim() + " ";
			}

			return str;
		}
	}
	
	
	static public int groceryListEnum (String name) {

		int i = 0;
		for (; i < MainActivity.GROCERY_CATEGORIES.length; i++) {
			if (name.equals(MainActivity.GROCERY_CATEGORIES[i])) {
				return i;
			}
		}
		
		return i;
	}
}
