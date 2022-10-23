package export;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.commons.csv.CSVPrinter;
import bean.OwnedCard;
import connection.CsvConnection;
import connection.SQLiteConnection;

public class ExportAllOwnedCardToCSV {

	public static void main(String[] args) throws SQLException, IOException {
		ExportAllOwnedCardToCSV mainObj = new ExportAllOwnedCardToCSV();
		mainObj.run();
		SQLiteConnection.closeInstance();
	}

	public void run() throws SQLException, IOException {
		
		String filename = "C:\\Users\\Mike\\Documents\\GitHub\\YGO-DB\\YGO-DB\\csv\\all-export.csv";
		
		ArrayList<OwnedCard> list = SQLiteConnection.getAllOwnedCards();
		
		CSVPrinter p = CsvConnection.getExportOutputFile(filename);

		for(OwnedCard current : list) {

			CsvConnection.writeOwnedCardToCSV(p,current);

		}
		
		p.flush();
		p.close();
		
	}

}
