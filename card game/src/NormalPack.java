import java.util.*;
import java.util.function.Function;

final class NormalPack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 5;

    private static final BasicCard[] COMMON_CARDS = {
            new BasicCard("Yellow Avenger", 8, 30, "Common", Collections.singletonList(AbilityType.LIGHTNING_CHARGE)),
            new BasicCard("Blue Ice Warden", 5, 40, "Common", Collections.singletonList(AbilityType.FROSTBITE)),
            new BasicCard("Verdant Guardian", 6, 50, "Common", Collections.singletonList(AbilityType.THORN_ARMOR)),
            new BasicCard("Volcanic Golem", 10, 75, "Common", Collections.singletonList(AbilityType.LAVA_SURGE)),
            new BasicCard("Frost Titan", 9, 70, "Common", Collections.singletonList(AbilityType.GLACIAL_SHIELD))
    };


    private static final BasicCard[] UNCOMMON_CARDS = {
            new BasicCard("Citrine Monk", 7, 40, "Uncommon", Collections.singletonList(AbilityType.RADIANT_BALANCE)),
            new BasicCard("Blazing Lion", 9, 45, "Uncommon", Collections.singletonList(AbilityType.BURNING_CLAWS)),
            new BasicCard("Solar Paladin", 10, 60, "Uncommon", Collections.singletonList(AbilityType.RADIANT_LIGHT)),
            new BasicCard("Zephyr Falcon", 8, 50, "Uncommon", Collections.singletonList(AbilityType.GALE_FORCE))
    };

    private static final BasicCard[] RARE_CARDS = {
            new BasicCard("Azure Guardian", 8, 60, "Rare", Collections.singletonList(AbilityType.OCEANS_GRASP)),
            new BasicCard("Sunset Golem", 5, 80, "Rare", Collections.singletonList(AbilityType.MOLTEN_CORE)),
            new BasicCard("Tempest Serpent", 10, 65, "Rare", Collections.singletonList(AbilityType.CYCLONE_FURY)),
            new BasicCard("Amber Phoenix", 9, 50, "Rare", Collections.singletonList(AbilityType.ASHEN_WINGS))
    };

    private static final BasicCard[] EPIC_CARDS = {
            new BasicCard("Crimson Berserker", 12, 40, "Epic", Collections.singletonList(AbilityType.FLAME_FURY)),
            new BasicCard("Scarlet Phoenix", 10, 50, "Epic", Collections.singletonList(AbilityType.FIRE_REBIRTH))
    };

    private static final BasicCard[] LEGENDARY_CARDS = {
            new BasicCard("Prism Dragon", 15, 70, "Legendary", Collections.singletonList(AbilityType.RAINBOW_PULSE)),
            new BasicCard("Rainbow Phoenix", 20, 90, "Legendary", Collections.singletonList(AbilityType.ETERNAL_FLAME))
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