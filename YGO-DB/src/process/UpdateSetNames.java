package process;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import bean.SetMetaData;
import connection.SQLiteConnection;
import connection.Util;

public class UpdateSetNames {

	public static void main(String[] args) throws SQLException, IOException {
		UpdateSetNames mainObj = new UpdateSetNames();
		mainObj.run();
		SQLiteConnection.closeInstance();
	}

	public void run() throws SQLException, IOException {

		ArrayList<String> setsList = SQLiteConnection.getDistinctSetNames();
		
		ArrayList<SetMetaData> metaData = SQLiteConnection.getAllSetMetaDataFromSetData();
		
		for(SetMetaData meta: metaData) {
			if(!setsList.contains(meta.set_name)) {
				setsList.add(meta.set_name);
			}
		}

		for (String setName : setsList) {

			if (setName.contains("Tip Card") || setName.contains("(POR)")) {
				continue;
			}
			
			String newSetName = Util.checkForTranslatedSetName(setName);
			
			if(!newSetName.equals(setName)) {
				SQLiteConnection.updateSetName(setName, newSetName);
			}

			

		}
		

	}

}
