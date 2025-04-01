import java.util.Random;

public class SelfHarmDecorator extends CardDecorator{
    private static final Random RANDOM = new Random();

    public SelfHarmDecorator(ICard card){
        super(card);
    }

    @Override
    public int getAttack() {
        return decoratedCard.getAttack() * 2;
    }

    @Override
    public void takeDamage(int damage) {
        decoratedCard.takeDamage(damage);

        if (RANDOM.nextDouble() < 0.25){
            decoratedCard.setHealth(0);
        }
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Self-Harm]";
    }
}
