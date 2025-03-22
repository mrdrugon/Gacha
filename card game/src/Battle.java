import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Battle {
    private List<Card> playerDeck;
    private List<Card> opponentDeck;
    private Map<Integer, Card> playerCardsMap;
    private Map<Integer, Card> opponentCardsMap;

    private int opponentIndex;
    private String lastRoundResult;
    public Card selectedPlayerCard;

    public Battle(List<Card> playerDeck, List<Card> opponentDeck) {
        this.playerDeck = playerDeck;
        this.opponentDeck = opponentDeck;
        this.playerCardsMap = new HashMap<>();
        this.opponentCardsMap = new HashMap<>();
        this.opponentIndex = 0;
        this.lastRoundResult = "";

        for (Card card : playerDeck){
            playerCardsMap.put(card.getId(), card);
        }

        for (Card card : opponentDeck){
            opponentCardsMap.put(card.getId(), card);
        }

    }

    public boolean playerSelectedCard(Card selectedCard){
        Card cardToSelect = playerCardsMap.get(selectedCard.getId());
            if (cardToSelect != null && cardToSelect.getHealth() > 0){
                selectedPlayerCard = cardToSelect;
                return true;
            }
        return false;
    }

    public boolean playNextRound(Card selectedCard) {
        while (opponentIndex < opponentDeck.size() && opponentDeck.get(opponentIndex).getHealth() <= 0) {
            opponentIndex++;
        }

        if (opponentIndex >= opponentDeck.size()) {
            return true;
        }

        Card opponentCard = getNextOpponentCard();
        if (opponentCard == null) return true;

        lastRoundResult = "Player's " + selectedPlayerCard.getName() + " (ATK: " + selectedPlayerCard.getAttack() + ", HP: " + selectedPlayerCard.getHealth() + ") VS "
                + "Opponent's " + opponentCard.getName() + " (ATK: " + opponentCard.getAttack() + ", HP: " + opponentCard.getHealth() + ")\n";

        selectedPlayerCard.takeDamage(opponentCard.getAttack());
        opponentCard.takeDamage(selectedPlayerCard.getAttack());

        if (selectedPlayerCard.hasDoubleAttack() && selectedPlayerCard.getHealth() > 0 && opponentCard.getHealth() > 0) {
            opponentCard.takeDamage(selectedPlayerCard.getAttack());
            lastRoundResult += "Player's " + selectedPlayerCard.getName() + " attacks again!\n";
        }

        if (opponentCard.hasDoubleAttack() && opponentCard.getHealth() > 0 && selectedPlayerCard.getHealth() > 0) {
            selectedPlayerCard.takeDamage(opponentCard.getAttack());
            lastRoundResult += "Opponent's " + opponentCard.getName() + " attacks again!\n";
        }

        boolean playerRevived = selectedPlayerCard.getHealth() <= 0 && reviveCard(selectedPlayerCard);
        boolean opponentRevived = opponentCard.getHealth() <= 0 && reviveCard(opponentCard);

        if (!playerRevived && !opponentRevived) {
            if (selectedPlayerCard.getHealth() <= 0 && opponentCard.getHealth() <= 0) {
                lastRoundResult += "It's a tie! Both cards are eliminated.\n";
                opponentIndex++;
            } else if (selectedPlayerCard.getHealth() <= 0) {
                lastRoundResult += "Opponent's " + opponentCard.getName() + " wins the round!\n";
            } else if (opponentCard.getHealth() <= 0) {
                lastRoundResult += "Player's " + selectedPlayerCard.getName() + " wins the round!\n";
                opponentIndex++;
            } else {
                lastRoundResult += "Both cards survived the round!\n";
            }
        }
        return opponentIndex >= opponentDeck.size();
    }

    private boolean reviveCard(Card card){
        if (card.hasRevive() && !card.hasRevived() && card.getHealth() <= 0){
            card.revive();
            card.setHealth(card.getMaxHealth());
            lastRoundResult += card.getName() + " revives with full HP!\n";
            return true;
        }
        return false;
    }

    public String getLastRoundResult () {
        return lastRoundResult;
    }

    public boolean didPlayerWin () {
        return opponentIndex >= opponentDeck.size();
    }

    public boolean isOpponentDefeated(){
        return opponentDeck.isEmpty();
    }

    public String getBattleOutcome(){
        if (playerDeck.isEmpty() && opponentDeck.isEmpty()){
            return "tie";
        } else if (playerDeck.isEmpty()){
            return "loss";
        } else {
            return "win";
        }
    }

    private Card getNextOpponentCard(){
        while (opponentIndex < opponentDeck.size()){
            Card nextCard = opponentDeck.get(opponentIndex);
            if (nextCard.getHealth() > 0) return nextCard;
            opponentIndex++;
        }
        return null;
    }
}