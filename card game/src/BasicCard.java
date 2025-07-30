import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasicCard implements ICard {
    private static int counter = 1;
    private int id;
    private String name;
    private int attack;
    private int health;
    private int originalHealth;
    private String rarity;
    private List<AbilityType> abilities;
    private boolean hasRevived = false;
    private boolean hasUsedShadowDrain = false;
    private int burnTurnsLeft = 0;
    private int temporaryAttackReduction = 0;
    private int attackReductionTurnsLeft = 0;
    private boolean stunned = false;
    private int hexTurnsLeft = 0;
    private int turnsSinceLastRootsHeal = 0;
    private int soothingBloomCounter = 0;
    private boolean isLuringTarget = false;
    private boolean hasDodged = false;
    private boolean attackedThisTurn = false;
    private int shieldAmount = 0;
    private final List<AbilityType> pendingAbilities = new ArrayList<>();
    private Set<AbilityType> copiedAbilities = new HashSet<>();
    private boolean attackedLastTurn = false;

    public BasicCard(String name, int attack, int health, String rarity, List<AbilityType> abilityType) {
        this.name = name;
        this.attack = attack;
        this.health = health;
        this.originalHealth = health;
        this.rarity = rarity;
        this.id = counter++;
        this.abilities = (abilityType == null) ? new ArrayList<>() : new ArrayList<>(abilityType);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAttack() {
        return attack;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getOriginalHealth() {
        return originalHealth;
    }

    @Override
    public void setHealth(int health) {
        this.health = Math.min(health, originalHealth);
    }

    public void setAttack(int attack){
        this.attack = Math.max(0, attack);
    }

    @Override
    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    @Override
    public void resetHealth() {
        health = originalHealth;
    }

    public String getRarity() {
        return rarity;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public List<AbilityType> getAbilities() {
        return new ArrayList<>(abilities);
    }

    @Override
    public void addAbility(AbilityType ability) {
        abilities.add(ability);
    }

    private boolean hasRebirthed = false;

    public boolean hasRebirthed() {
        return hasRebirthed;
    }

    public void setRebirthed(boolean rebirthed) {
        this.hasRebirthed = rebirthed;
    }

    public void applyTemporaryAttackReduction(int amount, int turns){
        temporaryAttackReduction = amount;
        attackReductionTurnsLeft = turns;
    }

    public void tickAttackReduction(){
        if (attackReductionTurnsLeft > 0){
            attackReductionTurnsLeft--;
            if (attackReductionTurnsLeft == 0){
                temporaryAttackReduction = 0;
            }
        }
    }

    public void attack(ICard target, Battle battle) {
        if (this.stunned) {
            return;
        }

        attackedThisTurn = true;
        boolean usedFlameFury = false;

        for (AbilityType type : new ArrayList<>(abilities)) {
            CardAbilities.onAttack(this, target, target, type, battle);
            if (type == AbilityType.FLAME_FURY && (double) this.health / this.originalHealth < 0.3) {
                usedFlameFury = true;
            }
        }

        if (!usedFlameFury) {
            target.takeDamageWithAbilities(getEffectiveAttack(), battle);
        }

        if (target.getHealth() <= 0 && abilities.contains(AbilityType.TIDE_TURN)){
            int newHealth = this.getHealth() + 10;
            this.setHealth(newHealth);
        }
    }

    @Override
    public void takeDamageWithAbilities(int damage, Battle battle) {

        if (hasShield()){
            int absorbed = Math.min(damage,shieldAmount);
            damage -= absorbed;
            shieldAmount -= absorbed;
            if (damage > 0){
                this.health = Math.max(0, this.health - damage);
            }
        }

        if (this.getAbilities().contains(AbilityType.DEEP_SLIP) && !this.hasDodged) {
            this.setHasDodged(true);
            return; // No damage taken
        }
        if (battle.isGlacialShieldActive(this)) {
            damage = (int) Math.ceil(damage * 0.5);
        }

        this.health = Math.max(0, this.health - damage);

        ICard attacker = (this == battle.selectedPlayerCard)
                ? Battle.getCurrentOpponentCard()
                : battle.selectedPlayerCard;

        boolean usedFlameFury = false;
        boolean usedWhiteStrike = false;

        for (AbilityType type : abilities) {
            CardAbilities.onDefend(attacker, this, type, battle);

            if (type == AbilityType.WHITE_STRIKE) {
                List<ICard> allies = battle.getPlayerDeck(); // same check here
                boolean isPlayer = this == battle.getSelectedPlayerCard();
                List<ICard> friendly = isPlayer ? battle.getPlayerDeck() : battle.getOpponentDeck();
                long aliveOthers = friendly.stream().filter(c -> c.getHealth() > 0 && c != this).count();
                if (aliveOthers == 0) usedWhiteStrike = true;
            }
        }

        List<ICard> friendlyDeck = battle.getPlayerDeck().contains(this) ? battle.getPlayerDeck() : battle.getOpponentDeck();
        for (ICard card : friendlyDeck){
            if (card != this && card.getHealth() > 0 && card.getAbilities().contains(AbilityType.PHOTOSYNTHESIS)){
                card.setHealth(card.getHealth() + 5);
            }
        }
    }

    public void startTurn(Battle battle) {
        attackedThisTurn = false;

        tickBurn();
        tickHex();

        ICard defender = (this == battle.selectedPlayerCard)
                ? Battle.getCurrentOpponentCard()
                : battle.selectedPlayerCard;

        for (AbilityType type : new ArrayList<>(abilities)) {
            CardAbilities.onTurnStart(this, defender, this, type, battle);
        }
    }

    public void endTurn(Battle battle) {
        tickAttackReduction();
        ICard defender = (this == battle.selectedPlayerCard)
                ? Battle.getCurrentOpponentCard()
                : battle.selectedPlayerCard;

        for (AbilityType type : abilities) {
            CardAbilities.onTurnEnd(this, defender, this, type, battle);
        }
    }

    public boolean hasRevived() {
        return hasRevived;
    }

    public void setHasRevived(boolean revived) {
        this.hasRevived = revived;
    }

    public boolean hasUsedShadowDrain() {
        return hasUsedShadowDrain;
    }

    public void setHasUsedShadowDrain(boolean used) {
        this.hasUsedShadowDrain = used;
    }

    @Override
    public void tickBurn(){
        if (burnTurnsLeft > 0){
            System.out.println(name + " takes 3 burn damage (" + burnTurnsLeft + " turns left)");
            this.takeDamage(3);
            burnTurnsLeft--;
        }
    }

    @Override
    public boolean isStunned(){
        return stunned;
    }

    @Override
    public void setStunned(boolean stunned){
        this.stunned = stunned;
    }

    @Override
    public int getEffectiveAttack(){
        return Math.max(0, attack - temporaryAttackReduction);
    }

    @Override
    public void applyHex(int turns){
        this.hexTurnsLeft = turns;
    }

    @Override
    public boolean isHexed(){
        return hexTurnsLeft > 0;
    }

    @Override
    public void tickHex(){
        if (hexTurnsLeft > 0){
            hexTurnsLeft--;
        }
    }

    public void incrementRootsCounter(){
        turnsSinceLastRootsHeal++;
    }

    public boolean shouldTriggerRootsHeal(){
        if (turnsSinceLastRootsHeal >= 2){
            turnsSinceLastRootsHeal = 0;
            return true;
        }
        return false;
    }

    public void incrementSoothingBloomCounter(){
        soothingBloomCounter++;
    }

    public boolean shouldTriggerSoothingBlom(){
        if (soothingBloomCounter >= 2){
            soothingBloomCounter = 0;
            return true;
        }
        return false;
    }

    public boolean isLuringTarget(){
        return isLuringTarget;
    }

    public void setLuringTarget(boolean lure){
        this.isLuringTarget = lure;
    }

    public boolean hasDodged(){
        return hasDodged;
    }

    public void setHasDodged(boolean hasDodged){
        this.hasDodged = hasDodged;
    }

    public void setAttackedThisTurn(boolean attacked){
        this.attackedThisTurn = attacked;
    }

    public boolean hasAttackedThisTurn(){
        return attackedThisTurn;
    }

    public void applyShield(int amount){
        this.shieldAmount = Math.max(shieldAmount, amount);
    }

    public boolean hasShield(){
        return shieldAmount > 0;
    }

    public void consumeShield(){
        this.shieldAmount = 0;
    }

    public void addPendingAbility(AbilityType ability){
        pendingAbilities.add(ability);
    }

    public void applyPendingAbilities(){
        for (AbilityType ability : pendingAbilities){
            addAbility(ability);
        }
        pendingAbilities.clear();
    }

    public void markAsCopied(AbilityType ability){
        copiedAbilities.add(ability);
    }

    public boolean isCopiedAbility(AbilityType ability){
        return copiedAbilities.contains(ability);
    }

    public void setAttackedLastTurn(boolean value){
        this.attackedLastTurn = value;
    }

    public boolean didAttackLastTurn(){
        return attackedLastTurn;
    }

    @Override
    public void resetStatus(){
        this.hasRevived = false;
        this.hasUsedShadowDrain = false;
        this.burnTurnsLeft = 0;
        this.temporaryAttackReduction = 0;
        this.attackReductionTurnsLeft = 0;
        this.stunned = false;
        this.hasRebirthed = false;
        this.hexTurnsLeft = 0;
        this.turnsSinceLastRootsHeal = 0;
        this.soothingBloomCounter = 0;
        this.hasDodged = false;
        this.shieldAmount = 0;
    }



    @Override
    public String toString() {
        return name + " (ATK: " + attack + ", HP: " + health + ", " + rarity + ")";
    }
}