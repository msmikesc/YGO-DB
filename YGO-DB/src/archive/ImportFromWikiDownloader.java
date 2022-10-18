package archive;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Iterator;
import org.json.*;
import org.apache.commons.io.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImportFromWikiDownloader {

	public static void main(String[] args) throws SQLException {

		Connection connection = null;

		try {
			connection = DriverManager
					.getConnection("jdbc:sqlite:C:\\Users\\Mike\\eclipse-workspace\\ygodb\\YGO-DB\\YGO-DB.db");

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		File f = new File("C:\\Users\\Mike\\Documents\\GitHub\\Yugioh-Database-Downloader\\src\\cards.json");
		String s = null;
		try {
			s = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject jo = new JSONObject(s);

		s = null;

		JSONArray cards = (JSONArray) jo.get("cards");

		Iterator<Object> keyset = cards.iterator();

		while (keyset.hasNext()) {

			JSONObject current = (JSONObject) keyset.next();

			String gamePlayCard = "Replace into gamePlayCard(wikiID,title,wikiUrl,image,passcode,lore,pendulumEffect,firstMaterial,attribute,link_arrows,level,rank,atk,def,statusTcgAdv,statusTcgTrad,statusOcg) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			PreparedStatement statementgamePlayCard = connection.prepareStatement(gamePlayCard);

			int cardID = current.getInt("id");

			statementgamePlayCard.setInt(1, cardID);

			setStringOrNull(statementgamePlayCard, 2, current, "title");
			setStringOrNull(statementgamePlayCard, 3, current, "wikiUrl");
			setStringOrNull(statementgamePlayCard, 4, current, "image");
			setStringOrNull(statementgamePlayCard, 5, current, "passcode");
			setStringOrNull(statementgamePlayCard, 6, current, "lore");
			setStringOrNull(statementgamePlayCard, 7, current, "pendulumEffect");
			setStringOrNull(statementgamePlayCard, 8, current, "fm");
			setStringOrNull(statementgamePlayCard, 9, current, "attribute");
			setStringOrNull(statementgamePlayCard, 10, current, "link_arrows");
			setStringOrNull(statementgamePlayCard, 11, current, "level");
			setStringOrNull(statementgamePlayCard, 12, current, "rank");
			setStringOrNull(statementgamePlayCard, 13, current, "atk");
			setStringOrNull(statementgamePlayCard, 14, current, "def");
			setStringOrNull(statementgamePlayCard, 15, current, "statusTcgAdv");
			setStringOrNull(statementgamePlayCard, 16, current, "statusTcgTrad");
			setStringOrNull(statementgamePlayCard, 17, current, "statusOcg");

			statementgamePlayCard.execute();

			String deleteMaterials = "delete from gamePlayCardMaterials where wikiID = ?";
			String gamePlayCardMaterials = "Insert into gamePlayCardMaterials(wikiID,material) values(?,?)";

			JSONArray materials = null;
			boolean isMaterials = false;

			try {
				materials = current.getJSONArray("materials");
				isMaterials = true;
			} catch (JSONException e) {

			}

			if (isMaterials) {
				PreparedStatement statementDeleteMaterials = connection.prepareStatement(deleteMaterials);
				statementDeleteMaterials.setInt(1, cardID);

				statementDeleteMaterials.execute();

				for (int i = 0; i < materials.length(); i++) {
					PreparedStatement statementInsertMaterials = connection.prepareStatement(gamePlayCardMaterials);
					statementInsertMaterials.setInt(1, cardID);
					statementInsertMaterials.setString(2, materials.getString(i));
					statementInsertMaterials.execute();
				}
			}

			String deleteTypes = "delete from gamePlayCardTypes where wikiID = ?";
			String gamePlayCardTypes = "Insert into gamePlayCardTypes(wikiID,type) values(?,?)";

			JSONArray types = null;
			boolean isTypes = false;

			try {
				types = current.getJSONArray("types");
				isTypes = true;
			} catch (JSONException e) {

			}

			if (isTypes) {
				PreparedStatement statementDeleteTypes = connection.prepareStatement(deleteTypes);
				statementDeleteTypes.setInt(1, cardID);

				statementDeleteTypes.execute();

				for (int i = 0; i < types.length(); i++) {
					PreparedStatement statementInsertTypes = connection.prepareStatement(gamePlayCardTypes);
					statementInsertTypes.setInt(1, cardID);
					statementInsertTypes.setString(2, types.getString(i));
					statementInsertTypes.execute();
				}
			}

			String cardSets = "Replace into cardSets(wikiID,setNumber,setName,setRarity) values(?,?,?,?)";

			JSONArray sets = null;
			Iterator<Object> setIteraor = null;
			boolean isSets = false;

			try {
				sets = current.getJSONArray("sets");
				setIteraor = sets.iterator();
				isSets = true;
			} catch (JSONException e) {

			}

			if (isSets) {

				for (int i = 0; i < sets.length(); i++) {
					PreparedStatement statementInsertSets = connection.prepareStatement(cardSets);

					JSONObject currentSet = (JSONObject) setIteraor.next();

					statementInsertSets.setInt(1, cardID);

					setStringOrNull(statementInsertSets, 2, currentSet, "number");
					setStringOrNull(statementInsertSets, 3, currentSet, "setName");
					setStringOrNull(statementInsertSets, 4, currentSet, "rarity");

					statementInsertSets.execute();
				}
			}

		}

	}

	static private void setStringOrNull(PreparedStatement s, int index, JSONObject current, String id)
			throws SQLException {

		String value = null;

		try {
			value = current.getString(id);
		} catch (JSONException e) {

		}

		s.setString(index, value);

	}

}
