import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class NormalPack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 5;

    private static final ICard[] COMMON_CARDS = {
            new BasicCard("Red", 2, 3, "Common"),
            new BasicCard("Blue", 3, 2, "Common"),
            new BasicCard("Green", 2, 4, "Common"),
            new BasicCard("Yellow", 3, 3, "Common")
    };

    // Package-private constructor: only classes in this package can instantiate.
    NormalPack() {}

    @Override
    public List<ICard> openPack() {
        List<ICard> pack = new ArrayList<>();
        for (int i = 0; i < PACK_SIZE; i++) {
            ICard template = COMMON_CARDS[rand.nextInt(COMMON_CARDS.length)];
            // Create a fresh instance. Optionally, apply decorators here.
            pack.add(new BasicCard(template.getName(), template.getAttack(), template.getHealth(), "Common"));
        }
        return pack;
    }
}
