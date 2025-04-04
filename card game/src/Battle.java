import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Battle {
    private List<ICard> playerDeck;
    private List<ICard> opponentDeck;
    private Map<Integer, ICard> playerCardsMap;
    private Map<Integer, ICard> opponentCardsMap;
    private Map<ICard, Integer> poisonedCards;  // Renamed from poisendCards
    private Map<ICard, Integer> stunnedCards;
    private static ICard currentOpponentCard;

    private int opponentIndex;
    private String lastRoundResult;
    public ICard selectedPlayerCard;

    public Battle(List<ICard> playerDeck, List<ICard> opponentDeck) {
        this.playerDeck = playerDeck;
        this.opponentDeck = opponentDeck;
        this.playerCardsMap = new HashMap<>();
        this.opponentCardsMap = new HashMap<>();
        this.poisonedCards = new HashMap<>();
        this.stunnedCards = new HashMap<>();
        this.opponentIndex = 0;
        this.lastRoundResult = "";

        // Map player's cards by their ID.
        for (ICard card : playerDeck) {
            playerCardsMap.put(card.getId(), card);
        }
        // Map opponent's cards by their ID.
        for (ICard card : opponentDeck) {
            opponentCardsMap.put(card.getId(), card);
        }
    }

    public static ICard getCurrentOpponentCard(){
        return currentOpponentCard;
    }

    public void addPoisonedCard(ICard card, int poisonDamage){
        poisonedCards.put(card, poisonDamage);
    }

    public boolean playerSelectedCard(ICard selectedCard) {
        ICard cardToSelect = playerCardsMap.get(selectedCard.getId());
        if (cardToSelect != null && cardToSelect.getHealth() > 0) {
            selectedPlayerCard = cardToSelect;
            return true;
        }
        return false;
    }

    // Removed the unused parameter; the method now uses the field selectedPlayerCard.
    public boolean playNextRound() {
        // Skip over any defeated opponent cards.
        while (opponentIndex < opponentDeck.size() && opponentDeck.get(opponentIndex).getHealth() <= 0) {
            opponentIndex++;
        }
        if (opponentIndex >= opponentDeck.size()) {
            lastRoundResult = "No opponent cards left.";
            return true;  // Battle over.
        }

        ICard opponentCard = getNextOpponentCard();
        if (opponentCard == null) {
            lastRoundResult = "No opponent card available.";
            return true;
        }

        // Retrieve attack values.
        int playerAttack = selectedPlayerCard.getAttack();
        int opponentAttack = opponentCard.getAttack();

        // Set current opponent card for abilities that may reference it.
        currentOpponentCard = opponentCard;

        // Basic damage exchange.
        selectedPlayerCard.takeDamage(opponentAttack);
        opponentCard.takeDamage(playerAttack);

        // Handle double attack ability.
        if (selectedPlayerCard instanceof DoubleAttackDecorator &&
                selectedPlayerCard.getHealth() > 0 && opponentCard.getHealth() > 0) {
            opponentCard.takeDamage(playerAttack);
        }
        if (opponentCard instanceof DoubleAttackDecorator &&
                opponentCard.getHealth() > 0 && selectedPlayerCard.getHealth() > 0) {
            selectedPlayerCard.takeDamage(opponentAttack);
        }

        // Handle damage boost ability.
        int playerBoostAttack = (int) Math.round(selectedPlayerCard.getAttack() * 1.2);
        int opponentBoostAttack = (int) Math.round(opponentCard.getAttack() * 1.2);
        if (selectedPlayerCard instanceof DamageBoostDecorator &&
                selectedPlayerCard.getHealth() > 0 && opponentCard.getHealth() > 0) {
            opponentCard.takeDamage(playerBoostAttack);
        }
        if (opponentCard instanceof DamageBoostDecorator &&
                opponentCard.getHealth() > 0 && selectedPlayerCard.getHealth() > 0) {
            selectedPlayerCard.takeDamage(opponentBoostAttack);
        }

        // Handle revive ability.
        if (selectedPlayerCard.getHealth() <= 0 && selectedPlayerCard instanceof ReviveDecorator) {
            ((ReviveDecorator) selectedPlayerCard).revive();
        }
        if (opponentCard.getHealth() <= 0 && opponentCard instanceof ReviveDecorator) {
            ((ReviveDecorator) opponentCard).revive();
        }

        // If the opponent card is defeated, move to the next one.
        if (opponentCard.getHealth() <= 0) {
            opponentIndex++;
        }

        // Update the last round result message.
        lastRoundResult = "Player card HP: " + selectedPlayerCard.getHealth() +
                " | Opponent card HP: " + opponentCard.getHealth();

        // Return true if there are no opponent cards left.
        return opponentIndex >= opponentDeck.size();
    }

    public String getLastRoundResult() {
        return lastRoundResult;
    }

    public boolean didPlayerWin() {
        return opponentIndex >= opponentDeck.size();
    }

    public boolean isOpponentDefeated() {
        for (ICard card : opponentDeck) {
            if (card.getHealth() > 0) {
                return false;
            }
        }
        return true;
    }

    public String getBattleOutcome() {
        boolean playerDefeated = true;
        boolean opponentDefeated = true;

        for (ICard card : playerDeck) {
            if (card.getHealth() > 0) {
                playerDefeated = false;
                break;
            }
        }
        for (ICard card : opponentDeck) {
            if (card.getHealth() > 0) {
                opponentDefeated = false;
                break;
            }
        }
        if (playerDefeated && opponentDefeated) {
            return "tie";
        } else if (playerDefeated) {
            return "loss";
        } else {
            return "win";
        }
    }

    public ICard getNextOpponentCard() {
        while (opponentIndex < opponentDeck.size()) {
            ICard nextCard = opponentDeck.get(opponentIndex);
            if (nextCard.getHealth() > 0) {
                return nextCard;
            }
            opponentIndex++;
        }
        return null;
    }
}