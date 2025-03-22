import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pack {
    private static final Random rand = new Random();

    private static final Card[] COMMON_CARDS = {
            new Card("Red", 2, 3, "Common", false, false),
            new Card("Blue", 3, 2, "Common", false, false),
            new Card("Green", 2, 4, "Common", false, false),
            new Card("Yellow", 3, 3, "Common", false, false)
    };

    private static final Card[] RARE_CARDS = {
            new Card("silver", 4, 4, "Rare", false, false),
            new Card("Gold", 5, 3, "Rare", false, false)
    };

    private static final Card[] LEGENDARY_CARDS = {
            new Card("Rainbow", 6, 6, "Legendary", false, false)
    };

    private static final int COMMON_CHANCE = 600; // 60%
    private static final int RARE_CHANCE = 990;   // 39%
    private static final int PACK_SIZE = 5;

    public static List<Card> openPack() {
        List<Card> pack = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            int chance = rand.nextInt(1000);

            if (chance < COMMON_CHANCE) {
                pack.add(createNewCard(COMMON_CARDS));
            } else if (chance < RARE_CHANCE) {
                pack.add(createNewCard(RARE_CARDS));
            } else {
                pack.add(createNewCard(LEGENDARY_CARDS));
            }
        }
        return pack;
    }

    private static Card createNewCard(Card[] cardPool) {
        Card template = cardPool[rand.nextInt(cardPool.length)];
        return template.createCopy();
    }
}
