package analyze;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import bean.CardSet;
import bean.OwnedCard;
import bean.Rarity;
import bean.SetMetaData;
import connection.SQLiteConnection;

public class AnalyzeCardsInSet {

	public static void main(String[] args) throws SQLException, IOException {
		AnalyzeCardsInSet mainObj = new AnalyzeCardsInSet();
		mainObj.run();
		SQLiteConnection.closeInstance();
	}

	public void run() throws SQLException, IOException {
		
		String setName = "KICO";
		
		ArrayList<Integer> list = SQLiteConnection.getDistinctCardIDsInSetByName(setName);
		
		if(list.size() == 0) {
			ArrayList<SetMetaData> setNames = SQLiteConnection.getSetMetaDataFromSetCode(setName);
			setName = setNames.get(0).set_name;
			list = SQLiteConnection.getDistinctCardIDsInSetByName(setName);
		}
		
		
		Dictionary<Integer, ArrayList<String>> d = new Hashtable<Integer, ArrayList<String>>();
		
		for (int i :list) {
			ArrayList<OwnedCard> cardsList = SQLiteConnection.getNumberOfOwnedCardsById(i);
			
			ArrayList<CardSet> rarityList = SQLiteConnection.getAllRaritiesOfCardByID(i);
			
			String niceList = getStringOfRarities(rarityList);
			
			if(cardsList.size()==0) {
				
				String title = SQLiteConnection.getCardTitleFromID(i);
				
				if(title == null) {
				
					addToDictionaryList(d,-1,"No cards found for id:" + i);
				}
				else {
					addToDictionaryList(d, 0, title+niceList);
				}
			}
			
			for(OwnedCard current: cardsList) {
				addToDictionaryList(d, current.quantity, current.cardName+niceList);
			}
		}
		
		Enumeration<Integer> quantityEnum = d.keys();
		
		ArrayList<Integer> quantityList = Collections.list(quantityEnum);
		Collections.sort(quantityList);
		
		boolean printedSeparator = false;
		
		for(Integer i: quantityList) {
			ArrayList<String> currentList = d.get(i);
			Collections.sort(currentList);
			
			if(!printedSeparator &&i >= 3) {
				printedSeparator = true;
				System.out.println("");
				System.out.println("----");
				System.out.println("");
			}
			
			for(String s:currentList) {
				System.out.println(i+":"+s);
			}
			
		}
		
	}
	
	private String getStringOfRarities(ArrayList<CardSet> list) {
		HashSet<Rarity> enumList = new HashSet<Rarity>();
		
		for(CardSet s: list) {
			Rarity rarityValue = Rarity.fromString(s.setRarity);
			enumList.add(rarityValue);
		}
		
		ArrayList<Rarity> enumList2 = new ArrayList<Rarity>(enumList);
		
		Collections.sort(enumList2);
		
		String output = " (" + enumList2.get(0).toString();
		
		for(int i = 1; i < enumList.size(); i++) {
			output += ", " + enumList2.get(i).toString();
		}
		
		output += ")";
		
		return output;
		
		
	}
	
	private void addToDictionaryList(Dictionary<Integer, ArrayList<String>> d, int i, String s) {
		
		if(d.get(i) == null) {
			d.put(i, new ArrayList<String>());
		}
		
		d.get(i).add(s);
		
	}
}
