public class FuryDecorator extends CardDecorator{
    public FuryDecorator(ICard card){
        super(card);
    }

    @Override
    public int getAttack() {
        if (decoratedCard.getHealth() > (decoratedCard.getOriginalHealth() / 2)){
            return decoratedCard.getAttack() + 3;
        }
        return decoratedCard.getAttack();
    }
}
