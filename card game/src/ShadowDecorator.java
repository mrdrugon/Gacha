public class ShadowDecorator extends CardDecorator{
    public ShadowDecorator(ICard card){
        super(card);
    }

    public void applyShadowEffect(ICard opponent){
        int shadowDamage = (int) Math.ceil(opponent.getHealth() * 0.10);
        opponent.takeDamage(shadowDamage);
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Shadow]";
    }
}
