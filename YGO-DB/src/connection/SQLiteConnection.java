package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.CardSet;
import bean.SetMetaData;

public class SQLiteConnection {

	private static Connection connection = null;

	public static Connection getInstance() throws SQLException {
		if (connection == null) {
			connection = DriverManager
					.getConnection("jdbc:sqlite:C:\\Users\\Mike\\Documents\\GitHub\\YGO-DB\\YGO-DB\\YGO-DB.db");
		}

		return connection;
	}

	public static ArrayList<CardSet> getExistingOwnedRaritesForCard(String cardNumber, String priceBought,
			String dateBought, String folderName, String condition, String editionPrinting) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		// check for exact same entry
		String dupeQuery = "Select * from ownedCards where setNumber=? and priceBought = ? and dateBought = ? and folderName = ? and condition = ? and editionPrinting = ?";

		PreparedStatement statementDupeQuery = connection.prepareStatement(dupeQuery);
		statementDupeQuery.setString(1, cardNumber);
		statementDupeQuery.setString(2, priceBought);
		statementDupeQuery.setString(3, dateBought);
		statementDupeQuery.setString(4, folderName);
		statementDupeQuery.setString(5, condition);
		statementDupeQuery.setString(6, editionPrinting);

		ResultSet ownedList = statementDupeQuery.executeQuery();

		ArrayList<CardSet> ownedRarities = new ArrayList<CardSet>();

		while (ownedList.next()) {
			CardSet set = new CardSet();
			set.id = ownedList.getInt("wikiID");
			set.setNumber = ownedList.getString("setNumber");
			set.setName = ownedList.getString("setName");
			set.setRarity = ownedList.getString("setRarity");
			set.colorVariant = ownedList.getString("setRarityColorVariant");
			set.rarityUnsure = ownedList.getInt("rarityUnsure");

			ownedRarities.add(set);
		}

