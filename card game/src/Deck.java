import java.util.ArrayList;
import java.util.List;

public class Deck {
    private ArrayList<Card> deck;
    public static final int DECK_SIZE = 5;

    public Deck() {
        this.deck = new ArrayList<>();
    }

    public List<Card> getDeck() {
        return new ArrayList<>(deck);
    }

    public boolean addCard(Card card) {
        if (deck.size() < DECK_SIZE) {
            // Check if the card already exists in the deck based on its ID
            for (Card deckCard : deck) {
                if (deckCard.getId() == card.getId()) {
                    return false;  // Card already in deck
                }
            }
            deck.add(card);  // Add the card to the deck if it's not already there
            return true;
        }
        return false;
    }

    public boolean removeCard(int cardId) {
        // Remove the card from the deck based on its ID
        return deck.removeIf(card -> card.getId() == cardId);
    }

    public boolean isFull() {
        return deck.size() == DECK_SIZE;
    }

    public void clearDeck() {
        deck.clear();
    }
}