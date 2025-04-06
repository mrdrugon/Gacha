import java.util.*;
import java.util.function.Function;

final class NormalPack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 5;

    private static final BasicCard[] COMMON_CARDS = {
            new BasicCard("Yellow Avenger", 8, 30, "Common", Collections.singletonList(AbilityType.LIGHTNING_CHARGE)),
            new BasicCard("Blue Ice Warden", 5, 40, "Common", Collections.singletonList(AbilityType.FROSTBITE)),
            new BasicCard("Verdant Guardian", 6, 50, "Common", null),
            new BasicCard("Volcanic Golem", 10, 75, "Common", null),
            new BasicCard("Nightmare Specter", 8, 40, "Common", null),
            new BasicCard("Frost Titan", 9, 70, "Common", null),
    };


    private static final BasicCard[] UNCOMMON_CARDS = {
            new BasicCard("Citrine Monk", 7, 40, "Uncommon", null),
            new BasicCard("Blazing Lion", 9, 45, "Uncommon", null),
            new BasicCard("Solar Paladin", 10, 60, "Uncommon", null),
            new BasicCard("Zephyr Falcon", 8, 50, "Uncommon", null)
    };

    private static final BasicCard[] RARE_CARDS = {
            new BasicCard("Azure Guardian", 8, 60, "Rare", null),
            new BasicCard("Sunset Golem", 5, 80, "Rare", null),
            new BasicCard("Tempest Serpent", 10, 65, "Rare", null),
            new BasicCard("Amber Phoenix", 9, 50, "Rare", null)
    };

    private static final BasicCard[] EPIC_CARDS = {
            new BasicCard("Crimson Berserker", 12, 40, "Epic", null),
            new BasicCard("Scarlet Phoenix", 10, 50, "Epic", null)
    };

    private static final BasicCard[] LEGENDARY_CARDS = {
            new BasicCard("Prism Dragon", 15, 70, "Legendary", null),
            new BasicCard("Rainbow Phoenix", 20, 90, "Legendary", null)
    };

    private static final int COMMON_CHANCE = 60;
    private static final int UNCOMMON_CHANCE = 80;
    private static final int RARE_CHANCE = 90;
    private static final int EPIC_CHANCE = 99;


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