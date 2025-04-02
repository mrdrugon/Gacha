public class BasicCard implements ICard {
    private static int counter = 1;
    private int id;
    private String name;
    private int attack;
    private int health;
    private int originalHealth;
    private String rarity;

    public BasicCard(String name, int attack, int health, String rarity) {
        this.name = name;
        this.attack = attack;
        this.health = health;
        this.originalHealth = health;
        this.rarity = rarity;
        this.id = counter++;
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
    public String toString() {
        return name + " (ATK: " + attack + ", HP: " + health + ", " + rarity + ")";
    }
}
