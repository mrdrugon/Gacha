import java.util.Random;

public class BarrelRollDecorator extends CardDecorator{
    private static final double DODGE_CHANCE = 0.30;
    private Random random;

    public BarrelRollDecorator(ICard card){
        super(card);
        this.random = new Random();
    }

    @Override
    public void takeDamage(int damage) {
        if (random.nextDouble() < DODGE_CHANCE) {
            System.out.println(decoratedCard.getName() + " dodged the attack!");
            // Attack is dodged; no damage taken.
        } else {
            super.takeDamage(damage); // Apply damage normally.
        }
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Barrel Roll]";
    }
}
