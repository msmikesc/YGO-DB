package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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

	public static void replaceIntoCardSetMetaData(String set_name, String set_code, int num_of_cards, String tcg_date)
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

	public static void replaceIntoGamePlayCard(int wikiID, String name, String type, int passcode, String desc,
			String attribute, String race, int linkval, int level, int scale, int atk, int def, String archetype)
			throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String gamePlayCard = "Replace into gamePlayCard(wikiID,title,type,passcode,lore,attribute,race,linkValue,level,pendScale,atk,def,archetype) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		PreparedStatement statementgamePlayCard = connection.prepareStatement(gamePlayCard);

		statementgamePlayCard.setInt(1, wikiID);
		statementgamePlayCard.setString(2, name);
		statementgamePlayCard.setString(3, type);
		statementgamePlayCard.setInt(4, passcode);
		statementgamePlayCard.setString(5, desc);
		statementgamePlayCard.setString(6, attribute);
		statementgamePlayCard.setString(7, race);
		statementgamePlayCard.setInt(8, linkval);
		statementgamePlayCard.setInt(9, level);
		statementgamePlayCard.setInt(10, scale);
		statementgamePlayCard.setInt(11, atk);
		statementgamePlayCard.setInt(12, def);
		statementgamePlayCard.setString(13, archetype);

		statementgamePlayCard.execute();

		statementgamePlayCard.close();
	}

	public static void upsertOwnedCard(String folder, String name, String quantity, String setCode, String condition,
			String printing, String priceBought, String dateBought, CardSet setIdentified) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		if (setIdentified.rarityUnsure != 1) {
			setIdentified.rarityUnsure = 0;
		}

		if (setIdentified.colorVariant == null) {
			setIdentified.colorVariant = "-1";
		}

		String ownedInsert = "insert into ownedCards(wikiID,folderName,cardName,quantity,setCode,"
				+ "setNumber,setName,setRarity,setRarityColorVariant,condition,editionPrinting,dateBought"
				+ ",priceBought,rarityUnsure, creationDate, modificationDate) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ "datetime('now','localtime'),datetime('now','localtime'))"
				+ "on conflict (wikiID,folderName,setNumber,setRarity,setRarityColorVariant,"
				+ "condition,editionPrinting,dateBought,priceBought) "
				+ "do update set quantity = ?, rarityUnsure = ?, modificationDate = datetime('now','localtime')";

		PreparedStatement statementOwnedCard = connection.prepareStatement(ownedInsert);

		int cardID = setIdentified.id;

		statementOwnedCard.setInt(1, cardID);
		statementOwnedCard.setString(2, folder);
		statementOwnedCard.setString(3, name);
		statementOwnedCard.setInt(4, new Integer(quantity));
		statementOwnedCard.setString(5, setCode);
		statementOwnedCard.setString(6, setIdentified.setNumber);
		statementOwnedCard.setString(7, setIdentified.setName);
		statementOwnedCard.setString(8, setIdentified.setRarity);
		statementOwnedCard.setString(9, setIdentified.colorVariant);
		statementOwnedCard.setString(10, condition);
		statementOwnedCard.setString(11, printing);
		statementOwnedCard.setString(12, dateBought);
		statementOwnedCard.setString(13, priceBought);
		statementOwnedCard.setInt(14, setIdentified.rarityUnsure);
		statementOwnedCard.setInt(15, new Integer(quantity));
		statementOwnedCard.setInt(16, setIdentified.rarityUnsure);

		statementOwnedCard.execute();

		statementOwnedCard.close();
	}

	public static void replaceIntoCardSet(String cardNumber, String rarity, String setName, int wikiID, String price)
			throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setInsert = "replace into cardSets(wikiID,setNumber,setName,setRarity,setPrice) values(?,?,?,?,?)";

		PreparedStatement statementSetInsert = connection.prepareStatement(setInsert);

		statementSetInsert.setInt(1, wikiID);
		statementSetInsert.setString(2, cardNumber);
		statementSetInsert.setString(3, setName);
		statementSetInsert.setString(4, rarity);
		statementSetInsert.setString(5, price);

		statementSetInsert.execute();
		statementSetInsert.close();
	}

}
