import java.util.ArrayList;
import java.util.List;

public class Deck {
    private ArrayList<Card> deck;
    public static final int DECK_SIZE = 5;

    public Deck() {
        this.deck = new ArrayList<>();
    }

    public List<Card> getCards(){
        return deck;
    }

    public boolean addCard(Card card){
        if (deck.size() < DECK_SIZE && !deck.contains(card)){
            deck.add(card);
            return true;
        }
        return false;
    }

    public boolean removeCard(int cardId){
        return deck.removeIf(card -> card.getId() == cardId);
    }

    public List<Card> getDeck(){
        return new ArrayList<>(deck);
    }

    public boolean setDeck(List<Card> selectedDeck){
        if (selectedDeck.size() == DECK_SIZE){
            deck = new ArrayList<>(selectedDeck);
            return true;
        }
        return false;
    }

    public boolean isFull(){
        return deck.size() == DECK_SIZE;
    }

    public void clearDeck(){
        deck.clear();
    }
}
