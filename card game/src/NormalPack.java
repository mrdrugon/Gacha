import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class NormalPack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 5;

    private static final List<ICard> COMMON_CARDS;
    static {
        try {
            COMMON_CARDS = CardLoader.loadFromResource("/cards.csv");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cards.csv", e);
        }
    }

    @Override
    public List<ICard> openPack(int count) {
        List<ICard> pack = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            pack.add(COMMON_CARDS.get(rand.nextInt(COMMON_CARDS.size())));
        }
        return pack;
    }

    @Override
    public List<ICard> openPack() {
        return openPack(PACK_SIZE);
    }
}
