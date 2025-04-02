import java.util.List;

public class BoosterDecorator extends CardDecorator{
    private boolean boostApplied = false;

    public BoosterDecorator(ICard card){
        super(card);
    }

    public void applyBoost(List<ICard> playerDeck){
        if (!boostApplied){
            for (ICard card : playerDeck){
                if (card != this){
                    int boostedAttack = (int)(card.getAttack() * 1.3);
                    card.setAttack(boostedAttack);
                }
            }
            boostApplied = true;
        }
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Booster]";
    }
}
