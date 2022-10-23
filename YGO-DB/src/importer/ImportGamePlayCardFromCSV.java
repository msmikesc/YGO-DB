package importer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.csv.CSVRecord;
import org.json.*;

import connection.CsvConnection;
import connection.SQLiteConnection;
import connection.Util;

import java.sql.SQLException;

public class ImportGamePlayCardFromCSV {

	public static void main(String[] args) throws SQLException, IOException {
		ImportGamePlayCardFromCSV mainObj = new ImportGamePlayCardFromCSV();
		mainObj.run();
		SQLiteConnection.closeInstance();
	}

	public void run() throws SQLException, IOException {
		
		
		String csvFileName = "gamePlayCards";

		String fileNameString = "C:\\Users\\Mike\\Documents\\GitHub\\YGO-DB\\YGO-DB\\csv\\" + csvFileName + ".csv";

		Iterator<CSVRecord> it = CsvConnection.getIterator(fileNameString, StandardCharsets.UTF_8);

		while (it.hasNext()) {

			CSVRecord current = it.next();

			CsvConnection.insertGamePlayCardFromCSV(current, csvFileName);
		}

	}
}
