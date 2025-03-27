import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class LegendaryPack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 1;

    private static final ICard[] LEGENDARY_CARDS = {
            new BasicCard("Rainbow", 6, 6, "Legendary")
    };

    // Package-private constructor.
    LegendaryPack() {}

    @Override
    public List<ICard> openPack() {
        List<ICard> pack = new ArrayList<>();
        for (int i = 0; i < PACK_SIZE; i++) {
            ICard template = LEGENDARY_CARDS[rand.nextInt(LEGENDARY_CARDS.length)];
            pack.add(new BasicCard(template.getName(), template.getAttack(), template.getHealth(), "Legendary"));
        }
        return pack;
    }
}
