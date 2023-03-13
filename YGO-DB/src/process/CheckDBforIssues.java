package process;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import connection.SQLiteConnection;
import connection.Util;

public class CheckDBforIssues {

	public static void main(String[] args) throws SQLException, IOException {
		CheckDBforIssues mainObj = new CheckDBforIssues();
		mainObj.run();
		SQLiteConnection.closeInstance();
		System.out.println("Analyze complete");
	}

	public void run() throws SQLException, IOException {

		ArrayList<String> setsList = SQLiteConnection.getDistinctSetNames();

		for (String setName : setsList) {

			if (setName.contains("Tip Card") || setName.contains("(POR)")) {
				continue;
			}

			Util.checkForIssuesWithSet(setName);
			Util.checkForIssuesWithCardNamesInSet(setName);

		}
		Util.checkSetCounts();

	}

}
