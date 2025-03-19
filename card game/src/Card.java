public class Card {
    private String name;
    private int attack;
    private int health;
    private int originalHealth;
    private String rarity;
    private boolean hasDoubleAttack;
    private boolean revive;
    private boolean revived;

    public Card(String name, int attack, int health, String rarity, boolean hasDoubleAttack, boolean revive){
        this.name = name;
        this.attack = attack;
        this.health = health;
        this.originalHealth = health;
        this.rarity = rarity;
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
            setHealth(getMaxHealth());
            this.revived = true;
        }
    }

    public int getMaxHealth(){
        return originalHealth;
    }

    @Override
    public String toString() {
        return name+" (ATK: "+attack+", HP: "+health+", "+rarity+")";
    }

}