import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private Map<String, Card> cardMap;  // Full collection
    private Map<String, Integer> cardCounts;
    private Deck deck;

    public Inventory() {
        cardMap = new HashMap<>();
        cardCounts = new HashMap<>();
        deck = new Deck();
    }

    public void addCards(List<Card> newCards) {
        for (Card card : newCards) {
            String cardName = card.getName();
            cardCounts.put(cardName, cardCounts.getOrDefault(cardName, 0) + 1);

            // Ensure cardMap tracks all copies of the card, not just one
            cardMap.put(cardName, card);  // Always overwrite with the latest card reference
        }
    }

    public List<Card> getCards() {
        List<Card> cards = new ArrayList<>();
        // For each unique card in cardCounts, add it multiple times according to its count
        for (Map.Entry<String, Integer> entry : cardCounts.entrySet()) {
            Card card = cardMap.get(entry.getKey());  // Corrected: Get the Card from cardMap
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                cards.add(card);
            }
        }
        return cards;
    }

    public int getCardCount(String cardName) {
        return cardCounts.getOrDefault(cardName, 0);
    }

    public List<Card> findCardByName(String name) {
        List<Card> matchingCards = new ArrayList<>();
        if (cardCounts.containsKey(name)) {
            int count = cardCounts.get(name);
            Card card = cardMap.get(name);
            for (int i = 0; i < count; i++) {
                matchingCards.add(card);
            }
        }
        return matchingCards;
    }

    public Deck getDeck() {
        return deck;
    }

    public boolean addCardToDeck(Card card) {
        if (deck.addCard(card)) {
            String cardName = card.getName();
            int count = cardCounts.getOrDefault(cardName, 0);

            if (count > 0) {
                cardCounts.put(cardName, count - 1);
                // Remove card from cardMap if count reaches 0
                if (cardCounts.get(cardName) == 0) {
                    cardMap.remove(cardName);
                }
            }
            return true;
        }
        return false;
    }

    public boolean removeCardFromDeck(Card card) {
        if (deck.removeCard(card.getId())) {
            String cardName = card.getName();

            // Add the card back to the inventory
            cardCounts.put(cardName, cardCounts.getOrDefault(cardName, 0) + 1);
            cardMap.putIfAbsent(cardName, card);

            return true;
        }
        return false;
    }
}
