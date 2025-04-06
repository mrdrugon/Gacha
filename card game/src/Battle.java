import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Battle {
    private List<ICard> playerDeck;
    private List<ICard> opponentDeck;
    private Map<Integer, ICard> playerCardsMap;
    private Map<Integer, ICard> opponentCardsMap;
    private Map<ICard, Integer> poisonedCards;
    private Map<ICard, Integer> stunnedCards;
    private static ICard currentOpponentCard;

    private int opponentIndex;
    public ICard selectedPlayerCard;

    public Battle(List<ICard> playerDeck, List<ICard> opponentDeck) {
        this.playerDeck = playerDeck;
        this.opponentDeck = opponentDeck;
        this.playerCardsMap = new HashMap<>();
        this.opponentCardsMap = new HashMap<>();
        this.poisonedCards = new HashMap<>();
        this.stunnedCards = new HashMap<>();
        this.opponentIndex = 0;

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

        if (opponentIndex >= opponentDeck.size()) return true;

        ICard opponentCard = getNextOpponentCard();
        if (opponentCard == null) return true;

        // Set current opponent for reference
        currentOpponentCard = opponentCard;

        // Trigger turn start effects
        if (selectedPlayerCard instanceof BasicCard) {
            ((BasicCard) selectedPlayerCard).startTurn(this);
        }
        if (opponentCard instanceof BasicCard) {
            ((BasicCard) opponentCard).startTurn(this);
        }


        // Player attacks opponent
        if (selectedPlayerCard instanceof BasicCard) {
            ((BasicCard) selectedPlayerCard).attack(opponentCard, this);
        } else {
            opponentCard.takeDamageWithAbilities(selectedPlayerCard.getAttack(), this);
        }

        // Opponent attacks player (if still alive)
        if (opponentCard.getHealth() > 0) {
            if (opponentCard instanceof BasicCard) {
                ((BasicCard) opponentCard).attack(selectedPlayerCard, this);
            } else {
                selectedPlayerCard.takeDamageWithAbilities(opponentCard.getAttack(), this);
            }
        }

        // Trigger end-of-turn effects
        if (selectedPlayerCard instanceof BasicCard) {
            ((BasicCard) selectedPlayerCard).endTurn(this);
        }
        if (opponentCard instanceof BasicCard) {
            ((BasicCard) opponentCard).endTurn(this);
        }

        // If the opponent card is defeated, move to the next one.
        if (opponentCard.getHealth() <= 0) {
            opponentIndex++;
        }

        // Return true if there are no opponent cards left.
        return opponentIndex >= opponentDeck.size();
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