public class IncreaserDecorator extends CardDecorator{
    public IncreaserDecorator(ICard card){
        super(card);
    }

    @Override
    public int getAttack() {
        ICard opponent = Battle.getCurrentOpponentCard();
        if (opponent != null && decoratedCard.getAttack() < opponent.getAttack()){
            return decoratedCard.getAttack() * 2;
        }
        return decoratedCard.getAttack();
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Increaser]";
    }
}
