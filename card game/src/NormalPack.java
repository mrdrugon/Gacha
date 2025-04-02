import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class NormalPack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 5;

    private static final BasicCard[] COMMON_CARDS = {
            new BasicCard("Red", 2, 3, "Common"),
            new BasicCard("Blue", 3, 2, "Common"),
            new BasicCard("Green", 2, 4, "Common"),
            new BasicCard("Yellow", 3, 3, "Common"),
            new BasicCard("Purple", 4, 2, "Common"),
            new BasicCard("Pink", 2, 2, "Common"),
            new BasicCard("Orange", 4, 4, "Common"),

            new BasicCard("Crimson", 3, 4, "Common"),
            new BasicCard("Rose", 4, 3, "Common"),
            new BasicCard("Aqua", 5, 5, "Common"),
            new BasicCard("Violet", 2, 5, "Common"),
            new BasicCard("Coral", 4, 5, "Common"),
            new BasicCard("Cyan", 5, 4, "Common"),
            new BasicCard("Flamingo", 5, 3, "Common")
    };

    private static final BasicCard[] RARE_CARDS = {
            new BasicCard("Silver", 6, 6, "Rare"),
            new BasicCard("Gold", 5, 6, "Rare"),
            new BasicCard("Copper", 6, 5, "Rare"),
            new BasicCard("Brass", 6, 7, "Rare"),
            new BasicCard("Platinum", 7, 6, "Rare")
    };

    private static final BasicCard[] LEGENDARY_CARDS = {
            new BasicCard("Sapphire", 8, 9, "Legendary"),
            new BasicCard("Ruby", 9, 8, "Legendary"),
            new BasicCard("Emerald", 9, 7, "Legendary"),
            new BasicCard("Amethyst", 7, 9, "Legendary"),

            new BasicCard("Rainbow", 9, 9, "Legendary")
    };

    private static final int COMMON_CHANCE = 700;  // 70%
    private static final int RARE_CHANCE = 990;

    public List<ICard> openPack() {
        List<ICard> pack = new ArrayList<>();

        for (int i = 0; i < PACK_SIZE; i++) {
            int chance = rand.nextInt(1000);

            ICard card;
            if (chance < COMMON_CHANCE) {
                card = createNewCard(COMMON_CARDS);
            } else if (chance < RARE_CHANCE) {
                card = createNewCard(RARE_CARDS);
            } else {
                card = createNewCard(LEGENDARY_CARDS);
            }

            pack.add(card);
        }
        return pack;
    }

    private static ICard createNewCard(BasicCard[] cardPool) {
        BasicCard template = cardPool[rand.nextInt(cardPool.length)];
        return new BasicCard(template.getName(), template.getAttack(), template.getOriginalHealth(), template.getRarity());
    }
}