public class DoubleAttackDecorator extends CardDecorator {
    public DoubleAttackDecorator(ICard card) {
        super(card);
    }

    @Override
    public int getAttack() {
        // Returns double the base attack value.
        return decoratedCard.getAttack() * 2;
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Double Attack]";
    }
}
