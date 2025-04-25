import java.util.ArrayList;
import java.util.List;

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
        return abilities;
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

    public void attack(ICard target, Battle battle) {
        boolean usedFlameFury = false;
        for (AbilityType type : abilities) {
            CardAbilities.onAttack(this, target, type, battle);
            if (type == AbilityType.FLAME_FURY && (double) this.health / this.originalHealth < 0.3) {
                usedFlameFury = true;
            }
        }

        // Only deal default damage if Flame Fury didn't already do it
        if (!usedFlameFury) {
            target.takeDamageWithAbilities(this.attack, battle);
        }
    }

    @Override
    public void takeDamageWithAbilities(int damage, Battle battle) {
        if (battle.isGlacialShieldActive(this)) {
            damage = (int) Math.ceil(damage * 0.5);
        }

        this.health = Math.max(0, this.health - damage);

        for (AbilityType type : abilities) {
            CardAbilities.onTakeDamage(this, damage, type, battle);
        }

        if (this.health <= 0) {
            ICard attacker = (this == battle.selectedPlayerCard)
                    ? Battle.getCurrentOpponentCard()
                    : battle.selectedPlayerCard;
            for (AbilityType type : abilities) {
                CardAbilities.onDeath(this, attacker, type, battle);
            }
        }

        // Fire Rebirth logic
        if (this.health <= 0 && !hasRebirthed && abilities.contains(AbilityType.FIRE_REBIRTH)) {
            this.health = Math.max(1, originalHealth / 2);
            this.hasRebirthed = true;
        }
    }

    public void startTurn(Battle battle) {
        for (AbilityType type : abilities) {
            CardAbilities.onTurnStart(this, type, battle);
        }
    }

    public void endTurn(Battle battle) {
        for (AbilityType type : abilities) {
            CardAbilities.onTurnEnd(this, type, battle);
        }
    }

    public boolean hasRevived() {
        return hasRevived;
    }

    public void setHasRevived(boolean revived) {
        this.hasRevived = revived;
    }

    @Override
    public String toString() {
        return name + " (ATK: " + attack + ", HP: " + health + ", " + rarity + ")";
    }
}