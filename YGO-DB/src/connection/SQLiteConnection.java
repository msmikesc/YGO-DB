package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import bean.AnalyzePrintedOnceData;
import bean.CardSet;
import bean.GamePlayCard;
import bean.OwnedCard;
import bean.SetMetaData;

public class SQLiteConnection {

	private static Connection connection = null;

	public static Connection getInstance() throws SQLException {
		if (connection == null) {
			connection = DriverManager
					.getConnection("jdbc:sqlite:C:\\Users\\Mike\\Documents\\GitHub\\YGO-DB\\YGO-DB\\db\\YGO-DB.db");
		}

		return connection;
	}

	public static void closeInstance() throws SQLException {
		if (batchUpsertOwnedCard != null) {
			batchUpsertOwnedCard.executeBatch();
			batchUpsertOwnedCard.close();
			batchUpsertOwnedCard = null;
		}

		if (connection == null) {
			return;
		}

		connection.close();

		connection = null;
	}

	public static HashMap<String, ArrayList<CardSet>> getAllCardRarities() throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "Select * from cardSets";

		PreparedStatement statementSetQuery = connection.prepareStatement(setQuery);
		ResultSet rarities = statementSetQuery.executeQuery();

		HashMap<String, ArrayList<CardSet>> setRarities = new HashMap<String, ArrayList<CardSet>>();

		while (rarities.next()) {
			CardSet set = new CardSet();
			set.id = rarities.getInt("wikiID");
			set.cardName = rarities.getString("cardName");
			set.setNumber = rarities.getString("setNumber");
			set.setName = rarities.getString("setName");
			set.setRarity = rarities.getString("setRarity");
			set.setPrice = rarities.getString("setPrice");

			ArrayList<CardSet> currentList = setRarities.get(set.setNumber);

			if (currentList == null) {
				currentList = new ArrayList<CardSet>();
				setRarities.put(set.setNumber, currentList);
			}

			currentList.add(set);
		}

		statementSetQuery.close();
		rarities.close();

