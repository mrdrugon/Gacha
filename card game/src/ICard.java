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
    boolean isBurning();
    void applyBurn(int turns);
    void tickBurn();
    void applyTemporaryAttackReduction(int amount, int turns);
    void tickAttackReduction();
    boolean isStunned();
    void setStunned(boolean stunned);
    void applyHex(int turns);
    boolean isHexed();
    void tickHex();
    int getEffectiveAttack();
    void resetStatus();
    void attack(ICard target, Battle battle);
    boolean hasAttackedThisTurn();
    boolean isRevealed();
}
