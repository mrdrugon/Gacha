public class ReviveDecorator extends CardDecorator {
    public boolean revived;

    public ReviveDecorator(ICard card) {
        super(card);
        this.revived = false;
    }

    public void revive() {
        if (!revived && decoratedCard.getHealth() <= 0) {
            decoratedCard.resetHealth();
            revived = true;
        }
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Revive]";
    }
}
