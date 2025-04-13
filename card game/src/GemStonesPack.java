import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

final class GemStonesPack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 3;

    private static final BasicCard[] COMMON_CARDS = {
            new BasicCard("Emerald Sorceress", 8, 30, "Common", null),
            new BasicCard("Amethyst Phantom", 8, 30, "Common", null),
            new BasicCard("Obsidian Wraith", 8, 30, "Common", null)
    };


    private static final BasicCard[] UNCOMMON_CARDS = {
            new BasicCard("Sapphire Mage", 8, 30, "Uncommon", null),
            new BasicCard("Diamond Titan", 8, 30, "Uncommon", null),
            new BasicCard("Onyx Oracle", 8, 30, "Uncommon", null)
    };

    private static final BasicCard[] RARE_CARDS = {
    };

    private static final BasicCard[] EPIC_CARDS = {
    };

    private static final BasicCard[] LEGENDARY_CARDS = {
            new BasicCard("Ruby Dragon", 8, 30, "Legendary", null)
    };

    private static final int COMMON_CHANCE = 60;
    private static final int UNCOMMON_CHANCE = 80;
    private static final int RARE_CHANCE = 90;
    private static final int EPIC_CHANCE = 99;

    @Override
    public List<ICard> openPack() {
        List<ICard> pack = new ArrayList<>();
        for (int i = 0; i < PACK_SIZE; i++) {
            int chance = rand.nextInt(100);

            ICard card;
            if (chance <= COMMON_CHANCE) {
                card = createNewCard(COMMON_CARDS);
            } else if (chance < UNCOMMON_CHANCE) {
                card = createNewCard(UNCOMMON_CARDS);
            } else if (chance < RARE_CHANCE){
                card = createNewCard(RARE_CARDS);
            } else if (chance < EPIC_CHANCE){
                card = createNewCard(EPIC_CARDS);
            } else {
                card = createNewCard(LEGENDARY_CARDS);
            }

            pack.add(card);
        }
        return pack;
    }

    private static ICard createNewCard(BasicCard[] cardPool) {
        BasicCard template = cardPool[rand.nextInt(cardPool.length)];
        return new BasicCard(template.getName(), template.getAttack(), template.getOriginalHealth(), template.getRarity(), template.getAbilities());
    }
}
