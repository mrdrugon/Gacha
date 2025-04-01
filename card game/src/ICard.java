public interface ICard {
    String getName();
    int getAttack();
    int getHealth();
    void takeDamage(int damage);
    void resetHealth();
    int getId();
    int getOriginalHealth();
    void setHealth(int health);
}
