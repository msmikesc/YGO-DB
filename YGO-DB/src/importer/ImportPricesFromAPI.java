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

public class ImportPricesFromAPI {

	public static void main(String[] args) throws SQLException, IOException {
		ImportPricesFromAPI mainObj = new ImportPricesFromAPI();
		//mainObj.run();
		mainObj.callAPIForSet("Magnificent Mavens");
		SQLiteConnection.closeInstance();
		System.out.println("Import Finished: " + count);
	}
	
	public static int count = 0;

	public void run() throws SQLException, IOException {

		String setAPI = "http://yugiohprices.com/api/card_sets";

		try {

			URL url = new URL(setAPI);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();

			String redirect = conn.getHeaderField("Location");
			if (redirect != null) {
				conn = (HttpURLConnection) new URL(redirect).openConnection();
			}

			// Getting the response code
			int responsecode = conn.getResponseCode();

			if (responsecode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responsecode);
			} else {

				String inline = "{\"data\": ";

				Scanner scanner = new Scanner(conn.getInputStream());

				// Write all the JSON data into a string using a scanner
				while (scanner.hasNext()) {
					inline += scanner.nextLine();
				}
				inline += "}";

				// Close the scanner
				scanner.close();

				JSONObject jo = new JSONObject(inline);

				inline = null;

				JSONArray setNames = (JSONArray) jo.get("data");

				Iterator<Object> keyset = setNames.iterator();

				while (keyset.hasNext()) {

					String current = (String) keyset.next();

					callAPIForSet(current);
					//break;
					Thread.sleep(500);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void callAPIForSet(String setName) throws SQLException, IOException {

		String setAPI = "http://yugiohprices.com/api/set_data/";

		try {

			URL url = new URL(setAPI + URLEncoder.encode(setName));

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();

			String redirect = conn.getHeaderField("Location");
			if (redirect != null) {
				conn = (HttpURLConnection) new URL(redirect).openConnection();
			}

			// Getting the response code
			int responsecode = conn.getResponseCode();

			if (responsecode != 200) {
				System.out.println("Unable to call for set name: " + setName);
				throw new RuntimeException("HttpResponseCode: " + responsecode);
			} else {

				String inline = "";

				Scanner scanner = new Scanner(conn.getInputStream());

				// Write all the JSON data into a string using a scanner
				while (scanner.hasNext()) {
					inline += scanner.nextLine();
				}

				// Close the scanner
				scanner.close();

				JSONObject jo = new JSONObject(inline);

				inline = null;

				JSONObject dataResponse = (JSONObject) jo.get("data");

				JSONArray cards = dataResponse.getJSONArray("cards");

				for (Object currCard : cards) {

					JSONObject currObjCard = (JSONObject) currCard;

					JSONArray numbers = currObjCard.getJSONArray("numbers");
					
					String card = currObjCard.getString("name");

					for (Object currNumber : numbers) {

						JSONObject currObjNum = (JSONObject) currNumber;
						String setNumber = currObjNum.getString("print_tag");
						String rarity = currObjNum.getString("rarity");
						
						rarity = Util.checkForTranslatedRarity(rarity);
						
						double avgPrice=0;
						try {
						avgPrice = currObjNum.getJSONObject("price_data").getJSONObject("data")
								.getJSONObject("prices").getDouble("low");
						}
						catch(Exception e) {
							//System.out.println("Can't get price for: "+setNumber + ":" + rarity);
							continue;
						}

						try {
							int updated = SQLiteConnection.updateCardSetPrice(setNumber, rarity, Util.normalizePrice("" + avgPrice));
							count++;
							
							if(updated != 1) {
								System.out.println(updated + " rows updated for: "+card+":"+setNumber + ":" + rarity + ":" + avgPrice);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Issue with: "+card+":"+setNumber + ":" + rarity + ":" + avgPrice);
						}

					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
