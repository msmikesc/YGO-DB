package connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Iterator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import bean.CardSet;
import bean.OwnedCard;

public class CsvConnection {

	public static Iterator<CSVRecord> getIterator(String Filename, Charset charset) throws IOException {
		File f = new File(Filename);

		BufferedReader fr = new BufferedReader(new FileReader(f, charset));
		
		skipByteOrderMark(fr);

		CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(fr);

		Iterator<CSVRecord> it = parser.iterator();

		return it;
	}
	
	private static void skipByteOrderMark(Reader reader) throws IOException
    {
        reader.mark(1);
        char[] possibleBOM = new char[1];
        reader.read(possibleBOM);

        if (possibleBOM[0] != '\ufeff')
        {
            reader.reset();
        }
    }
	
	public static Iterator<CSVRecord> getIteratorSkipFirstLine(String Filename, Charset charset) throws IOException {
		File f = new File(Filename);

		FileReader fr = new FileReader(f, charset);
		
		BufferedReader s = new BufferedReader(fr);
		
		s.readLine();
		
		CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(s);

		Iterator<CSVRecord> it = parser.iterator();

		return it;
	}

	public static CSVPrinter getExportOutputFile(String filename) {
		
		try {
			Writer fw = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_16LE);
			CSVPrinter p  = new CSVPrinter(fw, CSVFormat.DEFAULT);
			
			p.printRecord("Folder Name","Quantity","Card Name","Set Code","Set Name","Card Number","Condition","Printing","Price Bought","Date Bought","Rarity","Rarity Color Variant", "Rarity Unsure");
			
			return p;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
 	public static void insertOwnedCardFromCSV(CSVRecord current) throws SQLException {

		String folder = current.get("Folder Name").trim();
		String name = current.get("Card Name").trim();
		String quantity = current.get("Quantity").trim();
		String setCode = current.get("Set Code").trim();
		String setNumber = current.get("Card Number").trim();
		String setName = current.get("Set Name").trim();
		String condition = current.get("Condition").trim();
		String printing = current.get("Printing").trim();
		String priceBought = current.get("Price Bought").trim();
		String dateBought = current.get("Date Bought").trim();

		if (printing.equals("Foil")) {
			printing = "1st Edition";
		}

		CardSet setIdentified = Util.findRarity(priceBought, dateBought, folder, condition, printing,
				setNumber, setName, name);

		SQLiteConnection.upsertOwnedCard(folder, name, quantity, setCode, condition, printing, priceBought, dateBought,
				setIdentified);
	}
 	
 	public static void insertOwnedCardFromExportedCSV(CSVRecord current) throws SQLException {

		String folder = current.get("Folder Name").trim();
		String name = current.get("Card Name").trim();
		String quantity = current.get("Quantity").trim();
		String setCode = current.get("Set Code").trim();
		String setNumber = current.get("Card Number").trim();
		String setName = current.get("Set Name").trim();
		String condition = current.get("Condition").trim();
		String printing = current.get("Printing").trim();
		String priceBought = current.get("Price Bought").trim();
		String dateBought = current.get("Date Bought").trim();
		String rarity = current.get("Rarity").trim();
		String rarityColorVariant = current.get("Rarity Color Variant").trim();
		String rarityUnsure = current.get("Rarity Unsure").trim();

		if (printing.equals("Foil")) {
			printing = "1st Edition";
		}
		
		int wikiID = SQLiteConnection.getCardIdFromTitle(name);

		CardSet setIdentified = new CardSet();
		setIdentified.rarityUnsure = new Integer(rarityUnsure);
		setIdentified.colorVariant = rarityColorVariant;
		setIdentified.setRarity = rarity;
		setIdentified.setName = setName;
		setIdentified.setNumber = setNumber;
		setIdentified.id = wikiID;

		SQLiteConnection.upsertOwnedCard(folder, name, quantity, setCode, condition, printing, priceBought, dateBought,
				setIdentified);
	}
 	
 	public static Integer getIntOrNull(CSVRecord current, String recordName) {
 		try {
			Integer returnVal = Integer.parseInt(current.get(recordName));
			return returnVal;
		}
		catch(Exception e) {
			return null;
		}
 	}
 	
 	public static Integer getIntOrNegativeOne(CSVRecord current, String recordName) {
 		try {
			Integer returnVal = Integer.parseInt(current.get(recordName));
			return returnVal;
		}
		catch(Exception e) {
			return new Integer(-1);
		}
 	}
 	
 	public static String getStringOrNull(CSVRecord current, String recordName) {
 		try {
			String returnVal = current.get(recordName);
			
			if(returnVal == null || returnVal.isBlank()) {
				return null;
			}
			
			return returnVal.trim();
		}
		catch(Exception e) {
			return null;
		}
 	}
 	
 	public static void insertGamePlayCardFromCSV(CSVRecord current, String defaultSetName) throws SQLException {

		String name = getStringOrNull(current,"Card Name");
		String type = getStringOrNull(current,"Card Type");
		Integer passcode = getIntOrNegativeOne(current,"Passcode");
		String lore = getStringOrNull(current,"Card Text");
		String attribute = getStringOrNull(current,"Attribute");
		String race = getStringOrNull(current,"Race");
		Integer linkValue = getIntOrNull(current,"Link Value");
		Integer pendScale = getIntOrNull(current,"Pendulum Scale");
		Integer level = getIntOrNull(current,"Level/Rank");
		Integer atk = getIntOrNull(current,"Attack");
		Integer def = getIntOrNull(current,"Defense");
		String archetype = getStringOrNull(current,"Archetype");

		SQLiteConnection.replaceIntoGamePlayCard(passcode, name, type, passcode, lore, attribute, race, linkValue, pendScale, level, atk, def, archetype);
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

	
	public static void writeOwnedCardToCSV(CSVPrinter p, OwnedCard current) throws IOException {
		//p.printRecord("Folder Name","Quantity","Card Name","Set Code","Set Name","Card Number","Condition","Printing","Price Bought","Date Bought","Rarity","Rarity Color Variant", "Rarity Unsure");
		p.printRecord(current.folderName, current.quantity, current.cardName, current.setCode, current.setName, current.setNumber, current.condition, current.editionPrinting, current.priceBought, current.dateBought, current.setRarity, current.colorVariant, current.rarityUnsure);
		
	}

}
