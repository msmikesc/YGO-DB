package importer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import org.json.*;

import connection.SQLiteConnection;
import connection.Util;

import org.apache.commons.io.*;
import java.sql.SQLException;

public class ImportFromYGOPRO {

	public static void main(String[] args) throws SQLException, IOException {
		ImportFromYGOPRO mainObj = new ImportFromYGOPRO();
		mainObj.run();
		SQLiteConnection.closeInstance();
	}

	public void run() throws SQLException, IOException {

		File f = new File("C:\\Users\\Mike\\Documents\\GitHub\\YGO-DB\\YGO-DB\\csv\\cardinfo.json");
		String s = null;
		try {
			s = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject jo = new JSONObject(s);

		s = null;

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

	}

	public static void insertGameplayCardFromYGOPRO(JSONObject current) throws SQLException {

		Integer wikiID = Util.getIntOrNull(current, "id");
		String name = Util.getStringOrNull(current, "name");
		String type = Util.getStringOrNull(current, "type");
		Integer passcode = Util.getIntOrNull(current, "id");// passcode
		String desc = Util.getStringOrNull(current, "desc");
		String attribute = Util.getStringOrNull(current, "attribute");
		String race = Util.getStringOrNull(current, "race");
		Integer linkval = Util.getIntOrNull(current, "linkval");
		Integer level = Util.getIntOrNull(current, "level");
		Integer scale = Util.getIntOrNull(current, "scale");
		Integer atk = Util.getIntOrNull(current, "atk");
		Integer def = Util.getIntOrNull(current, "def");
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

}
