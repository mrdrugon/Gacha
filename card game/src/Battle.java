import java.util.ArrayList;
import java.util.List;

public class Battle {
    private List<Card> playerDeck;
    private List<Card> opponentDeck;
    private List<Card> playerBattleDeck;
    private List<Card> opponentBattleDeck;
    private int opponentIndex;
    private String lastRoundResult;
    private Card selectedPlayerCard;

    public Battle(List<Card> playerDeck, List<Card> opponentDeck) {
        this.playerDeck = playerDeck;
        this.opponentDeck = opponentDeck;
        this.playerBattleDeck = cloneDeck(playerDeck); // Create fresh copies
        this.opponentBattleDeck = cloneDeck(opponentDeck);
        this.opponentIndex = 0;
        this.lastRoundResult = "";

    }

    private List<Card> cloneDeck(List<Card> originalDeck) {
        List<Card> newDeck = new ArrayList<>();
        for (Card card : originalDeck) {
            newDeck.add(new Card(card.getName(), card.getAttack(), card.getOriginalHealth(), card.getRarity(), card.hasDoubleAttack(), card.hasRevive()));
        }
        return newDeck;
    }

    public boolean playerSelectedCard(Card card){
        for (Card battleCard : playerBattleDeck){
            if (battleCard.getName().equals(card.getName()) && battleCard.getHealth() > 0){
                selectedPlayerCard = battleCard;
                return true;
            }
        }
        return false;
    }

    public boolean playNextRound(Card selectedCard) {
        if (!playerSelectedCard(selectedCard)){
            lastRoundResult = "Invalid selection!";
            return false;
        }

        while (opponentIndex < opponentBattleDeck.size() && opponentBattleDeck.get(opponentIndex).getHealth() <= 0) {
            opponentIndex++;
        }

        if (opponentIndex >= opponentBattleDeck.size()){
            return true;
        }

        Card opponentCard = opponentBattleDeck.get(opponentIndex);

        lastRoundResult = "Player's " + selectedPlayerCard.getName() + " (ATK: " + selectedPlayerCard.getAttack() + ", HP: " + selectedPlayerCard.getHealth() + ") VS "
                + "Opponent's " + opponentCard.getName() + " (ATK: " + opponentCard.getAttack() + ", HP: " + opponentCard.getHealth() + ")\n";

        selectedPlayerCard.setHealth(selectedPlayerCard.getHealth() - opponentCard.getAttack());
        opponentCard.setHealth(opponentCard.getHealth() - selectedPlayerCard.getAttack());

        if (selectedPlayerCard.hasDoubleAttack() && selectedPlayerCard.getHealth() > 0) {
            opponentCard.setHealth(opponentCard.getHealth() - selectedPlayerCard.getAttack());
            lastRoundResult += "Player's " + selectedPlayerCard.getName() + " attacks again!\n";
        }

        if (opponentCard.hasDoubleAttack() && opponentCard.getHealth() > 0) {
            selectedPlayerCard.setHealth(selectedPlayerCard.getHealth() - opponentCard.getAttack());
            lastRoundResult += "Opponent's " + opponentCard.getName() + " attacks again!\n";
        }

        if (selectedPlayerCard.getHealth() <= 0 && opponentCard.getHealth() <= 0) {
            boolean playerRevived = reviveCard(selectedPlayerCard);
            boolean opponentRevived = reviveCard(opponentCard);

            if (!playerRevived && !opponentRevived) {
                lastRoundResult += "It's a tie! Both cards are eliminated.\n";
                opponentIndex++;
            }
        } else if (selectedPlayerCard.getHealth() <=0) {
            if (!reviveCard(selectedPlayerCard)) {
                lastRoundResult += "Opponent's " + opponentCard.getName() + " wins the round!\n";
            }
        } else if (opponentCard.getHealth() <= 0) {
            if (!reviveCard(opponentCard)){
                lastRoundResult += "Player's " +selectedPlayerCard.getName()+ " wins the round!\n";
                opponentIndex++;
            }
        } else {
            lastRoundResult += "Both cards survived the round!\n";
        }
        return opponentIndex >= opponentBattleDeck.size();
    }

    private boolean reviveCard(Card card){
        if (card.hasRevive() && !card.hasRevived()){
            card.revive();
            lastRoundResult += card.getName() + " revives with full HP!\n";
            return true;
        }
        return false;
    }

    public String getLastRoundResult () {
        return lastRoundResult;
    }

    public boolean didPlayerWin () {
        return opponentIndex >= opponentBattleDeck.size();
    }
}
