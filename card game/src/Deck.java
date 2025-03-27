import java.util.ArrayList;
import java.util.List;

public class Deck {
    private ArrayList<ICard> deck;
    public static final int DECK_SIZE = 5;

    public Deck() {
        this.deck = new ArrayList<>();
    }

    public List<ICard> getDeck() {
        return new ArrayList<>(deck);
    }

    public boolean addCard(ICard card) {
        if (deck.size() < DECK_SIZE) {
            // Check if card already exists in deck based on its ID.
            for (ICard deckCard : deck) {
                if (deckCard.getId() == card.getId()) {
                    return false;
                }
            }
            deck.add(card);
            return true;
        }
        return false;
    }

    public boolean removeCard(int cardId) {
        return deck.removeIf(card -> card.getId() == cardId);
    }

    public boolean isFull() {
        return deck.size() == DECK_SIZE;
    }

    public void clearDeck() {
        deck.clear();
    }
}
