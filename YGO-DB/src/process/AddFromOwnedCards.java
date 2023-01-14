package process;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import bean.CardSet;
import bean.GamePlayCard;
import bean.OwnedCard;
import connection.SQLiteConnection;

public class AddFromOwnedCards {

	public static void main(String[] args) throws SQLException, IOException {
		AddFromOwnedCards mainObj = new AddFromOwnedCards();
		mainObj.run();
		SQLiteConnection.closeInstance();
		System.out.println("Process Complete");
	}

	private void run() throws SQLException {
		ArrayList<OwnedCard> cards = SQLiteConnection.getAllOwnedCards();

		for (OwnedCard card : cards) {

			card.cardName = card.cardName.trim();
			card.setName = card.setName.trim();

			GamePlayCard gamePlayCard = SQLiteConnection.getGamePlayCardByNameAndID(card.id, card.cardName);

			if (gamePlayCard == null) {
				// check for skill card
				String newCardName = card.cardName + " (Skill Card)";

				gamePlayCard = SQLiteConnection.getGamePlayCardByNameAndID(card.id, newCardName);

				if (gamePlayCard != null) {
					card.cardName = newCardName;
				} else {
					// add it
					System.out.println("No gamePlayCard found for " + card.cardName + ":" + card.id);

					SQLiteConnection.replaceIntoGamePlayCard(card.id, card.cardName, "unknown", card.id, null, null,
							null, null, null, null, null, null, null);

				}
			}

			ArrayList<CardSet> sets = SQLiteConnection.getRaritiesOfCardInSetByIDAndName(card.id, card.setName,
					card.cardName);

			if (sets.size() == 0) {
				// add it
				System.out.println("No rarity entries found for " + card.cardName + ":" + card.id + ":" + card.setName);
				SQLiteConnection.replaceIntoCardSet(card.setNumber, card.setRarity, card.setName, card.id, "0",
						card.cardName);
			} else {
				boolean match = false;

				for (CardSet set : sets) {
					if (set.setRarity.equalsIgnoreCase(card.setRarity)
							&& set.setNumber.equalsIgnoreCase(card.setNumber)) {
						match = true;
						break;
					}
				}

				if (!match) {
					// add it
					System.out.println("No matching rarity entries found for " + card.cardName + ":" + card.id + ":"
							+ card.setName);
					SQLiteConnection.replaceIntoCardSet(card.setNumber, card.setRarity, card.setName, card.id, "0",
							card.cardName);
				}
			}

		}

	}

}
