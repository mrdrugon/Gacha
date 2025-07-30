import java.awt.*;
import java.util.*;
import java.util.List;

public class Battle {
    public Random random;
    private List<ICard> playerDeck;
    private List<ICard> opponentDeck;
    private Map<Integer, ICard> playerCardsMap;
    private Map<Integer, ICard> opponentCardsMap;
    private Map<ICard, Integer> poisonedCards;
    private static ICard currentOpponentCard;
    private Map<ICard, Integer> frozenCards = new HashMap<>();
    private Map<ICard, Integer> nextFrozenCards = new HashMap<>();
    private final Map<ICard, Integer> freezeImmunity = new HashMap<>();
    private Map<ICard, Integer> burningCards = new HashMap<>();
    private final Map<ICard, Integer> lavaSurgeCards = new HashMap<>();
    private Map<ICard, Integer> glacialShieldExpiry = new HashMap<>();
    private int globalTurnCounter = 0;
    private Map<ICard, Integer> galeForceDebuff = new HashMap<>();
    private int opponentIndex;
    public ICard selectedPlayerCard;
    private final Set<ICard> revealedOpponentCards = new HashSet<>();
    private boolean isSecondAttack = false;
    private ICard cachedOpponentCard;

    public Battle(List<ICard> playerDeck, List<ICard> opponentDeck) {
        this.playerDeck = playerDeck;
        this.opponentDeck = opponentDeck;
        this.playerCardsMap = new HashMap<>();
        this.opponentCardsMap = new HashMap<>();
        this.poisonedCards = new HashMap<>();
        this.opponentIndex = 0;
        this.random = new Random();

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

        if (selectedPlayerCard.isStunned()) {
            selectedPlayerCard.setStunned(false);
            return false;
        }

        if (isOpponentDefeated()) return true;

        // Pick and store the opponent card ONCE for this turn
        currentOpponentCard = getNextOpponentCard();
        ICard opponentCard = consumeOpponentCard();

        if (opponentCard == null) return true;

        // Trigger start-of-turn effects
        if (selectedPlayerCard instanceof BasicCard) {
            ((BasicCard) selectedPlayerCard).startTurn(this);
        }
        if (opponentCard instanceof BasicCard) {
            ((BasicCard) opponentCard).startTurn(this);
        }

        // Player attacks first
        if (!isFrozen(selectedPlayerCard)) {
            if (selectedPlayerCard instanceof BasicCard) {
                ((BasicCard) selectedPlayerCard).attack(opponentCard, this);
            } else {
                opponentCard.takeDamageWithAbilities(selectedPlayerCard.getAttack(), this);
            }
        }

        // Opponent attacks if still alive
        if (opponentCard.getHealth() > 0 && !isFrozen(opponentCard)) {
            if (opponentCard instanceof BasicCard) {
                ((BasicCard) opponentCard).attack(selectedPlayerCard, this);
            } else {
                selectedPlayerCard.takeDamageWithAbilities(opponentCard.getAttack(), this);
            }
        }

        // End-of-turn effects
        if (selectedPlayerCard instanceof BasicCard) {
            ((BasicCard) selectedPlayerCard).endTurn(this);
        }
        if (opponentCard instanceof BasicCard) {
            ((BasicCard) opponentCard).endTurn(this);
        }

        // Death abilities
        if (selectedPlayerCard.getHealth() <= 0) {
            for (AbilityType type : selectedPlayerCard.getAbilities()) {
                CardAbilities.onDeath(selectedPlayerCard, opponentCard, selectedPlayerCard, opponentCard, type, this);
            }
        }

        if (opponentCard.getHealth() <= 0) {
            for (AbilityType type : opponentCard.getAbilities()) {
                CardAbilities.onDeath(opponentCard, selectedPlayerCard, opponentCard, selectedPlayerCard, type, this);
            }
        }

        updateFrozenCards();
        updateGlacialShields();

        globalTurnCounter++;
        decrementBurningCards();
        tickGaleForceDebuff();

        return isOpponentDefeated(); // return true if battle is over
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

    public ICard getSelectedPlayerCard(){
        return selectedPlayerCard;
    }

    public List<ICard> getOpponentDeckFor(ICard card) {
        return playerDeck.contains(card) ? opponentDeck : playerDeck;
    }

    public int getGlobalTurnCounter() {
        return globalTurnCounter;
    }

    public void updateGlacialShields() {
        glacialShieldExpiry.entrySet().removeIf(entry ->
                entry.getValue() <= globalTurnCounter
        );
    }

    public void setGlacialShield(ICard card, int turns) {
        glacialShieldExpiry.put(card, globalTurnCounter + turns);
    }

    public boolean isGlacialShieldActive(ICard card) {
        return glacialShieldExpiry.getOrDefault(card, 0) > globalTurnCounter;
    }

    public Map<ICard, Integer> getLavaSurgeCards() {
        return lavaSurgeCards;
    }

    public void updateFrozenCards() {
        for (Map.Entry<ICard, Integer> entry : nextFrozenCards.entrySet()) {
            ICard card = entry.getKey();
            if (!isFreezeImmune(card)) {
                frozenCards.put(card, entry.getValue());
            }
        }
        nextFrozenCards.clear();

        frozenCards.entrySet().removeIf(entry -> {
            ICard card = entry.getKey();
            int newDuration = entry.getValue() - 1;
            if (newDuration <= 0) {
                applyFreezeImmunity(card, 2); // Give 2 turns of immunity after thawing
                return true;
            } else {
                entry.setValue(newDuration);
                return false;
            }
        });

        // Decrease immunity timers
        freezeImmunity.entrySet().removeIf(entry -> entry.setValue(entry.getValue() - 1) <= 0);
    }

    public boolean isFreezeImmune(ICard card) {
        return freezeImmunity.getOrDefault(card, 0) > 0;
    }

    public void applyFreezeImmunity(ICard card, int turns) {
        freezeImmunity.put(card, turns);
    }

    public boolean isSecondAttack() {
        return isSecondAttack;
    }

    public void setSecondAttack(boolean secondAttack) {
        this.isSecondAttack = secondAttack;
    }

    public void freezeCard(ICard card, int turns) {
        // Only apply if no freeze is active or next one is longer
        int current = frozenCards.getOrDefault(card, 0);
        int next = nextFrozenCards.getOrDefault(card, 0);
        if (current == 0 && next == 0) {
            nextFrozenCards.put(card, turns);
        } else if (turns > Math.max(current, next)) {
            nextFrozenCards.put(card, turns);
        }
    }

    public boolean isFrozen(ICard card) {
        return frozenCards.getOrDefault(card, 0) > 0;
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
            return "win";
    }

    public ICard getNextOpponentCard() {
        List<ICard> aliveOpponents = new ArrayList<>();
        for (ICard card : opponentDeck) {
            if (card.getHealth() > 0) {
                aliveOpponents.add(card);
            }
        }
        if (aliveOpponents.isEmpty()) {
            return null;
        }
        return aliveOpponents.get(random.nextInt(aliveOpponents.size()));
    }

    public ICard selectNextOpponentCard(){
        if(cachedOpponentCard == null || cachedOpponentCard.getHealth() <= 0){
            List<ICard> aliveCards = getAliveCards(opponentDeck);
            if (!aliveCards.isEmpty()){
                cachedOpponentCard = aliveCards.get(new Random().nextInt(aliveCards.size()));
            }
        }
        revealOpponentCard(cachedOpponentCard);
        return cachedOpponentCard;
    }

    public ICard consumeOpponentCard(){
        ICard selected = cachedOpponentCard;
        cachedOpponentCard = null;
        return selected;
    }

    private List<ICard> getAliveCards(List<ICard> deck){
        List<ICard> aliveCards = new ArrayList<>();
        for (ICard card : deck){
            if (card.getHealth() > 0){
                aliveCards.add(card);
            }
        }
        return aliveCards;
    }

    public void revealOpponentCard(ICard card){
        if (card != null){
            revealedOpponentCards.add(card);
        }
    }

    public Set<ICard> getRevealedOpponentCards() {
        return revealedOpponentCards;
    }

}