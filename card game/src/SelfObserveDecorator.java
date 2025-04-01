public class SelfObserveDecorator extends CardDecorator {
    public SelfObserveDecorator(ICard card) {
        super(card);
    }

    @Override
    public int getAttack() {
        int missingHealth = decoratedCard.getOriginalHealth() - decoratedCard.getHealth(); // Health lost
        int bonusAttack = missingHealth / 2; // Gain attack as health decreases
        return decoratedCard.getAttack() + bonusAttack;
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Self Observe]";
    }
}