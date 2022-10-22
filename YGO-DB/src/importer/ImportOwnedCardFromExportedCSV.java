package importer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.csv.CSVRecord;
import connection.CsvConnection;
import connection.SQLiteConnection;

public class ImportOwnedCardFromExportedCSV {

	public static void main(String[] args) throws SQLException, IOException {
		ImportOwnedCardFromExportedCSV mainObj = new ImportOwnedCardFromExportedCSV();
		mainObj.run();
		SQLiteConnection.closeInstance();
	}

	public void run() throws SQLException, IOException {

		Iterator<CSVRecord> it = CsvConnection.getIterator(
				"C:\\Users\\Mike\\Documents\\GitHub\\YGO-DB\\YGO-DB\\csv\\rarity-unsure-export.csv", StandardCharsets.UTF_16LE);

		while (it.hasNext()) {

			CSVRecord current = it.next();

			CsvConnection.insertOwnedCardFromExportedCSV(current);

		}

	}

}
