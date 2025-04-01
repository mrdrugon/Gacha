public class RegenerationDecorator extends CardDecorator {
    public RegenerationDecorator(ICard card) {
        super(card);
    }

    @Override
    public void takeDamage(int damage) {
        decoratedCard.takeDamage(damage);

        // Heal 15% of the card's max health
        int maxHealth = decoratedCard.getHealth() + damage;  // Estimating max health
        int healAmount = (int) (maxHealth * 0.15);

        int newHealth = Math.min(decoratedCard.getHealth() + healAmount, maxHealth);
        decoratedCard.setHealth(newHealth);
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Regeneration]";
    }
}
