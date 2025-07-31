import java.awt.Color;
import java.util.List;

public interface ICard {
    int getId();
    String getName();
    int getAttack();
    int getHealth();
    int getOriginalHealth();
    String getRarity();
    List<AbilityType> getAbilities();

    void setHealth(int health);
    void setAttack(int attack);
    void takeDamage(int damage);
    void takeDamageWithAbilities(int damage, Battle battle);
    void resetHealth();
    void addAbility(AbilityType ability);

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
    void setAttackedThisTurn(boolean attacked);
    void setAttackedLastTurn(boolean attacked);
    boolean didAttackLastTurn();

    void applyShield(int amount);
    boolean hasShield();
    void consumeShield();

    void addPendingAbility(AbilityType ability);
    void applyPendingAbilities();
    void markAsCopied(AbilityType ability);
    boolean isCopiedAbility(AbilityType ability);

    Color getColor();
}
