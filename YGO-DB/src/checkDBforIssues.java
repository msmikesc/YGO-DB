import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import connection.SQLiteConnection;
import connection.Util;

public class checkDBforIssues {

	public static void main(String[] args) throws SQLException, IOException {
		checkDBforIssues mainObj = new checkDBforIssues();
		mainObj.run();
		Util.checkSetCounts();
	}

	public void run() throws SQLException, IOException {

		ArrayList<String> setsList = SQLiteConnection.getDistinctSetNames();

		for (String setName : setsList) {

			if (setName.contains("Tip Card") || setName.contains("(POR)")) {
				continue;
			}

			Util.checkForIssuesWithSet(setName);

		}

	}
	


	

}
