public class BattleHardenedDecorator extends CardDecorator{
    public BattleHardenedDecorator(ICard card){
        super(card);
    }

    @Override
    public void takeDamage(int damage) {
        decoratedCard.takeDamage(damage);

        if (damage > 0) {
            decoratedCard.setAttack(decoratedCard.getAttack() + 1);
        }
    }
}
