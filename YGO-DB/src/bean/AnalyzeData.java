package bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AnalyzeData implements Comparable<AnalyzeData> {

	public int quantity;
	public String cardName;
	public Set<String> setNumber;
	public Set<String> setName;
	public Set<String> setRarities;
	public String cardType;

	public AnalyzeData() {
		setNumber = new HashSet<String>();
		setName = new HashSet<String>();
		setRarities = new HashSet<String>();
	}

	@Override
	public int compareTo(AnalyzeData o) {

		int compare = 0;
		
		compare = Integer.valueOf(this.quantity).compareTo(Integer.valueOf(o.quantity));

		if (compare != 0) {
			return compare;
		}

		compare = this.getStringOfRarities().compareTo(o.getStringOfRarities());

		if (compare != 0) {
			return compare;
		}

		compare = this.getStringOfSetNames().compareTo(o.getStringOfSetNames());

		if (compare != 0) {
			return compare;
		}

		return cardName.compareTo(o.cardName);
	}

	public String getStringOfSetNames() {

		ArrayList<String> results = new ArrayList<String>(setName);

		Collections.sort(results);

		String output = "(" + results.get(0);

		for (int i = 1; i < results.size(); i++) {
			output += ", " + results.get(i);
		}

		output += ")";

		return output;
	}

	public String getStringOfSetNumbers() {
		ArrayList<String> results = new ArrayList<String>(setNumber);

		Collections.sort(results);

		String output = "(" + results.get(0);

		for (int i = 1; i < results.size(); i++) {
			output += ", " + results.get(i);
		}

		output += ")";

		return output;
	}

	public String getStringOfRarities() {
		HashSet<Rarity> enumList = new HashSet<Rarity>();

		for (String s : setRarities) {
			Rarity rarityValue = Rarity.fromString(s);
			enumList.add(rarityValue);
		}

		ArrayList<Rarity> enumList2 = new ArrayList<Rarity>(enumList);

		Collections.sort(enumList2);

		String output = "(" + enumList2.get(0).toString();

		for (int i = 1; i < enumList.size(); i++) {
			output += ", " + enumList2.get(i).toString();
		}

		output += ")";

		return output;

	}

}
