public class FirstHitDogeDecorator extends CardDecorator{
    private boolean firstHitDodged = false;

    public FirstHitDogeDecorator(ICard card){
        super(card);
    }

    @Override
    public void takeDamage(int damage) {
        if (!firstHitDodged){
            firstHitDodged = true;
            return;
        }
        super.takeDamage(damage);
    }

    @Override
    public void resetHealth() {
        super.resetHealth();
        firstHitDodged = false;
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [First Hit Doge]";
    }
}
