package importer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Scanner;

import org.json.*;

import connection.SQLiteConnection;
import connection.Util;

import java.sql.SQLException;

public class ImportFromYGOPROAPI {

	public static void main(String[] args) throws SQLException, IOException {
		ImportFromYGOPROAPI mainObj = new ImportFromYGOPROAPI();
		mainObj.run();
	}

	public void run() throws SQLException, IOException {

		String setName = "Darkwing Blast";

		setName = setName.trim();

		String setAPI = "https://db.ygoprodeck.com/api/v7/cardinfo.php?cardset=";

		String apiURL = setAPI + URLEncoder.encode(setName);

		try {

			updateDBWithSetsFromAPI(setName);

			URL url = new URL(apiURL);

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

				JSONObject jo = new JSONObject(inline);

				inline = null;

				JSONArray cards = (JSONArray) jo.get("data");

				Iterator<Object> keyset = cards.iterator();

				while (keyset.hasNext()) {

					JSONObject current = (JSONObject) keyset.next();

					insertGameplayCardFromYGOPRO(current);

					int cardID = current.getInt("id");
					String name = current.getString("name");

					JSONArray sets = null;
					Iterator<Object> setIteraor = null;
					boolean isSets = false;

					try {
						sets = current.getJSONArray("card_sets");
						setIteraor = sets.iterator();
						isSets = true;
					} catch (JSONException e) {

					}

					if (isSets) {
						insertCardSetsForOneCard(sets, setIteraor, name, cardID);
					}

				}

				Util.checkForIssuesWithSet(setName);
				Util.checkSetCounts();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertGameplayCardFromYGOPRO(JSONObject current) throws SQLException {

		int wikiID = Util.getIntOrNull(current, "id");
		String name = Util.getStringOrNull(current, "name");
		String type = Util.getStringOrNull(current, "type");
		int passcode = Util.getIntOrNull(current, "id");// passcode
		String desc = Util.getStringOrNull(current, "desc");
		String attribute = Util.getStringOrNull(current, "attribute");
		String race = Util.getStringOrNull(current, "race");
		int linkval = Util.getIntOrNull(current, "linkval");
		int level = Util.getIntOrNull(current, "level");
		int scale = Util.getIntOrNull(current, "scale");
		int atk = Util.getIntOrNull(current, "atk");
		int def = Util.getIntOrNull(current, "def");
		String archetype = Util.getStringOrNull(current, "archetype");

		SQLiteConnection.replaceIntoGamePlayCard(wikiID, name, type, passcode, desc, attribute, race, linkval, level,
				scale, atk, def, archetype);
	}

	public static void insertCardSetsForOneCard(JSONArray sets, Iterator<Object> setIteraor, String name, int wikiID)
			throws SQLException {

		for (int i = 0; i < sets.length(); i++) {

			JSONObject currentSet = (JSONObject) setIteraor.next();

			String set_code = null;
			String set_name = null;
			String set_rarity = null;
			String set_price = null;

			try {
				set_code = currentSet.getString("set_code");
				set_name = currentSet.getString("set_name");
				set_rarity = currentSet.getString("set_rarity");
				set_price = currentSet.getString("set_price");
			} catch (Exception e) {
				System.out.println("issue found on " + name);
				continue;
			}

			set_price = Util.getAdjustedPriceFromRarity(set_rarity, set_price);

			SQLiteConnection.replaceIntoCardSet(set_code, set_rarity, set_name, wikiID, set_price);

		}
	}

	public static void updateDBWithSetsFromAPI(String setName) {
		String setAPI = "https://db.ygoprodeck.com/api/v7/cardsets.php";

		boolean specificSet = true;

		if (setName == null || setName.isBlank()) {
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

					if (!specificSet) {
						SQLiteConnection.replaceIntoCardSetMetaData(set_name, set_code, num_of_cards, tcg_date);
					}
					if (specificSet && set_name.equalsIgnoreCase(setName)) {
						SQLiteConnection.replaceIntoCardSetMetaData(set_name, set_code, num_of_cards, tcg_date);
						return;
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
