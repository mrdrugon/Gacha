import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    // Tracks all copies of a card by name.
    private Map<String, List<ICard>> cardMap;
    private Map<String, Integer> cardCounts;
    private Deck deck;

    public Inventory() {
        cardMap = new HashMap<>();
        cardCounts = new HashMap<>();
        deck = new Deck();
    }

    public void addCards(List<ICard> newCards) {
        for (ICard card : newCards) {
            String cardName = card.getName();
            cardCounts.put(cardName, cardCounts.getOrDefault(cardName, 0) + 1);
            cardMap.putIfAbsent(cardName, new ArrayList<>());
            cardMap.get(cardName).add(card);
        }
    }

    public List<ICard> getCards() {
        List<ICard> cards = new ArrayList<>();
        for (List<ICard> cardList : cardMap.values()) {
            cards.addAll(cardList);
        }
        return cards;
    }

    public Map<String, List<ICard>> getGroupedInventory() {
        return cardMap;
    }

    public int getCardCount(String cardName) {
        return cardCounts.getOrDefault(cardName, 0);
    }

    public List<ICard> findCardByName(String name) {
        if (cardMap.containsKey(name)) {
            return new ArrayList<>(cardMap.get(name));
        }
        return new ArrayList<>();
    }

    public Deck getDeck() {
        return deck;
    }

    // When adding a card to the deck, remove that instance from the inventory.
    public boolean addCardToDeck(ICard card) {
        String cardName = card.getName();
        List<ICard> cards = cardMap.get(cardName);
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
    public boolean removeCardFromDeck(ICard card) {
        if (deck.removeCard(card.getId())) {
            String cardName = card.getName();
            cardCounts.put(cardName, cardCounts.getOrDefault(cardName, 0) + 1);
            cardMap.putIfAbsent(cardName, new ArrayList<>());
            cardMap.get(cardName).add(card);
            return true;
        }
        return false;
    }

    // Utility method to remove one copy of a card (used for upgrades)
    public boolean removeOneCard(ICard card) {
        String cardName = card.getName();
        List<ICard> list = cardMap.get(cardName);
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
