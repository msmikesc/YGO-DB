package connection;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import bean.CardSet;

public class CsvConnection {

	public static Iterator<CSVRecord> getIterator(String Filename, Charset charset) throws IOException {
		File f = new File(Filename);

		FileReader fr = new FileReader(f, charset);

		CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(fr);

		Iterator<CSVRecord> it = parser.iterator();

		return it;
	}
	
	public static void insertOwnedCardFromCSV(CSVRecord current) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String folder = current.get("Folder Name").trim();
		String name = current.get("Card Name").trim();
		String quantity = current.get("Quantity").trim();
		String setCode = current.get("Set Code").trim();
		String setNumber = current.get("Card Number").trim();
		String setName = current.get("Set Name").trim();
		String cardNumber = current.get("Card Number").trim();
		String condition = current.get("Condition").trim();
		String printing = current.get("Printing").trim();
		String priceBought = current.get("Price Bought").trim();
		String dateBought = current.get("Date Bought").trim();

		if (printing.equals("Foil")) {
			printing = "1st Edition";
		}

		CardSet setIdentified = Util.findRarity(cardNumber, priceBought, dateBought, folder, condition, printing, setNumber, setName, name);

		if (setIdentified == null) {
			

		}

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

	public static void insertCardSetFromCSV(CSVRecord current, String defaultSetName) throws SQLException {

		Connection connection = SQLiteConnection.getInstance();

		String name = current.get("Name").trim();
		String cardNumber = current.get("Card number").trim();
		String rarity = current.get("Rarity").trim();

		String setName = null;

		try {
			setName = current.get("Set Name").trim();
		} catch (Exception e) {
			setName = defaultSetName;
		}

		int wikiID = SQLiteConnection.getCardIdFromTitle(name);

		// try skill card
		if (wikiID == -1) {
			wikiID = SQLiteConnection.getCardIdFromTitle(name + " (Skill Card)");
		}

		if (wikiID == -1) {
			System.out.println("Unable to find match for " + cardNumber + ":" + name);
		}

		String price = Util.getAdjustedPriceFromRarity(rarity, "0");

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
