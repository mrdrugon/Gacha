public class PoisonAttackDecorator extends CardDecorator {
    private int poisonDamage;

    public PoisonAttackDecorator(ICard card, int poisonDamage){
        super(card);
        this.poisonDamage = poisonDamage;
    }

    @Override
    public String toString(){
        return  decoratedCard.toString() + " [Poison Attack]";
    }

    public void applyPoisonEffect(Battle battle, ICard target){
        battle.addPoisonedCard(target, poisonDamage);
    }
}
