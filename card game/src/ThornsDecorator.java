public class ThornsDecorator extends CardDecorator{
    public ThornsDecorator(ICard card){
        super(card);
    }

    @Override
    public void takeDamage(int damage) {
        decoratedCard.takeDamage(damage);

        int reflectedDamage = (int) Math.ceil(damage * 0.25);
        ICard attacker = Battle.getCurrentOpponentCard();
        if (attacker != null){
            attacker.takeDamage(reflectedDamage);
        }
    }
}
