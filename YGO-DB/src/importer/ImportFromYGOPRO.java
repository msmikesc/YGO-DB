package importer;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Iterator;
import org.json.*;

import connection.SQLiteConnection;
import connection.Util;

import org.apache.commons.io.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ImportFromYGOPRO {

	public static void main(String[] args) throws SQLException, IOException {
		ImportFromYGOPRO mainObj = new ImportFromYGOPRO();
		mainObj.run();
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

	}

	

}
