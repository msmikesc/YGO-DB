package analyze;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import bean.OwnedCard;
import connection.CsvConnection;
import connection.DatabaseHashMap;
import connection.SQLiteConnection;
import connection.Util;

public class AnalyzeCompareToDragonShieldCSV {
	
	private static final List<String> setIgnoreColorVariantConfict = Arrays.asList("Legendary Duelists: Season 2");

	public static void main(String[] args) throws SQLException, IOException {
		AnalyzeCompareToDragonShieldCSV mainObj = new AnalyzeCompareToDragonShieldCSV();
		mainObj.run();
		SQLiteConnection.closeInstance();
		System.out.println("Analyze Complete");
	}

	public void run() throws SQLException, IOException {

		Iterator<CSVRecord> it = CsvConnection.getIteratorSkipFirstLine(
				"C:\\Users\\Mike\\Documents\\GitHub\\YGO-DB\\YGO-DB\\csv\\all-folders.csv", StandardCharsets.UTF_16LE);
		
		HashMap<String, ArrayList<OwnedCard>> databaseList = DatabaseHashMap.getOwnedInstance();

		while (it.hasNext()) {

			CSVRecord current = it.next();

			String folder = current.get("Folder Name").trim();
			String name = current.get("Card Name").trim();
			String quantity = current.get("Quantity").trim();
			String setCode = current.get("Set Code").trim();
			String setNumber = current.get("Card Number").trim();
			String setName = current.get("Set Name").trim();
			String condition = current.get("Condition").trim();
			String printing = current.get("Printing").trim();
			String priceBought = Util.normalizePrice(current.get("Price Bought"));
			String dateBought = current.get("Date Bought").trim();
			
			String colorCode = Util.defaultColorVariant;

			if (printing.equals("Foil")) {
				printing = "1st Edition";
			}
			
			String key = setNumber + Util.normalizePrice(priceBought) + dateBought + folder + condition
					+ printing;

			ArrayList<OwnedCard> list = databaseList.get(key);
			
			if(list == null) {
				//try removing color code
				colorCode = setNumber.substring( setNumber.length() - 1, setNumber.length());
				setNumber = setNumber.substring(0, setNumber.length() - 1);
				
				key = setNumber + Util.normalizePrice(priceBought) + dateBought + folder + condition
						+ printing;

				list = databaseList.get(key);
			}
			
			if(list == null) {
				System.out.println("no match in DB found forkey : " + key);
			}
			else if(list.size() == 1) {
				//exact 1 match
				
				OwnedCard card = list.get(0);
				
				if(!colorCode.equalsIgnoreCase(list.get(0).colorVariant) && !setIgnoreColorVariantConfict.contains(card.setName)) {
					System.out.println("Color Code Mismatch on: " + card.cardName + " " +  card.setNumber + " " +  card.setRarity + " " + card.colorVariant + " " + card.priceBought + " " +  card.dateBought);
				}
				
				databaseList.remove(key);
			}
			else {
				
				boolean foundMatch = false;

				for (int i = 0; i < list.size(); i++) {

					OwnedCard existingCard = list.get(i);

					if (Util.doesCardExactlyMatch(folder, name, setCode, setNumber, condition,
							printing, priceBought, dateBought, colorCode, existingCard)) {
						foundMatch = true;
						list.remove(i);
						
						if(list.size() == 0) {
							databaseList.remove(key);
						}
						break;

					}
				}

				if (foundMatch == false) {
					System.out.println("Unable to find exact match for key: " + key);
				}

			}

		}
		
		for(ArrayList<OwnedCard> rarityList: databaseList.values()) {
			for(OwnedCard card: rarityList) {
				System.out.println("Card in DB but not in CSV: " + card.cardName + " " +  card.setNumber + " " +  card.setRarity + " " + card.colorVariant + " " + card.priceBought + " " +  card.dateBought);
			}
		}
		
		
	}

}
