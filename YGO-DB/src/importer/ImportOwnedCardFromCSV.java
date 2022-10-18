package importer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.csv.CSVRecord;
import connection.CsvConnection;

public class ImportOwnedCardFromCSV {

	public static void main(String[] args) throws SQLException, IOException {
		ImportOwnedCardFromCSV mainObj = new ImportOwnedCardFromCSV();
		mainObj.run();
	}

	public void run() throws SQLException, IOException {

		Iterator<CSVRecord> it = CsvConnection.getIterator("C:\\Users\\Mike\\Documents\\GitHub\\YGO-DB\\YGO-DB\\csv\\all-folders.csv",
				StandardCharsets.UTF_16LE);

		while (it.hasNext()) {

			CSVRecord current = it.next();

			CsvConnection.insertOwnedCardFromCSV(current);

		}

	}

	

}
