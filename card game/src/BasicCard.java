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

    public void attack(ICard target, Battle battle) {
        for (AbilityType type : abilities) {
            CardAbilities.onAttack(this, target, type, battle);
        }
        target.takeDamageWithAbilities(this.attack, battle);
    }

    @Override
    public void takeDamageWithAbilities(int damage, Battle battle) {
        this.health = Math.max(0, this.health - damage);
        for (AbilityType type : abilities) {
            CardAbilities.onTakeDamage(this, damage, type, battle);
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

    @Override
    public String toString() {
        return name + " (ATK: " + attack + ", HP: " + health + ", " + rarity + ")";
    }
}
