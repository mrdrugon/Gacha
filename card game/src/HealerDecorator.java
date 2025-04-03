public class HealerDecorator extends CardDecorator{
    public HealerDecorator(ICard card){
        super(card);
    }

    public void heal(int amount){
        decoratedCard.setHealth(Math.min(decoratedCard.getHealth() + amount, decoratedCard.getOriginalHealth()));
    }

    public void applyTurnEffect(){
        heal(1);
    }
}
