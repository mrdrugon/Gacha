import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Card> cards;
    private List<Deck> decks;
    private Deck mainDeck;

    public Inventory(){
        decks = new ArrayList<>();
        cards = new ArrayList<>();
    }

    public void addCards(List<Card> newCards){
        cards.addAll(newCards);
    }
    public void setMainDeck(int index){
        mainDeck = decks.get(index);
    }

    public List<Card> getCards(){
        return cards;
    }

    public Deck getMainDeck(){
        return mainDeck;
    }


}
