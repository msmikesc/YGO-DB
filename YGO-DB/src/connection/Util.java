package connection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.CardSet;
import bean.SetMetaData;

public class Util {

	public static BigDecimal one = new BigDecimal(1);
	public static BigDecimal two = new BigDecimal(2);
	public static BigDecimal cent50 = new BigDecimal(.5);
	public static BigDecimal ten = new BigDecimal(10);
	public static BigDecimal thirty = new BigDecimal(30);
	public static BigDecimal oneCent = new BigDecimal(0.01);
	
	public static void checkSetCounts() throws SQLException {
		ArrayList<SetMetaData> list = SQLiteConnection.getSetMetaDataFromSetData();
		
		for(SetMetaData setData:list) {
			int countCardsinList = SQLiteConnection.getCountDistinctCardsInSet(setData.set_name);
			
			if(countCardsinList != setData.num_of_cards) {
				System.out.println("Issue for " + setData.set_name + " metadata:" + setData.num_of_cards + " count:" + countCardsinList);
			}
		}
	}
	
	public static void updateDBWithSetsFromAPI(String setName) {
		String setAPI = "https://db.ygoprodeck.com/api/v7/cardsets.php";
		
		boolean specificSet =true;
		
		if(setName == null || setName.isBlank()) {
			specificSet = false;
		}

		try {
			URL url = new URL(setAPI);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();

			// Getting the response code
			int responsecode = conn.getResponseCode();

			if (responsecode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responsecode);
			} else {

				String inline = "";
				Scanner scanner = new Scanner(url.openStream());

				// Write all the JSON data into a string using a scanner
				while (scanner.hasNext()) {
					inline += scanner.nextLine();
				}

				// Close the scanner
				scanner.close();

				JSONArray array = new JSONArray(inline);

				inline = null;

				for (Object setObject : array) {
					JSONObject set = (JSONObject) setObject;

					String set_name = Util.getStringOrNull(set, "set_name");
					String set_code = Util.getStringOrNull(set, "set_code");
					int num_of_cards = Util.getIntOrNull(set, "num_of_cards");
					String tcg_date = Util.getStringOrNull(set, "tcg_date");
					
					if(!specificSet) {
						SQLiteConnection.insertCardSet(set_name, set_code, num_of_cards, tcg_date);
					}
					if(specificSet && set_name.equalsIgnoreCase(setName)) {
						SQLiteConnection.insertCardSet(set_name, set_code, num_of_cards, tcg_date);
						return;
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static CardSet findRarity(String cardNumber, String priceBought, String dateBought, String folderName,
			String condition, String editionPrinting, String setNumber, String setName, String cardName)
			throws SQLException {

		ArrayList<CardSet> setRarities = SQLiteConnection.getRaritiesOfCardInSet(cardNumber);

		if (setRarities.size() == 0) {
			// try removing color code

			String newSetNumber = cardNumber.substring(0, cardNumber.length() - 1);
			String colorcode = cardNumber.substring(cardNumber.length() - 1, cardNumber.length());

			setRarities = SQLiteConnection.getRaritiesOfCardInSet(newSetNumber);

			for (CardSet c : setRarities) {
				c.colorVariant = colorcode;
			}
		}

		if (setRarities.size() == 1) {
			CardSet match = setRarities.get(0);

			match.rarityUnsure = 0;

			return match;
		}

		ArrayList<CardSet> ownedRarities = SQLiteConnection.getExistingOwnedRaritesForCard(cardNumber, priceBought,
				dateBought, folderName, condition, editionPrinting);

		if (ownedRarities.size() == 1) {
			return ownedRarities.get(0);
		}

		// if we haven't found any at all give up
		if (setRarities.size() == 0) {
			System.out.println("Unable to find anything for " + cardNumber);
			CardSet setIdentified = new CardSet();

			setIdentified.setName = setName;
			setIdentified.setNumber = setNumber;
			setIdentified.setRarity = "Unknown";
			setIdentified.colorVariant = "Unknown";
			setIdentified.rarityUnsure = 1;

			// check for name
			setIdentified.id = SQLiteConnection.getCardIdFromTitle(cardName);

			return setIdentified;
		}

		// try closest price
		BigDecimal priceBoughtDec = new BigDecimal(priceBought);
		BigDecimal distance = new BigDecimal(setRarities.get(0).setPrice).subtract(priceBoughtDec).abs();
		int idx = 0;
		for (int c = 1; c < setRarities.size(); c++) {
			BigDecimal cdistance = new BigDecimal(setRarities.get(c).setPrice).subtract(priceBoughtDec).abs();
			if (cdistance.compareTo(distance) <= 0) {
				idx = c;
				distance = cdistance;
			}
		}

		CardSet rValue = setRarities.get(idx);
		rValue.rarityUnsure = 1;

		System.out.println("Took a guess that " + cardNumber + " is:" + rValue.setRarity);

		return rValue;

	}

	public static String getAdjustedPriceFromRarity(String rarity, String inputPrice) {

		BigDecimal price = new BigDecimal(inputPrice);

		if (price.compareTo(oneCent) < 0) {
			price = oneCent;
		}

		if (rarity.contains("Collector")) {
			price = price.add(thirty);
		}

		if (rarity.contains("Ultimate")) {
			price = price.add(ten);
		}

		if (rarity.contains("Starlight")) {
			price = price.add(thirty);
		}

		if (rarity.contains("Ghost")) {
			price = price.add(thirty);
		}

		if (rarity.contains("Duel Terminal")) {
			price = price.add(cent50);
		}

		if (rarity.contains("Gold")) {
			price = price.add(one);
		}

		if (rarity.contains("Starfoil")) {
			price = price.add(cent50);
		}

		if (rarity.contains("Shatterfoil")) {
			price = price.add(cent50);
		}

		if (rarity.contains("Mosaic")) {
			price = price.add(cent50);
		}

		if (rarity.contains("Super")) {
			price = price.add(cent50);
		}

		if (rarity.contains("Ultra")) {
			price = price.add(one);
		}

		if (rarity.contains("Secret")) {
			price = price.add(two);
		}

		price = price.setScale(2, RoundingMode.CEILING);

		return price.toString();

	}
	
	public static void checkForIssuesWithSet(String setName) throws SQLException {

		ArrayList<String> cardsInSetList = SQLiteConnection.getSortedCardsInSetByName(setName);

		String lastPrefix = null;
		String lastLang = null;
		int lastNum = -1;
		String lastFullString = null;
		for (String currentCode : cardsInSetList) {
			String[] splitStrings = currentCode.split("-");

			int numIndex = 0;

			try {
				while (!Character.isDigit(splitStrings[1].charAt(numIndex))) {
					numIndex++;
				}
			} catch (Exception e) {
				System.out.println("Issue found with " + setName + ": " + lastFullString + " and " + currentCode);
				lastFullString = currentCode;
				continue;
			}

			String identifiedPrefix = splitStrings[0];

			String identifiedLang = splitStrings[1].substring(0, numIndex);

			String identifiedNumString = splitStrings[1].substring(numIndex, splitStrings[1].length());

			Integer identifiedNumber = null;

			try {
				identifiedNumber = new Integer(identifiedNumString);
			} catch (Exception e) {
				System.out.println("Issue found with " + setName + ": " + lastFullString + " and " + currentCode);
				lastPrefix = identifiedPrefix;
				lastLang = identifiedLang;
				lastNum = -1;
				lastFullString = currentCode;
				continue;
			}

			// check for changed set id
			if (!identifiedPrefix.equals(lastPrefix) || !identifiedLang.equals(lastLang)) {
				lastPrefix = identifiedPrefix;
				lastLang = identifiedLang;
				lastNum = identifiedNumber;
				lastFullString = currentCode;
			}

			if (!(lastNum == identifiedNumber || lastNum == (identifiedNumber - 1))) {
				// issue found
				System.out.println("Issue found with " + setName + ": " + lastFullString + " and " + currentCode);
				lastPrefix = identifiedPrefix;
				lastLang = identifiedLang;
				lastNum = identifiedNumber;
				lastFullString = currentCode;
				continue;
			} else {
				lastPrefix = identifiedPrefix;
				lastLang = identifiedLang;
				lastNum = identifiedNumber;
				lastFullString = currentCode;
			}

		}
	}

	public static String getStringOrNull(JSONObject current, String id) {
		try {
			String value = current.getString(id);
			return value;
		} catch (JSONException e) {
			return null;
		}
	}
	
	public static Integer getIntOrNull(JSONObject current, String id) {
		try {
			int value = current.getInt(id);
			return value;
		} catch (JSONException e) {
			return null;
		}
	}

	
	
}
