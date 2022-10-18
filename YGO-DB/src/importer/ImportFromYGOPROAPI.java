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
			
			Util.updateDBWithSetsFromAPI(setName);
			
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

					SQLiteConnection.insertGameplayCardFromYGOPRO(current);

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
						SQLiteConnection.insertCardSetsForOneCard(sets, setIteraor, name, cardID);
					}

				}
				
				Util.checkForIssuesWithSet(setName);
				Util.checkSetCounts();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
