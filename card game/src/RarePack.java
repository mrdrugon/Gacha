import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class RarePack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 3;

    private static final ICard[] RARE_CARDS = {
            new BasicCard("Silver", 6, 6, "Rare", null),
            new BasicCard("Gold", 5, 6, "Rare", null),
            new BasicCard("Copper", 6, 5, "Rare", null),
            new BasicCard("Brass", 6, 7, "Rare", null),
            new BasicCard("Platinum", 7, 6, "Rare", null)

    };

    // Package-private constructor.
    RarePack() {}

    @Override
    public List<ICard> openPack() {
        List<ICard> pack = new ArrayList<>();
        for (int i = 0; i < PACK_SIZE; i++) {
            ICard template = RARE_CARDS[rand.nextInt(RARE_CARDS.length)];
            // Create a fresh instance.
            ICard card = new BasicCard(template.getName(), template.getAttack(), template.getHealth(), "Rare", template.getAbilities());
            pack.add(card);
        }
        return pack;
    }
}
