public class ResilientDecorator extends CardDecorator{
    private static final double DAMAGE_REDUCTION_PERCENTAGE = 0.15;

    public ResilientDecorator(ICard card){
        super(card);
    }

    public int reduceDamage(int damage){
        return (int) Math.ceil(damage * (1 - DAMAGE_REDUCTION_PERCENTAGE));
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(reduceDamage(damage));
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Resilient]";
    }
}
