public class DamageBoostDecorator extends CardDecorator{

    public DamageBoostDecorator(ICard card){
        super(card);
    }

    @Override
    public int getAttack() {
        // Returns double the base attack value.
        return decoratedCard.getAttack() * 2;
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Damage Boost]";
    }
}
