import java.util.List;

public interface ICard {
    String getName();
    int getAttack();
    int getHealth();
    void takeDamage(int damage);
    void takeDamageWithAbilities(int damage, Battle battle);
    void resetHealth();
    int getId();
    int getOriginalHealth();
    void setHealth(int health);
    void setAttack(int attack);
    String getRarity();
    List<AbilityType> getAbilities();
    void addAbility(AbilityType ability);
}
