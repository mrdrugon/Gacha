public class ExplosionDecorator extends CardDecorator{
    public ExplosionDecorator(ICard card){
        super(card);
    }

    public void attack(ICard opponent){
        int explosionDamage = decoratedCard.getAttack() *3;
        opponent.takeDamage(explosionDamage);
    }

    @Override
    public String toString() {
        return decoratedCard.toString() + " [Explosion]";
    }
}
