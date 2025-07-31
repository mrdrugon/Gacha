// File: BasicCard.java
// (default package; place alongside ICard.java, AbilityType.java, etc.)

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasicCard implements ICard {
    private static int counter = 1;

    private final int id;
    private final String name;
    private int attack, health;
    private final int originalHealth;
    private final String rarity;
    private final Color  color;

    private final List<AbilityType> abilities        = new ArrayList<>();
    private final List<AbilityType> pendingAbilities = new ArrayList<>();
    private final Set<AbilityType>  copiedAbilities  = new HashSet<>();

    // Status fields
    private boolean stunned;
    private int     burnTurnsLeft;
    private int     hexTurnsLeft;
    private int     temporaryAttackReduction;
    private int     attackReductionTurnsLeft;
    private int     turnsSinceLastRootsHeal;
    private int     soothingBloomCounter;
    private boolean isLuringTarget;
    private boolean hasDodged;
    private boolean attackedThisTurn;
    private boolean attackedLastTurn;
    private int     shieldAmount;
    private boolean hasRevived;
    private boolean hasUsedShadowDrain;

    public BasicCard(String name,
                     int attack,
                     int health,
                     String rarity,
                     List<AbilityType> abilities,
                     Color color) {
        this.id = counter++;
        this.name = name;
        this.attack = attack;
        this.health = health;
        this.originalHealth = health;
        this.rarity = rarity;
        this.color = color;
        if (abilities != null) this.abilities.addAll(abilities);
    }

    // ─── ICard getters ────────────────────────────────────────────────────

    @Override public int getId()                { return id; }
    @Override public String getName()           { return name; }
    @Override public int getAttack()            { return attack; }
    @Override public int getHealth()            { return health; }
    @Override public int getOriginalHealth()    { return originalHealth; }
    @Override public String getRarity()         { return rarity; }
    @Override public List<AbilityType> getAbilities() {
        return new ArrayList<>(abilities);
    }
    @Override public Color getColor()           { return color; }

    // ─── ICard mutators ────────────────────────────────────────────────────

    @Override public void setHealth(int h)      { health = Math.min(h, originalHealth); }
    @Override public void setAttack(int a)      { attack = Math.max(0, a); }
    @Override public void takeDamage(int d)     { health = Math.max(0, health - d); }
    @Override public void resetHealth()         { health = originalHealth; }
    @Override public void addAbility(AbilityType a) { abilities.add(a); }

    @Override public void tickBurn() {
        if (burnTurnsLeft-- > 0) takeDamage(3);
    }
    @Override public void applyTemporaryAttackReduction(int amt, int turns) {
        temporaryAttackReduction = amt;
        attackReductionTurnsLeft = turns;
    }
    @Override public void tickAttackReduction() {
        if (attackReductionTurnsLeft-- > 0 && attackReductionTurnsLeft == 0)
            temporaryAttackReduction = 0;
    }

    @Override public boolean isStunned()        { return stunned; }
    @Override public void    setStunned(boolean s) { stunned = s; }

    @Override public void applyHex(int t)       { hexTurnsLeft = t; }
    @Override public boolean isHexed()          { return hexTurnsLeft > 0; }
    @Override public void    tickHex()          { if (hexTurnsLeft-- > 0) {} }

    @Override public int getEffectiveAttack()   { return Math.max(0, attack - temporaryAttackReduction); }
    @Override public void resetStatus() {
        stunned = false;
        burnTurnsLeft = hexTurnsLeft = 0;
        temporaryAttackReduction = attackReductionTurnsLeft = 0;
        turnsSinceLastRootsHeal = soothingBloomCounter = 0;
        isLuringTarget = hasDodged = attackedThisTurn = attackedLastTurn = false;
        shieldAmount = 0;
        hasRevived = hasUsedShadowDrain = false;
        pendingAbilities.clear();
        copiedAbilities.clear();
    }

    // ─── Shield methods ────────────────────────────────────────────────────

    @Override public void applyShield(int amt)  { shieldAmount = Math.max(shieldAmount, amt); }
    @Override public boolean hasShield()        { return shieldAmount > 0; }
    @Override public void consumeShield()       { shieldAmount = 0; }

    // ─── Combat hooks ───────────────────────────────────────────────────────

    @Override
    public void takeDamageWithAbilities(int damage, Battle battle) {
        if (shieldAmount > 0) {
            int a = Math.min(damage, shieldAmount);
            damage -= a; shieldAmount -= a;
        }
        if (abilities.contains(AbilityType.DEEP_SLIP) && !hasDodged) {
            hasDodged = true; return;
        }
        if (battle.isGlacialShieldActive(this)) {
            damage = (int)Math.ceil(damage * 0.5);
        }
        health = Math.max(0, health - damage);

        ICard attacker = battle.isPlayerCard(this)
                ? battle.getCurrentOpponentCard()
                : battle.getSelectedPlayerCard();
        for (AbilityType t : abilities) {
            CardAbilities.onDefend(attacker, this, t, battle);
        }

        List<ICard> team = battle.isPlayerCard(this)
                ? battle.getPlayerDeck()
                : battle.getOpponentDeck();
        for (ICard ally : team) {
            if (ally != this
                    && ally.getHealth() > 0
                    && ally.getAbilities().contains(AbilityType.PHOTOSYNTHESIS)) {
                ally.setHealth(ally.getHealth() + 5);
            }
        }
    }

    @Override
    public void attack(ICard target, Battle battle) {
        if (stunned) return;
        attackedThisTurn = true;
        boolean usedFF = false;
        for (AbilityType t : new ArrayList<>(abilities)) {
            CardAbilities.onAttack(this, target, target, t, battle);
            if (t == AbilityType.FLAME_FURY
                    && ((double)health / originalHealth) < 0.3) {
                usedFF = true;
            }
        }
        if (!usedFF) {
            target.takeDamageWithAbilities(getEffectiveAttack(), battle);
        }
        if (target.getHealth() <= 0
                && abilities.contains(AbilityType.TIDE_TURN)) {
            setHealth(getHealth() + 10);
        }
    }

    @Override public boolean hasAttackedThisTurn()       { return attackedThisTurn; }
    @Override public void    setAttackedThisTurn(boolean a) { attackedThisTurn = a; }
    @Override public void    setAttackedLastTurn(boolean a) { attackedLastTurn = a; }
    @Override public boolean didAttackLastTurn()         { return attackedLastTurn; }

    // ─── Pending & Copied ─────────────────────────────────────────────────

    @Override public void addPendingAbility(AbilityType a) { pendingAbilities.add(a); }
    @Override public void applyPendingAbilities()         { abilities.addAll(pendingAbilities); pendingAbilities.clear(); }
    @Override public void markAsCopied(AbilityType a)     { copiedAbilities.add(a); }
    @Override public boolean isCopiedAbility(AbilityType a){ return copiedAbilities.contains(a); }

    // ─── Roots & Bloom counters ────────────────────────────────────────────

    public void incrementRootsCounter() {
        turnsSinceLastRootsHeal++;
    }
    public boolean shouldTriggerRootsHeal() {
        if (turnsSinceLastRootsHeal >= 2) {
            turnsSinceLastRootsHeal = 0;
            return true;
        }
        return false;
    }

    public void incrementSoothingBloomCounter() {
        soothingBloomCounter++;
    }
    public boolean shouldTriggerSoothingBlom() {
        if (soothingBloomCounter >= 2) {
            soothingBloomCounter = 0;
            return true;
        }
        return false;
    }

    // ─── Luring target ─────────────────────────────────────────────────────

    public void setLuringTarget(boolean lure) { isLuringTarget = lure; }
    public boolean isLuringTarget()           { return isLuringTarget; }

    // ─── Shadow Drain & Revive ─────────────────────────────────────────────

    public boolean hasUsedShadowDrain()               { return hasUsedShadowDrain; }
    public void    setHasUsedShadowDrain(boolean u)  { hasUsedShadowDrain = u; }

    public boolean hasRevived()                      { return hasRevived; }
    public void    setHasRevived(boolean r)          { hasRevived = r; }

    // ─── Turn Hooks ─────────────────────────────────────────────────────────

    public void startTurn(Battle battle) {
        attackedThisTurn = false;
        tickBurn();
        tickHex();
        ICard opp = (this == battle.getSelectedPlayerCard())
                ? battle.getCurrentOpponentCard()
                : battle.getSelectedPlayerCard();
        for (AbilityType t : new ArrayList<>(abilities)) {
            CardAbilities.onTurnStart(this, opp, this, t, battle);
        }
    }

    public void endTurn(Battle battle) {
        tickAttackReduction();
        ICard opp = (this == battle.getSelectedPlayerCard())
                ? battle.getCurrentOpponentCard()
                : battle.getSelectedPlayerCard();
        for (AbilityType t : abilities) {
            CardAbilities.onTurnEnd(this, opp, this, t, battle);
        }
    }

    @Override
    public String toString() {
        return name + " (ATK: " + attack + ", HP: " + health + ", " + rarity + ")";
    }
}
