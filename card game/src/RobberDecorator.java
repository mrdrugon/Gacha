public class RobberDecorator extends CardDecorator{
    public RobberDecorator(ICard card){
        super(card);
    }

    public void stealStats(ICard defeatedCard){
        if (defeatedCard.getHealth() <= 0){
            int stolenAttack = (int) Math.ceil(defeatedCard.getAttack() * 0.3);
            int stolenHealth = (int) Math.ceil(defeatedCard.getOriginalHealth() * 0.3);

            decoratedCard.setAttack(defeatedCard.getAttack() + stolenAttack);
            decoratedCard.setHealth(Math.min(decoratedCard.getHealth() + stolenHealth, defeatedCard.getOriginalHealth()));
        }
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Robber]";
    }
}
