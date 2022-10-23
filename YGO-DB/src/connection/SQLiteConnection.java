package connection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import bean.CardSet;
import bean.OwnedCard;
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
	
	public static void closeInstance() throws SQLException {
		if (connection == null) {
			return;
		}
		
		connection.close();
		
		connection = null;
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
	
	public static ArrayList<CardSet> getAllRaritiesOfCardByID(int id) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "Select * from cardSets where wikiID=?";

		PreparedStatement statementSetQuery = connection.prepareStatement(setQuery);
		statementSetQuery.setInt(1, id);

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
	
	public static ArrayList<CardSet> getRaritiesOfCardInSetByID(int id, String setName) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "Select * from cardSets where wikiID=? and setName = ?";

		PreparedStatement statementSetQuery = connection.prepareStatement(setQuery);
		statementSetQuery.setInt(1, id);
		statementSetQuery.setString(2, setName);

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
	
	public static String getCardTitleFromID(int wikiID) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "Select * from gamePlayCard where wikiID=?";

		PreparedStatement statementSetQuery = connection.prepareStatement(setQuery);
		statementSetQuery.setInt(1, wikiID);

		ResultSet rarities = statementSetQuery.executeQuery();

		ArrayList<String> titlesFound = new ArrayList<String>();

		while (rarities.next()) {

			titlesFound.add(rarities.getString("title"));

		}

		statementSetQuery.close();
		rarities.close();

		if (titlesFound.size() == 1) {
			return titlesFound.get(0);
		}

		return null;
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
	
	public static ArrayList<OwnedCard> getNumberOfOwnedCardsById(int id) throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "select sum(quantity), cardName from ownedCards where wikiID = ? group by cardName";

		PreparedStatement setQueryStatement = connection.prepareStatement(setQuery);

		setQueryStatement.setInt(1, id);

		ResultSet rs = setQueryStatement.executeQuery();

		ArrayList<OwnedCard> cardsInSetList = new ArrayList<OwnedCard>();

		while (rs.next()) {
			
			OwnedCard current = new OwnedCard();
			
			current.id = id;
			current.quantity = rs.getInt(1);
			current.cardName = rs.getString(2);
			
			cardsInSetList.add(current);
		}

		rs.close();
		setQueryStatement.close();

		return cardsInSetList;
	}
	
	public static ArrayList<OwnedCard> getAllOwnedCards() throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "select * from ownedCards order by setName, setRarity, cardName";

		PreparedStatement setQueryStatement = connection.prepareStatement(setQuery);

		ResultSet rs = setQueryStatement.executeQuery();

		ArrayList<OwnedCard> cardsInSetList = new ArrayList<OwnedCard>();

		while (rs.next()) {
			
			OwnedCard current = new OwnedCard();
			
			current.id = rs.getInt("wikiID");
			current.rarityUnsure =rs.getInt("rarityUnsure");
			current.quantity = rs.getInt("quantity");
			current.cardName = rs.getString("cardName");
			current.setCode = rs.getString("setCode");
			current.setNumber = rs.getString("setNumber");
			current.setName = rs.getString("setName");
			current.setRarity = rs.getString("setRarity");
			current.colorVariant = rs.getString("setRarityColorVariant");
			current.folderName = rs.getString("folderName");
			current.condition = rs.getString("condition");
			current.editionPrinting = rs.getString("editionPrinting");
			current.dateBought = rs.getString("dateBought");
			current.priceBought = rs.getString("priceBought");
			current.creationDate = rs.getString("creationDate");
			current.modificationDate = rs.getString("modificationDate");
			
			
			cardsInSetList.add(current);
		}

		rs.close();
		setQueryStatement.close();

		return cardsInSetList;
	}
	
	public static ArrayList<OwnedCard> getRarityUnsureOwnedCards() throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "select * from ownedCards where rarityUnsure = 1 order by setName";

		PreparedStatement setQueryStatement = connection.prepareStatement(setQuery);

		ResultSet rs = setQueryStatement.executeQuery();

		ArrayList<OwnedCard> cardsInSetList = new ArrayList<OwnedCard>();

		while (rs.next()) {
			
			OwnedCard current = new OwnedCard();
			
			current.id = rs.getInt("wikiID");
			current.rarityUnsure =rs.getInt("rarityUnsure");
			current.quantity = rs.getInt("quantity");
			current.cardName = rs.getString("cardName");
			current.setCode = rs.getString("setCode");
			current.setNumber = rs.getString("setNumber");
			current.setName = rs.getString("setName");
			current.setRarity = rs.getString("setRarity");
			current.colorVariant = rs.getString("setRarityColorVariant");
			current.folderName = rs.getString("folderName");
			current.condition = rs.getString("condition");
			current.editionPrinting = rs.getString("editionPrinting");
			current.dateBought = rs.getString("dateBought");
			current.priceBought = rs.getString("priceBought");
			current.creationDate = rs.getString("creationDate");
			current.modificationDate = rs.getString("modificationDate");
			
			
			cardsInSetList.add(current);
		}

		rs.close();
		setQueryStatement.close();

		return cardsInSetList;
	}
	
	public static ArrayList<Integer> getDistinctCardIDsInSetByName(String setName) throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "select distinct wikiID from cardSets where setName = ?";

		PreparedStatement setQueryStatement = connection.prepareStatement(setQuery);

		setQueryStatement.setString(1, setName);

		ResultSet rs = setQueryStatement.executeQuery();

		ArrayList<Integer> cardsInSetList = new ArrayList<Integer>();

		while (rs.next()) {
			
			cardsInSetList.add(rs.getInt(1));
		}

		rs.close();
		setQueryStatement.close();

		return cardsInSetList;
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
	
	public static ArrayList<SetMetaData> getSetMetaDataFromSetCode(String setCode) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String distrinctQuery = "select setName,setCode,numOfCards,releaseDate  from setData where setCode = ?";

		PreparedStatement distrinctQueryStatement = connection.prepareStatement(distrinctQuery);
		
		distrinctQueryStatement.setString(1, setCode);

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
	
	public static void setStringOrNull(PreparedStatement p, int index, String s) throws SQLException {
		if(s == null) {
			p.setNull(index, Types.VARCHAR);
		}
		else {
			p.setString(index, s);
		}
	}
	
	public static void setIntegerOrNull(PreparedStatement p, int index, Integer value) throws SQLException {
		if(value == null) {
			p.setNull(index, Types.INTEGER);
		}
		else {
			p.setInt(index, value.intValue());
		}
	}

	public static void replaceIntoGamePlayCard(Integer wikiID, String name, String type, Integer passcode, String desc,
			String attribute, String race, Integer linkval, Integer level, Integer scale, Integer atk, Integer def,
			String archetype) throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String gamePlayCard = "Replace into gamePlayCard(wikiID,title,type,passcode,lore,attribute,race,linkValue,level,pendScale,atk,def,archetype) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		PreparedStatement statementgamePlayCard = connection.prepareStatement(gamePlayCard);

		setIntegerOrNull(statementgamePlayCard, 1, wikiID);
		setStringOrNull(statementgamePlayCard, 2, name);
		setStringOrNull(statementgamePlayCard, 3, type);
		setIntegerOrNull(statementgamePlayCard, 4, passcode);
		setStringOrNull(statementgamePlayCard, 5, desc);
		setStringOrNull(statementgamePlayCard, 6, attribute);
		setStringOrNull(statementgamePlayCard, 7, race);
		setIntegerOrNull(statementgamePlayCard, 8, linkval);
		setIntegerOrNull(statementgamePlayCard, 9, level);
		setIntegerOrNull(statementgamePlayCard, 10, scale);
		setIntegerOrNull(statementgamePlayCard, 11, atk);
		setIntegerOrNull(statementgamePlayCard, 12, def);
		setStringOrNull(statementgamePlayCard, 13, archetype);

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
		
		BigDecimal price = new BigDecimal(priceBought);
		
		price = price.setScale(2, RoundingMode.HALF_UP);
		
		String normalizedPrice = price.toString();

		String ownedInsert = "insert into ownedCards(wikiID,folderName,cardName,quantity,setCode,"
				+ "setNumber,setName,setRarity,setRarityColorVariant,condition,editionPrinting,dateBought"
				+ ",priceBought,rarityUnsure, creationDate, modificationDate) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ "datetime('now','localtime'),datetime('now','localtime'))"
				+ "on conflict (wikiID,folderName,setNumber,"
				+ "condition,editionPrinting,dateBought,priceBought) "
				+ "do update set quantity = ?, rarityUnsure = ?, setRarity = ?, setRarityColorVariant = ?, modificationDate = datetime('now','localtime')";

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
		statementOwnedCard.setString(13, normalizedPrice);
		statementOwnedCard.setInt(14, setIdentified.rarityUnsure);
		statementOwnedCard.setInt(15, new Integer(quantity));
		statementOwnedCard.setInt(16, setIdentified.rarityUnsure);
		statementOwnedCard.setString(17, setIdentified.setRarity);
		statementOwnedCard.setString(18, setIdentified.colorVariant);
		
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
