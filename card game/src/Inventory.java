import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    // Tracks all copies of a card by name.
    private Map<String, List<Card>> cardMap;
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
            cardMap.putIfAbsent(cardName, new ArrayList<>());
            cardMap.get(cardName).add(card);
        }
    }

    public List<Card> getCards() {
        List<Card> cards = new ArrayList<>();
        for (List<Card> cardList : cardMap.values()) {
            cards.addAll(cardList);
        }
        return cards;
    }

    // Returns the internal grouping (used for display, if needed)
    public Map<String, List<Card>> getGroupedInventory() {
        return cardMap;
    }

    public int getCardCount(String cardName) {
        return cardCounts.getOrDefault(cardName, 0);
    }

    public List<Card> findCardByName(String name) {
        if (cardMap.containsKey(name)) {
            return new ArrayList<>(cardMap.get(name));
        }
        return new ArrayList<>();
    }

    public Deck getDeck() {
        return deck;
    }

    // When adding a card to the deck, remove that instance from the inventory.
    public boolean addCardToDeck(Card card) {
        String cardName = card.getName();
        List<Card> cards = cardMap.get(cardName);
        if (cards == null || !cards.contains(card)) {
            return false;
        }
        if (deck.addCard(card)) {
            cards.remove(card);
            int count = cardCounts.get(cardName);
            cardCounts.put(cardName, count - 1);
            if (cardCounts.get(cardName) == 0) {
                cardMap.remove(cardName);
            }
            return true;
        }
        return false;
    }

    // When removing a card from the deck, add that instance back.
    public boolean removeCardFromDeck(Card card) {
        if (deck.removeCard(card.getId())) {
            String cardName = card.getName();
            cardCounts.put(cardName, cardCounts.getOrDefault(cardName, 0) + 1);
            cardMap.putIfAbsent(cardName, new ArrayList<>());
            cardMap.get(cardName).add(card);
            card.setInCurDeck(false);
            return true;
        }
        return false;
    }

    // Utility method to remove one copy of a card (used for upgrades)
    public boolean removeOneCard(Card card) {
        String cardName = card.getName();
        List<Card> list = cardMap.get(cardName);
        if (list != null && !list.isEmpty()) {
            list.remove(0);
            int count = cardCounts.get(cardName);
            cardCounts.put(cardName, count - 1);
            if (cardCounts.get(cardName) <= 0) {
                cardMap.remove(cardName);
            }
            return true;
        }
        return false;
    }
}
