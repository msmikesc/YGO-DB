package export;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.commons.csv.CSVPrinter;
import bean.OwnedCard;
import connection.CsvConnection;
import connection.SQLiteConnection;

public class ExportOwnedCardToCSV {

	public static void main(String[] args) throws SQLException, IOException {
		ExportOwnedCardToCSV mainObj = new ExportOwnedCardToCSV();
		mainObj.run();
		SQLiteConnection.closeInstance();
	}

	public void run() throws SQLException, IOException {
		
		String filename = "C:\\Users\\Mike\\Documents\\GitHub\\YGO-DB\\YGO-DB\\csv\\rarity-unsure-export.csv";
		
		ArrayList<OwnedCard> list = SQLiteConnection.getRarityUnsureOwnedCards();
		
		CSVPrinter p = CsvConnection.getRarityUnsureOutputFile(filename);

		for(OwnedCard current : list) {

			CsvConnection.writeOwnedCardToCSV(p,current);

		}
		
		p.flush();
		p.close();
		
	}

}
