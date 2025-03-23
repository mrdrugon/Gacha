import java.util.Objects;

public class Card {
    private String name;
    private int attack;
    private static int counter = 1;
    private int health;
    private int originalHealth;
    private String rarity;
    private boolean hasDoubleAttack;
    private boolean revive;
    private boolean revived;
    private int id;

    public Card(String name, int attack, int health, String rarity, boolean hasDoubleAttack, boolean revive){
        this.name = name;
        this.attack = attack;
        this.health = health;
        this.originalHealth = health;
        this.rarity = rarity;
        this.id = counter++;
        this.hasDoubleAttack = hasDoubleAttack;
        this.revive = revive;
        this.revived = false;
    }

    public  String getName(){
        return name;
    }

    public int getAttack(){
        return attack;
    }

    public int getHealth() {
        return health;
    }

    public int getId() {
        return id;
    }

    public String getRarity() {
        return rarity;
    }

    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void resetHealth() {
        this.health = originalHealth;
    }

    public int getOriginalHealth() {
        return originalHealth;
    }

    public boolean hasDoubleAttack() {
        return hasDoubleAttack;
    }

    public boolean hasRevive(){
        return revive;
    }

    public boolean hasRevived(){
        return revived;
    }

    public void setRevived(){
        this.revived = true;
    }


    public void revive(){
        if (revive && !revived){
            resetHealth();
            this.revived = true;
        }
    }

    public int getMaxHealth(){
        return originalHealth;
    }

    public void resetCardState(){
        this.health = originalHealth;
        this.revived = false;
    }

    public int compareTo(Card other){
        return Integer.compare(this.attack, other.attack);
    }

    public Card createCopy() {
        Card copy = new Card(this.name, this.attack, this.originalHealth, this.rarity, this.hasDoubleAttack, this.revive);
        copy.id = this.id;
        return copy;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return id == card.id;
    }

    public int hasCode(){
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name+" (ATK: "+attack+", HP: "+health+", "+rarity+")";
    }

}
