import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Card> cards;
    private List<Card> deck;
    private static final int DECK_SIZE = 5;

    public Inventory(){
        cards = new ArrayList<>();
        deck = new ArrayList<>();
    }

    public void addCards(List<Card> newCards){
        cards.addAll(newCards);
    }

    public List<Card> getCards(){
        return cards;
    }

    public List<Card> getDeck(){
        return deck;
    }

    public boolean addToDeck(Card card){
        if (deck.size() < DECK_SIZE){
            deck.add(card);
            return true;
        }
        return false;
    }

    public void removeFromDeck(int index){
        if(index >= 0 && index < deck.size()){
            deck.remove(index);
        }
    }

    public void setDeck(List<Card> selectedDeck){
        if (selectedDeck.size() == DECK_SIZE){
            deck = new ArrayList<>(selectedDeck);
        }
    }
}
