package connection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class CheckDBforIssues {

	public static void main(String[] args) throws SQLException, IOException {
		CheckDBforIssues mainObj = new CheckDBforIssues();
		mainObj.run();
		SQLiteConnection.closeInstance();
	}

	public void run() throws SQLException, IOException {

		ArrayList<String> setsList = SQLiteConnection.getDistinctSetNames();

		for (String setName : setsList) {

			if (setName.contains("Tip Card") || setName.contains("(POR)")) {
				continue;
			}

			Util.checkForIssuesWithSet(setName);

		}
		Util.checkSetCounts();

	}

}
