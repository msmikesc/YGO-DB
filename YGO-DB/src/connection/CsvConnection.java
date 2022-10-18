package connection;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
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

		CardSet setIdentified = Util.findRarity(cardNumber, priceBought, dateBought, folder, condition, printing,
				setNumber, setName, name);

		SQLiteConnection.upsertOwnedCard(folder, name, quantity, setCode, condition, printing, priceBought, dateBought,
				setIdentified);
	}

	public static void insertCardSetFromCSV(CSVRecord current, String defaultSetName) throws SQLException {

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

		SQLiteConnection.replaceIntoCardSet(cardNumber, rarity, setName, wikiID, price);
	}

}