		return setRarities;
	}

	public static ArrayList<CardSet> getAllRaritiesOfCardByID(int id) throws SQLException {
		//TODO add name?

		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "Select * from cardSets where wikiID=?";

		PreparedStatement statementSetQuery = connection.prepareStatement(setQuery);
		statementSetQuery.setInt(1, id);

		ResultSet rarities = statementSetQuery.executeQuery();

		ArrayList<CardSet> setRarities = new ArrayList<CardSet>();

		while (rarities.next()) {
			CardSet set = new CardSet();
			set.id = rarities.getInt("wikiID");
			set.cardName = rarities.getString("cardName");
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
	
	public static ArrayList<CardSet> getAllCardSetsOfCardByIDAndSet(int id, String setName) throws SQLException {

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
			set.cardName = rarities.getString("cardName");
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
	
	public static ArrayList<CardSet> getAllCardSetsOfCardBySetNumber(String setNumber) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "Select * from cardSets where setNumber = ?";

		PreparedStatement statementSetQuery = connection.prepareStatement(setQuery);
		statementSetQuery.setString(1, setNumber);

		ResultSet rarities = statementSetQuery.executeQuery();

		ArrayList<CardSet> setRarities = new ArrayList<CardSet>();

		while (rarities.next()) {
			CardSet set = new CardSet();
			set.id = rarities.getInt("wikiID");
			set.cardName = rarities.getString("cardName");
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
		//TODO add name?

		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "Select * from cardSets a left join gamePlayCard b on a.wikiID = b.wikiID and b.title = a.cardName where a.wikiID=? and a.setName = ?";

		PreparedStatement statementSetQuery = connection.prepareStatement(setQuery);
		statementSetQuery.setInt(1, id);
		statementSetQuery.setString(2, setName);

		ResultSet rarities = statementSetQuery.executeQuery();

		ArrayList<CardSet> setRarities = new ArrayList<CardSet>();

		while (rarities.next()) {
			CardSet set = new CardSet();
			set.id = rarities.getInt("wikiID");
			set.cardName = rarities.getString("cardName");
			set.setNumber = rarities.getString("setNumber");
			set.setName = rarities.getString("setName");
			set.setRarity = rarities.getString("setRarity");
			set.setPrice = rarities.getString("setPrice");
			set.cardType = rarities.getString("type");

			setRarities.add(set);
		}

		statementSetQuery.close();
		rarities.close();

		return setRarities;
	}
	
	public static ArrayList<CardSet> getRaritiesOfCardInSetByIDAndName(int id, String setName, String cardName) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "Select * from cardSets a left join gamePlayCard b on a.wikiID = b.wikiID and b.title = a.cardName where a.wikiID=? and UPPER(a.setName) = UPPER(?) and UPPER(a.cardName) = UPPER(?)";

		PreparedStatement statementSetQuery = connection.prepareStatement(setQuery);
		statementSetQuery.setInt(1, id);
		statementSetQuery.setString(2, setName);
		statementSetQuery.setString(3, cardName);

		ResultSet rarities = statementSetQuery.executeQuery();

		ArrayList<CardSet> setRarities = new ArrayList<CardSet>();

		while (rarities.next()) {
			CardSet set = new CardSet();
			set.id = rarities.getInt("wikiID");
			set.cardName = rarities.getString("cardName");
			set.setNumber = rarities.getString("setNumber");
			set.setName = rarities.getString("setName");
			set.setRarity = rarities.getString("setRarity");
			set.setPrice = rarities.getString("setPrice");
			set.cardType = rarities.getString("type");

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
	
	public static ArrayList<String> getMultiCardTitlesFromID(int wikiID) throws SQLException {

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

		return titlesFound;
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
		//TODO add name?
		
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
			current.rarityUnsure = rs.getInt("rarityUnsure");
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
			current.priceBought = Util.normalizePrice(rs.getString("priceBought"));
			current.creationDate = rs.getString("creationDate");
			current.modificationDate = rs.getString("modificationDate");
			
			current.priceLow = rs.getString("priceLow");
			current.priceMid = rs.getString("priceMid");
			current.priceMarket = rs.getString("priceMarket");
			
			current.UUID = rs.getString("UUID");

			cardsInSetList.add(current);
		}

		rs.close();
		setQueryStatement.close();

		return cardsInSetList;
	}
	
	public static ArrayList<OwnedCard> getAllOwnedCardsWithoutSetCode() throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "select * from ownedCards where setCode is null";

		PreparedStatement setQueryStatement = connection.prepareStatement(setQuery);

		ResultSet rs = setQueryStatement.executeQuery();

		ArrayList<OwnedCard> cardsInSetList = new ArrayList<OwnedCard>();

		while (rs.next()) {

			OwnedCard current = new OwnedCard();

			current.id = rs.getInt("wikiID");
			current.rarityUnsure = rs.getInt("rarityUnsure");
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
			current.priceBought = Util.normalizePrice(rs.getString("priceBought"));
			current.creationDate = rs.getString("creationDate");
			current.modificationDate = rs.getString("modificationDate");
			
			current.priceLow = rs.getString("priceLow");
			current.priceMid = rs.getString("priceMid");
			current.priceMarket = rs.getString("priceMarket");
			
			current.UUID = rs.getString("UUID");

			cardsInSetList.add(current);
		}

		rs.close();
		setQueryStatement.close();

		return cardsInSetList;
	}

	public static HashMap<String, ArrayList<OwnedCard>> getAllOwnedCardsForHashMap() throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "select * from ownedCards order by setName, setRarity, cardName";

		PreparedStatement setQueryStatement = connection.prepareStatement(setQuery);

		ResultSet rs = setQueryStatement.executeQuery();

		HashMap<String, ArrayList<OwnedCard>> ownedCards = new HashMap<String, ArrayList<OwnedCard>>();

		while (rs.next()) {

			OwnedCard current = new OwnedCard();

			current.id = rs.getInt("wikiID");
			current.rarityUnsure = rs.getInt("rarityUnsure");
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
			current.priceBought = Util.normalizePrice(rs.getString("priceBought"));
			current.creationDate = rs.getString("creationDate");
			current.modificationDate = rs.getString("modificationDate");
			
			current.priceLow = rs.getString("priceLow");
			current.priceMid = rs.getString("priceMid");
			current.priceMarket = rs.getString("priceMarket");
			
			current.UUID = rs.getString("UUID");

			String key = current.setNumber + current.priceBought + current.dateBought + current.folderName
					+ current.condition + current.editionPrinting;

			ArrayList<OwnedCard> currentList = ownedCards.get(key);

			if (currentList == null) {
				currentList = new ArrayList<OwnedCard>();
				ownedCards.put(key, currentList);
			}

			currentList.add(current);
		}

		rs.close();
		setQueryStatement.close();

		return ownedCards;
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
			current.rarityUnsure = rs.getInt("rarityUnsure");
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
			current.priceBought = Util.normalizePrice(rs.getString("priceBought"));
			current.creationDate = rs.getString("creationDate");
			current.modificationDate = rs.getString("modificationDate");
			
			current.priceLow = rs.getString("priceLow");
			current.priceMid = rs.getString("priceMid");
			current.priceMarket = rs.getString("priceMarket");
			
			current.UUID = rs.getString("UUID");

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
	
	public static ArrayList<Integer> getDistinctCardIDsByArchetype(String archetype) throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String setQuery = "select distinct wikiID from gamePlayCard where archetype = ?";

		PreparedStatement setQueryStatement = connection.prepareStatement(setQuery);

		setQueryStatement.setString(1, archetype);

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
	
	public static int getCountQuantity() throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String query = "select sum(quantity) from ownedcards where ownedcards.folderName <> 'Manual Folder'";

		PreparedStatement queryStatement = connection.prepareStatement(query);

		ResultSet rs = queryStatement.executeQuery();

		int results = -1;

		while (rs.next()) {
			results = rs.getInt(1);
		}

		rs.close();
		queryStatement.close();

		return results;
	}
	
	public static int getCountQuantityManual() throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String query = "select sum(quantity) from ownedcards where ownedcards.folderName = 'Manual Folder'";

		PreparedStatement queryStatement = connection.prepareStatement(query);

		ResultSet rs = queryStatement.executeQuery();

		int results = -1;

		while (rs.next()) {
			results = rs.getInt(1);
		}

		rs.close();
		queryStatement.close();

		return results;
	}
	
	public static CardSet getFirstCardSetForCardInSet(String cardName, String setName) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String distrinctQuery = "select * from cardSets where UPPER(setName) = UPPER(?) and UPPER(cardName) = UPPER(?)";

		PreparedStatement distrinctQueryStatement = connection.prepareStatement(distrinctQuery);

		distrinctQueryStatement.setString(1, setName);
		distrinctQueryStatement.setString(2, cardName);

		ResultSet rs = distrinctQueryStatement.executeQuery();
		
		CardSet set = null;
		
		while (rs.next()) {
			set = new CardSet();
			set.id = rs.getInt("wikiID");
			set.cardName = rs.getString("cardName");
			set.setNumber = rs.getString("setNumber");
			set.setName = rs.getString("setName");
			set.setRarity = rs.getString("setRarity");
			set.setPrice = rs.getString("setPrice");
			break;
		}

		rs.close();
		distrinctQueryStatement.close();

		return set;
	}
	
	public static ArrayList<SetMetaData> getSetMetaDataFromSetName(String setName) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String distrinctQuery = "select setName,setCode,numOfCards,releaseDate  from setData where UPPER(setName) = UPPER(?)";

		PreparedStatement distrinctQueryStatement = connection.prepareStatement(distrinctQuery);

		distrinctQueryStatement.setString(1, setName);

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

	public static ArrayList<SetMetaData> getAllSetMetaDataFromSetData() throws SQLException {

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
	
	public static HashMap<String, AnalyzePrintedOnceData> getCardsOnlyPrintedOnce() throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String distrinctQuery = "select cardSets.wikiid, cardname, type, setNumber,setRarity, cardSets.setName, releaseDate, archetype from cardSets join setData on setData.setName = cardSets.setName \r\n"
				+ "join gamePlayCard on cardSets.cardName = gamePlayCard.title and gamePlayCard.wikiID = cardSets.wikiID\r\n"
				+ "where cardName in (select cardName from \r\n"
				+ "(Select DISTINCT cardName, setName from cardSets join gamePlayCard on gamePlayCard.title = cardSets.cardName and gamePlayCard.wikiid = cardSets.wikiID where type <>'Token') \r\n"
				+ "group by cardname having count(cardname) = 1) \r\n"
				+ "order by releaseDate";

		PreparedStatement distrinctQueryStatement = connection.prepareStatement(distrinctQuery);

		ResultSet rs = distrinctQueryStatement.executeQuery();

		HashMap<String, AnalyzePrintedOnceData> setsList = new HashMap<String, AnalyzePrintedOnceData>();

		while (rs.next()) {
			
			int wikiID = rs.getInt("wikiID");
			
			String cardName = rs.getString("cardname");
			String type = rs.getString("type");
			String setNumber = rs.getString("setNumber");
			String setRarity = rs.getString("setRarity");
			String setName = rs.getString("setName");
			String releaseDate = rs.getString("releaseDate");
			String archetype = rs.getString("archetype");
			
			
			AnalyzePrintedOnceData current = setsList.get(cardName);
			
			if(current == null) {
				current = new AnalyzePrintedOnceData();
				current.wikiID = wikiID;
				current.cardName = cardName;
				current.cardType = type;
				current.releaseDate = releaseDate;
				current.archetype = archetype;
			}

			
			current.setNumber.add(setNumber);
			current.setRarities.add(setRarity);
			current.setName.add(setName);

			setsList.put(cardName,current);
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
		if (s == null) {
			p.setNull(index, Types.VARCHAR);
		} else {
			p.setString(index, s);
		}
	}

	public static void setIntegerOrNull(PreparedStatement p, int index, Integer value) throws SQLException {
		if (value == null) {
			p.setNull(index, Types.INTEGER);
		} else {
			p.setInt(index, value.intValue());
		}
	}
	
	public static GamePlayCard getGamePlayCardByNameAndID(Integer wikiID, String name) throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String gamePlayCard = "select * from gamePlayCard where wikiID = ? and UPPER(title) = UPPER(?)";

		PreparedStatement statementgamePlayCard = connection.prepareStatement(gamePlayCard);

		setIntegerOrNull(statementgamePlayCard, 1, wikiID);
		setStringOrNull(statementgamePlayCard, 2, name);
		
		ResultSet rs = statementgamePlayCard.executeQuery();

		GamePlayCard current = new GamePlayCard();
		
		if(rs.next() == false) {
			return null;
		}

		current.wikiID = rs.getInt("wikiID");
		current.cardName = rs.getString("title");
		current.cardType = rs.getString("type");
		current.passcode = rs.getInt("passcode");
		current.desc = rs.getString("lore");
		current.attribute = rs.getString("attribute");
		current.race = rs.getString("race");
		current.linkval = rs.getString("linkValue");
		current.level = rs.getString("level");
		current.scale = rs.getString("pendScale");
		current.atk = rs.getString("atk");
		current.def = rs.getString("def");
		current.archetype = rs.getString("archetype");

		rs.close();
		statementgamePlayCard.close();

		return current;
		
	}

	public static void replaceIntoGamePlayCard(GamePlayCard input) throws SQLException {
		Connection connection = SQLiteConnection.getInstance();

		String gamePlayCard = "Replace into gamePlayCard(wikiID,title,type,passcode,lore,attribute,race,linkValue,level,pendScale,atk,def,archetype) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		PreparedStatement statementgamePlayCard = connection.prepareStatement(gamePlayCard);

		setIntegerOrNull(statementgamePlayCard, 1, input.wikiID);
		setStringOrNull(statementgamePlayCard, 2, input.cardName);
		setStringOrNull(statementgamePlayCard, 3, input.cardType);
		setIntegerOrNull(statementgamePlayCard, 4, input.passcode);
		setStringOrNull(statementgamePlayCard, 5, input.desc);
		setStringOrNull(statementgamePlayCard, 6, input.attribute);
		setStringOrNull(statementgamePlayCard, 7, input.race);
		setStringOrNull(statementgamePlayCard, 8, input.linkval);
		setStringOrNull(statementgamePlayCard, 9, input.level);
		setStringOrNull(statementgamePlayCard, 10, input.scale);
		setStringOrNull(statementgamePlayCard, 11, input.atk);
		setStringOrNull(statementgamePlayCard, 12, input.def);
		setStringOrNull(statementgamePlayCard, 13, input.archetype);

		statementgamePlayCard.execute();

		statementgamePlayCard.close();
	}

	private static PreparedStatement batchUpsertOwnedCard = null;

	private static int batchUpsertSize = 1000;

	private static int batchUpsertCurrentSize = 0;

	public static void UpdateOwnedCardByUUID(OwnedCard card)
			throws SQLException {
		
		int id = card.id;
		String folder = card.folderName;
		String name = card.cardName;
		int quantity = card.quantity;
		String setCode = card.setCode;
		String condition = card.condition;
		String printing = card.editionPrinting;
		String priceBought = card.priceBought;
		String dateBought = card.dateBought;
		int rarityUnsure = card.rarityUnsure;
		String colorVariant = card.colorVariant;
		String setNumber = card.setNumber;
		String setName = card.setName;
		String setRarity = card.setRarity;
		
		String low = card.priceLow;
		String mid = card.priceMid;
		String market = card.priceMarket;
		
		String UUID = card.UUID;

		Connection connection = SQLiteConnection.getInstance();

		if (rarityUnsure != 1) {
			rarityUnsure = 0;
		}

		if (colorVariant == null) {
			colorVariant = Util.defaultColorVariant;
		}

		String normalizedPrice = Util.normalizePrice(priceBought);

		String ownedInsert = "update ownedCards set wikiID = ?,folderName = ?,cardName = ?,quantity = ?,"
				+ "setCode = ?, setNumber = ?,setName = ?,setRarity = ?,setRarityColorVariant = ?,"
				+ "condition = ?,editionPrinting = ?,dateBought = ?,priceBought = ?,rarityUnsure = ?, "
				+ "modificationDate = datetime('now','localtime'), priceLow = ?, priceMid = ?, priceMarket = ? "
				+ "where UUID = ?";

		PreparedStatement statement = connection.prepareStatement(ownedInsert);

		statement.setInt(1, id);
		statement.setString(2, folder);
		statement.setString(3, name);
		statement.setInt(4, quantity);
		statement.setString(5, setCode);
		statement.setString(6, setNumber);
		statement.setString(7, setName);
		statement.setString(8, setRarity);
		statement.setString(9, colorVariant);
		statement.setString(10, condition);
		statement.setString(11, printing);
		statement.setString(12, dateBought);
		statement.setString(13, normalizedPrice);
		statement.setInt(14, rarityUnsure);
		
		statement.setString(15, low);
		statement.setString(16, mid);
		statement.setString(17, market);
		
		statement.setString(18, UUID);
		
		statement.execute();
		statement.close();

	}
	
	public static void upsertOwnedCardBatch(OwnedCard card)
			throws SQLException {
		
		int id = card.id;
		String folder = card.folderName;
		String name = card.cardName;
		int quantity = card.quantity;
		String setCode = card.setCode;
		String condition = card.condition;
		String printing = card.editionPrinting;
		String priceBought = card.priceBought;
		String dateBought = card.dateBought;
		int rarityUnsure = card.rarityUnsure;
		String colorVariant = card.colorVariant;
		String setNumber = card.setNumber;
		String setName = card.setName;
		String setRarity = card.setRarity;
		
		String low = card.priceLow;
		String mid = card.priceMid;
		String market = card.priceMarket;
		
		String UUID = card.UUID;

		Connection connection = SQLiteConnection.getInstance();

		if (rarityUnsure != 1) {
			rarityUnsure = 0;
		}

		if (colorVariant == null) {
			colorVariant = Util.defaultColorVariant;
		}

		String normalizedPrice = Util.normalizePrice(priceBought);

		String ownedInsert = "insert into ownedCards(wikiID,folderName,cardName,quantity,setCode,"
				+ "setNumber,setName,setRarity,setRarityColorVariant,condition,editionPrinting,dateBought"
				+ ",priceBought,rarityUnsure, creationDate, modificationDate, priceLow, priceMid, priceMarket, UUID) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ "datetime('now','localtime'),datetime('now','localtime'),?,?,?,?)"
				+ "on conflict (wikiID,folderName,setNumber," + "condition,editionPrinting,dateBought,priceBought) "
				+ "do update set quantity = ?, rarityUnsure = ?, setRarity = ?, setRarityColorVariant = ?, "
				+ "modificationDate = datetime('now','localtime'), priceLow = ?, priceMid = ?, priceMarket = ?,"
				+ "UUID = ?";

		if (batchUpsertOwnedCard == null) {

			batchUpsertOwnedCard = connection.prepareStatement(ownedInsert);
		}

		batchUpsertOwnedCard.setInt(1, id);
		batchUpsertOwnedCard.setString(2, folder);
		batchUpsertOwnedCard.setString(3, name);
		batchUpsertOwnedCard.setInt(4, quantity);
		batchUpsertOwnedCard.setString(5, setCode);
		batchUpsertOwnedCard.setString(6, setNumber);
		batchUpsertOwnedCard.setString(7, setName);
		batchUpsertOwnedCard.setString(8, setRarity);
		batchUpsertOwnedCard.setString(9, colorVariant);
		batchUpsertOwnedCard.setString(10, condition);
		batchUpsertOwnedCard.setString(11, printing);
		batchUpsertOwnedCard.setString(12, dateBought);
		batchUpsertOwnedCard.setString(13, normalizedPrice);
		batchUpsertOwnedCard.setInt(14, rarityUnsure);
		
		batchUpsertOwnedCard.setString(15, low);
		batchUpsertOwnedCard.setString(16, mid);
		batchUpsertOwnedCard.setString(17, market);
		
		batchUpsertOwnedCard.setString(18, UUID);
		
		//conflict fields
		
		batchUpsertOwnedCard.setInt(19, Integer.valueOf(quantity));
		batchUpsertOwnedCard.setInt(20, rarityUnsure);
		batchUpsertOwnedCard.setString(21, setRarity);
		batchUpsertOwnedCard.setString(22, colorVariant);
		
		batchUpsertOwnedCard.setString(23, low);
		batchUpsertOwnedCard.setString(24, mid);
		batchUpsertOwnedCard.setString(25, market);
		
		batchUpsertOwnedCard.setString(26, UUID);

		batchUpsertOwnedCard.addBatch();
		batchUpsertCurrentSize++;

		if (batchUpsertCurrentSize >= batchUpsertSize) {
			batchUpsertCurrentSize = 0;
			batchUpsertOwnedCard.executeBatch();
		}
	}

	public static void replaceIntoCardSet(String setNumber, String rarity, String setName, int wikiID, String price,
			String cardName) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setInsert = "replace into cardSets(wikiID,setNumber,setName,setRarity,setPrice, cardName) values(?,?,?,?,?,?)";

		PreparedStatement statementSetInsert = connection.prepareStatement(setInsert);

		statementSetInsert.setInt(1, wikiID);
		statementSetInsert.setString(2, setNumber);
		statementSetInsert.setString(3, setName);
		statementSetInsert.setString(4, rarity);
		statementSetInsert.setString(5, price);
		statementSetInsert.setString(6, cardName);

		statementSetInsert.execute();
		statementSetInsert.close();
	}
	
	public static void updateSetName(String original, String newName) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String setInsert = "update cardSets set setName = ? where setName = ?";

		PreparedStatement statementSetInsert = connection.prepareStatement(setInsert);

		statementSetInsert.setString(1, newName);
		statementSetInsert.setString(2, original);

		statementSetInsert.execute();
		statementSetInsert.close();
		
		setInsert = "update ownedCards set setName = ? where setName = ?";

		statementSetInsert = connection.prepareStatement(setInsert);

		statementSetInsert.setString(1, newName);
		statementSetInsert.setString(2, original);

		statementSetInsert.execute();
		statementSetInsert.close();
		
		setInsert = "update setData set setName = ? where setName = ?";

		statementSetInsert = connection.prepareStatement(setInsert);

		statementSetInsert.setString(1, newName);
		statementSetInsert.setString(2, original);

		statementSetInsert.execute();
		statementSetInsert.close();
	}
	
	public static int updateCardSetPrice(String setNumber, String rarity, String price) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String update = "update cardSets set setPrice = ?, setPriceUpdateTime = datetime('now','localtime')"
				+ " where setNumber = ? and setRarity = ?";

		PreparedStatement statement = connection.prepareStatement(update);

		statement.setString(1, price);
		statement.setString(2, setNumber);
		statement.setString(3, rarity);

		statement.execute();
		statement.close();
		
		String query = "select changes()";
		
		statement = connection.prepareStatement(query);
		
		ResultSet rs = statement.executeQuery();
		
		rs.next();
		int updated = rs.getInt(1);
		
		return updated;
		
	}
	
	public static int updateCardSetPrice(String setNumber, String price) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String update = "update cardSets set setPrice = ?, setPriceUpdateTime = datetime('now','localtime')"
				+ " where setNumber = ?";

		PreparedStatement statement = connection.prepareStatement(update);

		statement.setString(1, price);
		statement.setString(2, setNumber);

		statement.execute();
		statement.close();
		
		String query = "select changes()";
		
		statement = connection.prepareStatement(query);
		
		ResultSet rs = statement.executeQuery();
		
		rs.next();
		int updated = rs.getInt(1);
		
		return updated;
		
	}
	
	
}
