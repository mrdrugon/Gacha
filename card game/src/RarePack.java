import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class RarePack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 3;

    private static final ICard[] RARE_CARDS = {
            new BasicCard("Silver", 4, 4, "Rare"),
            new BasicCard("Gold", 5, 3, "Rare")
    };

    // Package-private constructor.
    RarePack() {}

    @Override
    public List<ICard> openPack() {
        List<ICard> pack = new ArrayList<>();
        for (int i = 0; i < PACK_SIZE; i++) {
            ICard template = RARE_CARDS[rand.nextInt(RARE_CARDS.length)];
            // Create a fresh instance.
            ICard card = new BasicCard(template.getName(), template.getAttack(), template.getHealth(), "Rare");
            // If the card is "Silver", decorate it with double attack ability.
            if (card.getName().equalsIgnoreCase("Silver")) {
                card = new DoubleAttackDecorator(card);
            }
            pack.add(card);
        }
        return pack;
    }
}
