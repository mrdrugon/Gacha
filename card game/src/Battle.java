import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Battle {
    private List<ICard> playerDeck;
    private List<ICard> opponentDeck;
    private Map<Integer, ICard> playerCardsMap;
    private Map<Integer, ICard> opponentCardsMap;
    private Map<ICard, Integer> poisendCards;

    private int opponentIndex;
    private String lastRoundResult;
    public ICard selectedPlayerCard;

    public Battle(List<ICard> playerDeck, List<ICard> opponentDeck) {
        this.playerDeck = playerDeck;
        this.opponentDeck = opponentDeck;
        this.playerCardsMap = new HashMap<>();
        this.opponentCardsMap = new HashMap<>();
        this.poisendCards = new HashMap<>();
        this.opponentIndex = 0;
        this.lastRoundResult = "";

        for (ICard card : playerDeck) {
            playerCardsMap.put(card.getId(), card);
        }

        for (ICard card : opponentDeck) {
            opponentCardsMap.put(card.getId(), card);
        }
    }

    public void addPoisonedCard(ICard card, int poisonDamage){
        poisendCards.put(card, poisonDamage);
    }

    public boolean playerSelectedCard(ICard selectedCard) {
        ICard cardToSelect = playerCardsMap.get(selectedCard.getId());
        if (cardToSelect != null && cardToSelect.getHealth() > 0) {
            selectedPlayerCard = cardToSelect;
            return true;
        }
        return false;
    }

    public boolean playNextRound(ICard selectedCard) {
        while (opponentIndex < opponentDeck.size() && opponentDeck.get(opponentIndex).getHealth() <= 0) {
            opponentIndex++;
        }

        if (opponentIndex >= opponentDeck.size()) {
            return true;
        }

        ICard opponentCard = getNextOpponentCard();
        if (opponentCard == null) return true;

        int playerAttack = selectedPlayerCard.getAttack();
        int opponentAttack = opponentCard.getAttack();

        // Check if player card has SelfObserve ability
        if (selectedPlayerCard instanceof SelfObserveDecorator) {
            playerAttack = selectedPlayerCard.getAttack(); // Ensures attack is recalculated
        }

        // Check if opponent card has SelfObserve ability
        if (opponentCard instanceof SelfObserveDecorator) {
            opponentAttack = opponentCard.getAttack();
        }

        //Apply Poison
        if (poisendCards.containsKey(opponentCard)){
            int poisonDmg = poisendCards.get(opponentCard);
            opponentCard.takeDamage(poisonDmg);

            if (opponentCard.getHealth() <= 0){
                poisendCards.remove(opponentCard);
            }
        }

        if (poisendCards.containsKey(selectedPlayerCard)){
            int poisonDmg = poisendCards.get(selectedCard);
            selectedPlayerCard.takeDamage(poisonDmg);

            if (selectedPlayerCard.getHealth() <= 0){
                poisendCards.remove(selectedPlayerCard);
            }
        }

        if (selectedPlayerCard instanceof PoisonAttackDecorator){
            ((PoisonAttackDecorator) selectedPlayerCard).applyPoisonEffect(this, opponentCard);
        }

        if (opponentCard instanceof PoisonAttackDecorator){
            ((PoisonAttackDecorator) opponentCard).applyPoisonEffect(this, selectedPlayerCard);
        }

        //triple slice
        if (selectedCard instanceof TripleSliceDecorator){
            ((TripleSliceDecorator) selectedPlayerCard).attack(opponentCard);
        } else {
            opponentCard.takeDamage(playerAttack);
        }

        if (opponentCard instanceof TripleSliceDecorator){
            ((TripleSliceDecorator) opponentCard).attack(selectedPlayerCard);
        } else {
            selectedPlayerCard.takeDamage(opponentAttack);
        }

        lastRoundResult = "Player's " + selectedPlayerCard.getName() + " (ATK: " + playerAttack + ", HP: " + selectedPlayerCard.getHealth() + ") VS "
                + "Opponent's " + opponentCard.getName() + " (ATK: " + opponentAttack + ", HP: " + opponentCard.getHealth() + ")\n";

        selectedPlayerCard.takeDamage(opponentAttack);
        opponentCard.takeDamage(playerAttack);

        // Handle double attack ability
        if (selectedPlayerCard instanceof DoubleAttackDecorator && selectedPlayerCard.getHealth() > 0 && opponentCard.getHealth() > 0) {
            opponentCard.takeDamage(playerAttack);
            lastRoundResult += "Player's " + selectedPlayerCard.getName() + " attacks again!\n";
        }
        if (opponentCard instanceof DoubleAttackDecorator && opponentCard.getHealth() > 0 && selectedPlayerCard.getHealth() > 0) {
            selectedPlayerCard.takeDamage(opponentAttack);
            lastRoundResult += "Opponent's " + opponentCard.getName() + " attacks again!\n";
        }

        // Check for revive ability
        if (selectedPlayerCard.getHealth() <= 0 && selectedPlayerCard instanceof ReviveDecorator) {
            ((ReviveDecorator) selectedPlayerCard).revive();
            lastRoundResult += selectedPlayerCard.getName() + " revives with full HP!\n";
        }
        if (opponentCard.getHealth() <= 0 && opponentCard instanceof ReviveDecorator) {
            ((ReviveDecorator) opponentCard).revive();
            lastRoundResult += opponentCard.getName() + " revives with full HP!\n";
        }

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
            if (nextCard.getHealth() > 0) return nextCard;
            opponentIndex++;
        }
        return null;
    }
}
