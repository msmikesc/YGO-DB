package importer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.CardSet;
import connection.SQLiteConnection;
import connection.Util;

public class ImportPricesFromYGOPROAPI {
	
	HashMap<String, List<String>> NameUpdateMap = new HashMap<String, List<String>>();

	public static void main(String[] args) throws SQLException, IOException {
		ImportPricesFromYGOPROAPI mainObj = new ImportPricesFromYGOPROAPI();
		mainObj.run();
		SQLiteConnection.closeInstance();
		System.out.println("Import Finished");
	}

	public void run() throws SQLException, IOException {

		//String setName = "OTS Tournament Pack 21";

		//setName = setName.trim();

		String setAPI = "https://db.ygoprodeck.com/api/v7/cardinfo.php?tcgplayer_data=true";

		//String apiURL = setAPI + URLEncoder.encode(setName);

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
				InputStream inputStreamFromURL = url.openStream();

				ByteArrayOutputStream result = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				for (int length; (length = inputStreamFromURL.read(buffer)) != -1; ) {
				    result.write(buffer, 0, length);
				}
				inline = result.toString("UTF-8");

				JSONObject jo = new JSONObject(inline);

				inline = null;

				JSONArray cards = (JSONArray) jo.get("data");
				
				System.out.println("Getting data from API complete");

				Iterator<Object> keyset = cards.iterator();

				while (keyset.hasNext()) {

					JSONObject current = (JSONObject) keyset.next();

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
				
				List<String> namesList = new ArrayList<String>(NameUpdateMap.keySet());
				
				for(int i = 0; i < namesList.size(); i++) {
					String setName = namesList.get(i);
					System.out.println("Possibly need to handle set name issue count: " + NameUpdateMap.get(setName).size() + " " + setName );
					/*
					for(int j = 0; j < NameUpdateMap.get(setName).size(); j++) {
						System.out.println(NameUpdateMap.get(setName).get(j));
					}
					*/
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertCardSetsForOneCard(JSONArray sets, Iterator<Object> setIteraor, String name, int wikiID)
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
				//set_rarity_code = currentSet.getString("set_rarity_code");
				//set_edition = currentSet.getString("set_edition");
				//set_url = currentSet.getString("set_url");
			} catch (Exception e) {
				System.out.println("issue found on " + name);
				continue;
			}

			set_rarity = Util.checkForTranslatedRarity(set_rarity);
			
			set_name = Util.checkForTranslatedSetName(set_name);
			
			set_price = Util.normalizePrice(set_price);
			
			if(!set_price.equals("0.00")){
				int updated = SQLiteConnection.updateCardSetPriceWithSetName(set_code, set_rarity, set_price, set_name);
				
				if(updated == 0) {
				
					updated = SQLiteConnection.updateCardSetPrice(set_code, set_rarity, set_price);
					
					if(updated == 0) {
						ArrayList<CardSet> list = SQLiteConnection.getAllCardSetsOfCardBySetNumber(set_code);
						
						if(list.size() == 1) {
							updated = SQLiteConnection.updateCardSetPrice(set_code, set_price);
						}
						
					}
					else {
						
						List<String> setNamesList = NameUpdateMap.get(set_name);
						
						if(setNamesList == null) {
							setNamesList = new ArrayList<String>();
							NameUpdateMap.put(set_name, setNamesList);
						}
						setNamesList.add(name);
					}
				}
				
				if(updated != 1) {
					System.out.println(updated + " rows updated for: "+name+":"+set_code + ":" + set_rarity + ":" + set_price+":"+set_name);
				}
			}
		}
	}
}
