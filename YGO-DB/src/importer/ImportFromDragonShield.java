package importer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.csv.CSVRecord;

import bean.OwnedCard;
import connection.CsvConnection;
import connection.DatabaseHashMap;
import connection.SQLiteConnection;
import connection.Util;

public class ImportFromDragonShield {

	public static void main(String[] args) throws SQLException, IOException {
		ImportFromDragonShield mainObj = new ImportFromDragonShield();
		mainObj.run();
		SQLiteConnection.closeInstance();
		System.out.println("Import Complete");
	}

	public void run() throws SQLException, IOException {

		boolean importPriceChange = true;
		importPriceChange = false;

		Iterator<CSVRecord> it = CsvConnection.getIteratorSkipFirstLine(
				"C:\\Users\\Mike\\Documents\\GitHub\\YGO-DB\\YGO-DB\\csv\\all-folders.csv", StandardCharsets.UTF_16LE);

		int count = 0;

		while (it.hasNext()) {

			CSVRecord current = it.next();

			OwnedCard card = CsvConnection.getOwnedCardFromDragonShieldCSV(current);

			ArrayList<OwnedCard> ownedRarities = DatabaseHashMap.getExistingOwnedRaritesForCardFromHashMap(
					card.setNumber, card.priceBought, card.dateBought, card.folderName, card.condition,
					card.editionPrinting);

			for (OwnedCard existingCard : ownedRarities) {
				if (Util.doesCardExactlyMatch(card.folderName, card.cardName, card.setCode, card.setNumber,
						card.condition, card.editionPrinting, card.priceBought, card.dateBought, existingCard)) {
					if (card.quantity == existingCard.quantity) {

						if (importPriceChange) {
							// import anyway if price needs to be updated
							card.UUID = existingCard.UUID;
							break;
						}
						// no changes, no need to update
						card = null;
						break;
					} else {
						// something to update
						card.UUID = existingCard.UUID;
						break;
					}
				}
			}

			if (card != null) {
				count += card.quantity;
				SQLiteConnection.upsertOwnedCardBatch(card);
			}
		}

		SQLiteConnection.closeInstance();

		System.out.println("Imported " + count + " cards");
		System.out.println("Total cards: " + SQLiteConnection.getCountQuantity() + " + "
				+ SQLiteConnection.getCountQuantityManual() + " Manual");

	}

}
