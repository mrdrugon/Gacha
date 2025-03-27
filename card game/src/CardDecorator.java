public abstract class CardDecorator implements ICard {
    protected ICard decoratedCard;

    public CardDecorator(ICard decoratedCard) {
        this.decoratedCard = decoratedCard;
    }

    @Override
    public String getName() {
        return decoratedCard.getName();
    }

    @Override
    public int getAttack() {
        return decoratedCard.getAttack();
    }

    @Override
    public int getHealth() {
        return decoratedCard.getHealth();
    }

    @Override
    public void takeDamage(int damage) {
        decoratedCard.takeDamage(damage);
    }

    @Override
    public void resetHealth() {
        decoratedCard.resetHealth();
    }

    @Override
    public int getId() {
        return decoratedCard.getId();
    }
}
