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
    public int getOriginalHealth() {
        return decoratedCard.getOriginalHealth();
    }

    @Override
    public void setHealth(int health) {
        decoratedCard.setHealth(health);
    }

    public void setAttack(int attack){
        decoratedCard.setAttack(attack);
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

    @Override
    public String getRarity() {
        return decoratedCard.getRarity();
    }
}
