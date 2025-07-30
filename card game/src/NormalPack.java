import java.util.*;

final class NormalPack implements Pack {
    private static final Random rand = new Random();
    private static final int PACK_SIZE = 5;

    private static final BasicCard[] COMMON_CARDS = {
            new BasicCard("Yellow Avenger", 8, 30, "Common", Collections.singletonList(AbilityType.LIGHTNING_CHARGE)),
            new BasicCard("Blue Ice Warden", 5, 40, "Common", Collections.singletonList(AbilityType.FROSTBITE)),
            new BasicCard("Verdant Guardian", 6, 50, "Common", Collections.singletonList(AbilityType.THORN_ARMOR)),
            new BasicCard("Volcanic Golem", 10, 30, "Common", Collections.singletonList(AbilityType.LAVA_SURGE)),
            new BasicCard("Golden Warrior", 8, 30, "Common", Collections.singletonList(AbilityType.SUNSTRIKE)),
            new BasicCard("Silver Sentinel", 8, 30, "Common", Collections.singletonList(AbilityType.SILVER_SHIELD)),
            new BasicCard("Luminous Priest", 8, 30, "Common", Collections.singletonList(AbilityType.SOLAR_FLARE)),
            new BasicCard("Shadow Phantom", 8, 30, "Common", Collections.singletonList(AbilityType.SHADOW_SLASH)),
            new BasicCard("Ivory Knight", 8, 30, "Common", Collections.singletonList(AbilityType.WHITE_STRIKE)),
            new BasicCard("Nightmare Specter", 8, 30, "Common", Collections.singletonList(AbilityType.PHANTOM_STRIKE)),
            new BasicCard("Emerald Sorceress", 8, 30, "Common", Collections.singletonList(AbilityType.GEM_SHIELD)),
            new BasicCard("Amethyst Phantom", 8, 30, "Common", Collections.singletonList(AbilityType.SHADOW_DRAIN)),
            new BasicCard("Obsidian Wraith", 8, 30, "Common", Collections.singletonList(AbilityType.VOLCANIC_CORE)),
    };

    private static final BasicCard[] UNCOMMON_CARDS = {
            new BasicCard("Citrine Monk", 7, 40, "Uncommon", Collections.singletonList(AbilityType.RADIANT_BALANCE)),
            new BasicCard("Blazing Lion", 9, 45, "Uncommon", Collections.singletonList(AbilityType.BURNING_CLAWS)),
            new BasicCard("Solar Paladin", 10, 60, "Uncommon", Collections.singletonList(AbilityType.RADIANT_LIGHT)),
            new BasicCard("Zephyr Falcon", 8, 50, "Uncommon", Collections.singletonList(AbilityType.GALE_FORCE)),
            new BasicCard("Frost Titan", 9, 70, "Common", Collections.singletonList(AbilityType.GLACIAL_SHIELD)),
            new BasicCard("Steel Knight", 8, 30, "Uncommon", Collections.singletonList(AbilityType.METAL_SLUSH)),
            new BasicCard("Grayscale Trickster", 8, 30, "Uncommon", Collections.singletonList(AbilityType.DUALITY)),
            new BasicCard("Void Mage", 8, 30, "Uncommon", Collections.singletonList(AbilityType.MIND_WARP)),
            new BasicCard("Tidal Leviathan", 8, 30, "Uncommon", Collections.singletonList(AbilityType.TIDE_TURN)),
            new BasicCard("Sapphire Mage", 8, 30, "Uncommon", Collections.singletonList(AbilityType.WATER_SHIELD)),
            new BasicCard("Diamond Titan", 8, 30, "Uncommon", Collections.singletonList(AbilityType.SHIMMERING_RETALIATION)),
            new BasicCard("Onyx Oracle", 8, 30, "Uncommon", Collections.singletonList(AbilityType.DARK_VISION)),
            new BasicCard("Twilight Witch", 8, 30, "Uncommon", Collections.singletonList(AbilityType.HEX)),
            new BasicCard("Wishblossom", 8, 30, "Uncommon", Collections.singletonList(AbilityType.SOOTHING_BLOOM))

    };

    private static final BasicCard[] RARE_CARDS = {
            new BasicCard("Azure Guardian", 8, 60, "Rare", Collections.singletonList(AbilityType.OCEANS_GRASP)),
            new BasicCard("Sunset Golem", 5, 80, "Rare", Collections.singletonList(AbilityType.MOLTEN_CORE)),
            new BasicCard("Tempest Serpent", 10, 65, "Rare", Collections.singletonList(AbilityType.CYCLONE_FURY)),
            new BasicCard("Amber Phoenix", 9, 50, "Rare", Collections.singletonList(AbilityType.ASHEN_WINGS)),
            new BasicCard("Forest Spirit", 8, 30, "Rare", Collections.singletonList(AbilityType.PHOTOSYNTHESIS))
    };

    private static final BasicCard[] EPIC_CARDS = {
            new BasicCard("Crimson Berserker", 12, 40, "Epic", Collections.singletonList(AbilityType.FLAME_FURY)),
            new BasicCard("Scarlet Phoenix", 10, 50, "Epic", Collections.singletonList(AbilityType.FIRE_REBIRTH)),
            new BasicCard("Pearl Warden", 8, 30, "Epic", Collections.singletonList(AbilityType.PROTECTIVE_GLEAM)),
            new BasicCard("Meadow Warden", 8, 30, "Epic", Collections.singletonList(AbilityType.SPREADING_ROOTS)),
            new BasicCard("Lavender Spirit", 8, 30, "Epic", Collections.singletonList(AbilityType.SOOTHING_BLOOM))
    };

    private static final BasicCard[] LEGENDARY_CARDS = {
            new BasicCard("Prism Dragon", 15, 70, "Legendary", Collections.singletonList(AbilityType.RAINBOW_PULSE)),
            new BasicCard("Rainbow Phoenix", 20, 90, "Legendary", Collections.singletonList(AbilityType.ETERNAL_FLAME)),
            new BasicCard("Ruby Dragon", 8, 30, "Legendary", Collections.singletonList(AbilityType.FIRE_REBIRTH)),
            new BasicCard("Blooming Dryad", 8, 30, "Legendary", Collections.singletonList(AbilityType.HEALING_SPROUT)),
            new BasicCard("Forest Beast", 8, 30, "Legendary", Collections.singletonList(AbilityType.WILD_ROAR))
    };

    private static final int COMMON_CHANCE = 60;
    private static final int UNCOMMON_CHANCE = 80;
    private static final int RARE_CHANCE = 90;
    private static final int EPIC_CHANCE = 99;

    @Override
    public List<ICard> openPack(int count) {
        List<ICard> pack = new ArrayList<>();

        for (int i = 0; i < count; i++) {
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

    @Override
    public List<ICard> openPack() {
        return openPack(PACK_SIZE);  // default 5 cards
    }

    private static ICard createNewCard(BasicCard[] cardPool) {
        BasicCard template = cardPool[rand.nextInt(cardPool.length)];
        return new BasicCard(template.getName(), template.getAttack(), template.getOriginalHealth(), template.getRarity(), template.getAbilities());
    }
}