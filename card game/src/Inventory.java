import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private List<Card> cards;  // Full collection
    private Map<Integer, Integer> cardCounts;
    private Deck deck;

    public Inventory(){
        cards = new ArrayList<>();
        cardCounts = new HashMap<>();
        deck = new Deck();
    }

    public void addCards(List<Card> newCards){
        for (Card card : newCards){
            cardCounts.put(card.getId(), cardCounts.getOrDefault(card.getId(), 0) + 1);
            if (!cards.contains(card)){
                cards.add(card);
            }
        }
    }

    public List<Card> getCards(){
        return new ArrayList<>(cards);
    }

    public Deck getDeck(){
        return deck;
    }

    public int getCardCount(int cardId){
        return cardCounts.getOrDefault(cardId, 0);
    }

    public Card findCardById(int cardId){
        for (Card card : cards){
            if (card.getId() == cardId){
                return card;
            }
        }
        return null;
    }

    public void removeCard(int cardId){
        cardCounts.put(cardId, cardCounts.getOrDefault(cardId, 1) -1);
        if (cardCounts.get(cardId) <= 0){
            cardCounts.remove(cardId);
            cards.removeIf(card -> card.getId() == cardId);
        }
    }
}
