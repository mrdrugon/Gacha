public class LifeStealDecorator extends CardDecorator{
    public LifeStealDecorator(ICard card){
        super(card);
    }

    @Override
    public void takeDamage(int damage) {
        decoratedCard.takeDamage(damage);
    }

    public void heal(int amount){
        decoratedCard.setHealth(Math.min(decoratedCard.getHealth() + amount, decoratedCard.getOriginalHealth()));
    }

    public void attack(ICard target){
        int damageDealt = decoratedCard.getAttack();
        target.takeDamage(damageDealt);

        int healAmount = (int) Math.ceil(damageDealt * 0.2);
        heal(healAmount);
    }
}
