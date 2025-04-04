import java.util.*;
import java.util.function.Function;

final class NormalPack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 5;

    private static final BasicCard[] COMMON_CARDS = {
            new BasicCard("Red", 1, 6, "Common"),
            new BasicCard("Blue", 8, 8, "Common"),
            new BasicCard("Green", 4, 2, "Common"),
            new BasicCard("Yellow", 8, 5, "Common"),
            new BasicCard("Purple", 3, 1, "Common"),
            new BasicCard("Orange", 5, 6, "Common"),

            new BasicCard("Crimson", 8, 6, "Common"),
            new BasicCard("Rose", 2, 4, "Common"),
            new BasicCard("Aqua", 5, 5, "Common"),
            new BasicCard("Coral", 1, 2, "Common")
    };

    private static final BasicCard[] RARE_CARDS = {
            new BasicCard("Silver", 4, 4, "Rare"),
            new BasicCard("Gold", 5, 9, "Rare"),
            new BasicCard("Copper", 9, 4, "Rare"),
            new BasicCard("Brass", 9, 8, "Rare"),
            new BasicCard("Platinum", 6, 6, "Rare")
    };

    private static final BasicCard[] LEGENDARY_CARDS = {
            new BasicCard("Sapphire", 15, 19, "Legendary"),
            new BasicCard("Ruby", 8, 15, "Legendary"),
            new BasicCard("Emerald", 18, 17, "Legendary"),
            new BasicCard("Amethyst", 17, 17, "Legendary"),
            new BasicCard("Rainbow", 12, 17, "Legendary")
    };

    private static final int COMMON_CHANCE = 900;  // 90%
    private static final int RARE_CHANCE = 990;

    private static final Map<String, Function<ICard, ICard>> ABILITY_MAP = new HashMap<>();

    static {
    }

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

            // Check if the card has an assigned ability
            if (ABILITY_MAP.containsKey(card.getName())) {
                card = ABILITY_MAP.get(card.getName()).apply(card);
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