		statementDupeQuery.close();
		ownedList.close();
		return ownedRarities;
	}

	public static ArrayList<CardSet> getRaritiesOfCardInSet(String setNumber) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "Select * from cardSets where setNumber=?";

		PreparedStatement statementSetQuery = connection.prepareStatement(setQuery);
		statementSetQuery.setString(1, setNumber);

		ResultSet rarities = statementSetQuery.executeQuery();

		ArrayList<CardSet> setRarities = new ArrayList<CardSet>();

		while (rarities.next()) {
			CardSet set = new CardSet();
			set.id = rarities.getInt("wikiID");
			set.setNumber = rarities.getString("setNumber");
			set.setName = rarities.getString("setName");
			set.setRarity = rarities.getString("setRarity");
			set.setPrice = rarities.getString("setPrice");

			setRarities.add(set);
		}

		statementSetQuery.close();
		rarities.close();

		return setRarities;
	}

	public static int getCardIdFromTitle(String title) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "Select * from gamePlayCard where title=?";

		PreparedStatement statementSetQuery = connection.prepareStatement(setQuery);
		statementSetQuery.setString(1, title);

		int id = -1;

		ResultSet rarities = statementSetQuery.executeQuery();

		ArrayList<Integer> idsFound = new ArrayList<Integer>();

		while (rarities.next()) {

			idsFound.add(rarities.getInt("wikiID"));

		}

		statementSetQuery.close();
		rarities.close();

		if (idsFound.size() == 1) {
			return idsFound.get(0);
		}

		return id;
	}

	public static ArrayList<String> getSortedCardsInSetByName(String setName) throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "select setNumber from cardSets where setName = ?";

		PreparedStatement setQueryStatement = connection.prepareStatement(setQuery);

		setQueryStatement.setString(1, setName);

		ResultSet rs = setQueryStatement.executeQuery();

		ArrayList<String> cardsInSetList = new ArrayList<String>();

		while (rs.next()) {
			cardsInSetList.add(rs.getString(1));
		}

		rs.close();
		setQueryStatement.close();

		Collections.sort(cardsInSetList);
		return cardsInSetList;
	}

	public static ArrayList<String> getDistinctSetNames() throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String distrinctQuery = "select distinct setName from cardSets";

		PreparedStatement distrinctQueryStatement = connection.prepareStatement(distrinctQuery);

		ResultSet rs = distrinctQueryStatement.executeQuery();

		ArrayList<String> setsList = new ArrayList<String>();

		while (rs.next()) {
			setsList.add(rs.getString(1));
		}

		rs.close();
		distrinctQueryStatement.close();

		return setsList;
	}

	public static int getCountDistinctCardsInSet(String setName) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String distrinctQuery = "select count (distinct setNumber) from cardSets where setName = ?";

		PreparedStatement distrinctQueryStatement = connection.prepareStatement(distrinctQuery);

		distrinctQueryStatement.setString(1, setName);

		ResultSet rs = distrinctQueryStatement.executeQuery();

		int results = -1;

		while (rs.next()) {
			results = rs.getInt(1);
		}

		rs.close();
		distrinctQueryStatement.close();

		return results;
	}

	public static ArrayList<SetMetaData> getSetMetaDataFromSetData() throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String distrinctQuery = "select distinct setName,setCode,numOfCards,releaseDate  from setData";

		PreparedStatement distrinctQueryStatement = connection.prepareStatement(distrinctQuery);

		ResultSet rs = distrinctQueryStatement.executeQuery();

		ArrayList<SetMetaData> setsList = new ArrayList<SetMetaData>();

		while (rs.next()) {

			SetMetaData current = new SetMetaData();
			current.set_name = rs.getString(1);
			current.set_code = rs.getString(2);
			current.num_of_cards = rs.getInt(3);
			current.tcg_date = rs.getString(4);

			setsList.add(current);
		}

		rs.close();
		distrinctQueryStatement.close();

		return setsList;
	}

	public static void insertCardSetsForOneCard(JSONArray sets, Iterator<Object> setIteraor, String name, int wikiID)
			throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String cardSets = "Replace into cardSets(wikiID,setNumber,setName,setRarity,setPrice) values(?,?,?,?,?)";

		for (int i = 0; i < sets.length(); i++) {
			PreparedStatement statementInsertSets = connection.prepareStatement(cardSets);

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

			statementInsertSets.setInt(1, wikiID);
			statementInsertSets.setString(2, set_code);
			statementInsertSets.setString(3, set_name);
			statementInsertSets.setString(4, set_rarity);
			statementInsertSets.setString(5, set_price);

			statementInsertSets.execute();

			statementInsertSets.close();
		}
	}

	public static void insertCardSet(String set_name, String set_code, int num_of_cards, String tcg_date)
			throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String cardSets = "Replace into setData(setName,setCode,numOfCards,releaseDate) values(?,?,?,?)";

		PreparedStatement statementInsertSets = connection.prepareStatement(cardSets);

		statementInsertSets.setString(1, set_name);
		statementInsertSets.setString(2, set_code);
		statementInsertSets.setInt(3, num_of_cards);
		statementInsertSets.setString(4, tcg_date);

		statementInsertSets.execute();

		statementInsertSets.close();

	}

	public static void insertGameplayCardFromYGOPRO(JSONObject current) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String gamePlayCard = "Replace into gamePlayCard(wikiID,title,type,passcode,lore,attribute,race,linkValue,level,pendScale,atk,def,archetype) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		PreparedStatement statementgamePlayCard = connection.prepareStatement(gamePlayCard);

		int cardID = current.getInt("id");

		statementgamePlayCard.setInt(1, cardID);

		setStringOrNull(statementgamePlayCard, 2, current, "name");
		setStringOrNull(statementgamePlayCard, 3, current, "type");
		setIntOrNull(statementgamePlayCard, 4, current, "id");// passcode
		setStringOrNull(statementgamePlayCard, 5, current, "desc");
		setStringOrNull(statementgamePlayCard, 6, current, "attribute");
		setStringOrNull(statementgamePlayCard, 7, current, "race");
		setIntOrNull(statementgamePlayCard, 8, current, "linkval");
		setIntOrNull(statementgamePlayCard, 9, current, "level");
		setIntOrNull(statementgamePlayCard, 10, current, "scale");
		setIntOrNull(statementgamePlayCard, 11, current, "atk");
		setIntOrNull(statementgamePlayCard, 12, current, "def");
		setStringOrNull(statementgamePlayCard, 13, current, "archetype");

		statementgamePlayCard.execute();

		statementgamePlayCard.close();
	}

	static private void setStringOrNull(PreparedStatement s, int index, JSONObject current, String id)
			throws SQLException {

		String value = null;

		try {
			value = current.getString(id);
			s.setString(index, value);
		} catch (JSONException e) {
			s.setNull(index, Types.VARCHAR);
		}
	}

	static private void setIntOrNull(PreparedStatement s, int index, JSONObject current, String id)
			throws SQLException {

		int value;

		try {
			value = current.getInt(id);
			s.setInt(index, value);
		} catch (JSONException e) {
			s.setNull(index, Types.INTEGER);
		}
	}
}
