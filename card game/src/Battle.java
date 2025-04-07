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
    private Map<ICard, Integer> frozenCards = new HashMap<>();
    private Map<ICard, Integer> burningCards = new HashMap<>();
    private final Map<ICard, Integer> lavaSurgeCards = new HashMap<>();
    private Map<ICard, Integer> glacialShieldedCards = new HashMap<>();
    private int globalTurnCounter = 0;
    private Map<ICard, Integer> galeForceDebuff = new HashMap<>();


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


        if (!isFrozen(selectedPlayerCard)) {
            if (selectedPlayerCard instanceof BasicCard) {
                ((BasicCard) selectedPlayerCard).attack(opponentCard, this);
            } else {
                opponentCard.takeDamageWithAbilities(selectedPlayerCard.getAttack(), this);
            }
        }

        // Opponent attacks player (if still alive)
        if (opponentCard.getHealth() > 0 && !isFrozen(opponentCard)) {
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

        if (selectedPlayerCard.getHealth() <= 0) {
            for (AbilityType type : selectedPlayerCard.getAbilities()) {
                CardAbilities.onDeath(selectedPlayerCard, opponentCard, type, this);
            }
        }

        // If the opponent card is defeated, move to the next one.
        if (opponentCard.getHealth() <= 0) {
            opponentIndex++;
            for (AbilityType type : opponentCard.getAbilities()) {
                CardAbilities.onDeath(opponentCard, selectedPlayerCard, type, this);
            }

            glacialShieldedCards.entrySet().removeIf(entry -> {
                int turnsLeft = entry.getValue() - 1;
                if (turnsLeft <= 0) {
                    return true;
                } else {
                    glacialShieldedCards.put(entry.getKey(), turnsLeft);
                    return false;
                }
            });
        }

        globalTurnCounter++;
        decrementFrozenCards();
        decrementBurningCards();
        tickGaleForceDebuff();

        // Return true if there are no opponent cards left.
        return opponentIndex >= opponentDeck.size();
    }

    public void applyGaleForceDebuff(ICard target) {
        galeForceDebuff.put(target, 2); // lasts 2 turns
        target.setAttack(target.getAttack() - 4);
    }

    public void tickGaleForceDebuff() {
        galeForceDebuff.entrySet().removeIf(entry -> {
            ICard card = entry.getKey();
            int turnsLeft = entry.getValue() - 1;

            if (turnsLeft <= 0) {
                card.setAttack(card.getAttack() + 4); // restore the attack
                return true;
            } else {
                entry.setValue(turnsLeft);
                return false;
            }
        });
    }

    public List<ICard> getOpponentDeckFor(ICard card) {
        return playerDeck.contains(card) ? opponentDeck : playerDeck;
    }

    public int getGlobalTurnCounter() {
        return globalTurnCounter;
    }

    public void setGlacialShield(ICard card, int turns) {
        glacialShieldedCards.put(card, turns);
    }

    public boolean isGlacialShieldActive(ICard card) {
        return glacialShieldedCards.containsKey(card);
    }

    public Map<ICard, Integer> getLavaSurgeCards() {
        return lavaSurgeCards;
    }

    public void freezeCard(ICard card, int turns) {
        frozenCards.put(card, turns);
    }

    public boolean isFrozen(ICard card) {
        return frozenCards.getOrDefault(card, 0) > 0;
    }

    private void decrementFrozenCards() {
        frozenCards.replaceAll((card, turns) -> Math.max(0, turns - 1));
    }

    public void applyBurn(ICard card, int turns) {
        burningCards.put(card, turns);
    }

    private void decrementBurningCards() {
        Map<ICard, Integer> updated = new HashMap<>();
        for (Map.Entry<ICard, Integer> entry : burningCards.entrySet()) {
            ICard card = entry.getKey();
            int turnsLeft = entry.getValue();
            if (turnsLeft > 0 && card.getHealth() > 0) {
                card.takeDamageWithAbilities(3, this);
                updated.put(card, turnsLeft - 1);
            }
        }
        burningCards = updated;
    }

    public boolean isPlayerCard(ICard card) {
        return playerDeck.contains(card);
    }

    public List<ICard> getPlayerDeck() {
        return playerDeck;
    }

    public List<ICard> getOpponentDeck() {
        return opponentDeck;
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