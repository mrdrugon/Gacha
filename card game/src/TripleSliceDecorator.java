public class TripleSliceDecorator extends CardDecorator{
    public TripleSliceDecorator(ICard card){
        super(card);
    }

    public void attack(ICard opponent){
        int baseAttack = decoratedCard.getAttack();

        //First hit 100%
        opponent.takeDamage(baseAttack);

        //Second hit 75%
        opponent.takeDamage((int) (baseAttack *0.75));

        //third hit 50%
        opponent.takeDamage((int) (baseAttack * 0.5));
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Triple Slice]";
    }
}
