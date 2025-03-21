import java.util.ArrayList;
import java.util.List;

public class Deck {
    private ArrayList<Card> deck;
    private static final int DECK_SIZE = 5;

    public Deck() {
        this.deck = new ArrayList<>();
    }
    public void addToDeck(Card card) throws Exception {
        if (deck.size() < DECK_SIZE){
            deck.add(card);
        }
        else {
            Exception IndexOutOfBoundsException = new Exception("deck already empty!");
            throw IndexOutOfBoundsException;
        }
    }

    public void removeFromDeck(int index) throws Exception {
        if(index >= 0 && index < deck.size()){
            deck.remove(index);
        }
        else {
            Exception IndexOutOfBoundsException = new Exception("deck already empty!");
            throw IndexOutOfBoundsException;
        }
    }

    public void setDeck(List<Card> selectedDeck){
        if (selectedDeck.size() == DECK_SIZE){
            deck = new ArrayList<>(selectedDeck);
        }
    }
}
