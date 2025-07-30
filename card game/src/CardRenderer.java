import java.util.List;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CardRenderer {
    private static BufferedImage metallicTexture;
    private static BufferedImage goldTexture;
    private static BufferedImage silverTexture;
    private static BufferedImage copperTexture;
    private static BufferedImage leadTexture;
    private static BufferedImage platinumTexture;

    private static BufferedImage gemTexture;
    private static BufferedImage rubyTexture;
    private static BufferedImage sapphireTexture;
    private static BufferedImage emeraldTexture;
    private static BufferedImage amethystTexture;

    static {
        loadTexture(); // Load textures once
    }

    private static void loadTexture() {
        try {
            metallicTexture = ImageIO.read(new File("card game/cards/metallicTexture.png"));
            goldTexture = applyMetallicTint(metallicTexture, new Color(255, 215, 0, 80));
            silverTexture = applyMetallicTint(metallicTexture, new Color(100, 100, 100, 50));
            copperTexture = applyMetallicTint(metallicTexture, new Color(198,131,70, 80));
            leadTexture = applyMetallicTint(metallicTexture, new Color(76,87,108, 80));
            platinumTexture = applyMetallicTint(metallicTexture, new Color(217, 217, 217, 50));

            gemTexture = ImageIO.read(new File("card game/cards/gemTexture.jpg"));
            sapphireTexture = applyGemTint(gemTexture, new Color(15,82,186, 80));
            rubyTexture = applyGemTint(gemTexture, new Color(224,17,95, 80));
            emeraldTexture = applyGemTint(gemTexture, new Color(80,200,120, 80));
            amethystTexture = applyGemTint(gemTexture, new Color(153,102,204, 80));
        } catch (IOException e) {
            metallicTexture = null;
            goldTexture = null;
            silverTexture = null;
            copperTexture = null;
            leadTexture = null;
            platinumTexture = null;

            gemTexture = null;
            sapphireTexture = null;
            rubyTexture = null;
            emeraldTexture = null;
            amethystTexture = null;
        }
    }

    public static void renderCard(Graphics2D g2d, ICard card, int width, int height) {
        // Background gradient
        Color baseColor = getCardColor(card);
        RadialGradientPaint gradient = new RadialGradientPaint(
                new Point2D.Double(width / 2.0, height / 2.0),
                Math.max(width, height) / 2.0f,
                new float[]{0f, 1f},
                new Color[]{baseColor.brighter(), baseColor.darker()}
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Apply metallic texture
        if (metallicTexture != null) {
            if (card.getName().equals("Gold") && goldTexture != null) {
                g2d.drawImage(goldTexture, 0, 0, width, height, null);
            } else if (card.getName().equals("Silver") && silverTexture != null) {
                g2d.drawImage(silverTexture, 0, 0, width, height, null);
            } else if (card.getName().equals("Copper") && copperTexture != null) {
                g2d.drawImage(copperTexture, 0, 0, width, height, null);
            } else if (card.getName().equals("Lead") && leadTexture != null) {
                g2d.drawImage(leadTexture, 0, 0, width, height, null);
            } else if (card.getName().equals("Platinum") && platinumTexture != null) {
                g2d.drawImage(platinumTexture, 0, 0, width, height, null);
            }
        }

        if (gemTexture != null) {
            if (card.getName().equals("Sapphire") && sapphireTexture != null) {
                g2d.drawImage(sapphireTexture, 0, 0, width, height, null);
            } else if (card.getName().equals("Ruby") && rubyTexture != null) {
                g2d.drawImage(rubyTexture, 0, 0, width, height, null);
            } else if (card.getName().equals("Emerald") && emeraldTexture != null) {
                g2d.drawImage(emeraldTexture, 0, 0, width, height, null);
            } else if (card.getName().equals("Amethyst") && amethystTexture != null) {
                g2d.drawImage(amethystTexture, 0, 0, width, height, null);
            }
        }

        // Set text properties

        if (card.getName() == "Nightmare Specter" || card.getName() == "Obsidian Wraith" || card.getName() == "Void Mage" || card.getName() == "Kraken's Tentacle" || card.getName() == "Shadow Phantom" || card.getName() == "Onyx Oracle" || card.getName() == "Azure Guardian" || card.getName() == "Forest Spirit"){
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
        }

        // Draw Name (centered)
        FontMetrics fm = g2d.getFontMetrics();
        int nameX = (width - fm.stringWidth(card.getName())) / 2;
        g2d.drawString(card.getName(), nameX, 20);

        // Draw Stats (left-aligned)
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("HP: " + card.getHealth(), 10, 50);
        g2d.drawString("ATK: " + card.getAttack(), 10, 70);

        List<AbilityType> abilities = card.getAbilities();
        int abilityY = 100;
        boolean isDuality = abilities.contains(AbilityType.DUALITY);

        for (int i = 0; i < abilities.size(); i++) {
            AbilityType type = abilities.get(i);

            String label;
            if (isDuality && i == 0 && type == AbilityType.DUALITY) {
                label = "ABILITY";
            } else if (isDuality && i == 1) {
                label = "COPIED ABILITY";
            } else {
                label = "ABILITY";
            }

            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(label, 10, abilityY);
            abilityY += 20;

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            drawMultilineText(g2d, getAbilityDescription(type), 10, abilityY, width - 20);
            abilityY += 40;
        }
    }

    private static String getAbilityDescription(AbilityType type) {
        switch (type) {
            case FROSTBITE:
                return "Reduces enemy card’s attack by 2.";
            case LIGHTNING_CHARGE:
                return "Gains +5 ATK after taking damage.";
            case THORN_ARMOR:
                return "Reflects 25% of the damage back to the attacker.";
            case FLAME_FURY:
                return "Deals double damage when below 30% health.";
            case FIRE_REBIRTH:
                return "Revives with 50% health after being destroyed once.";
            case OCEANS_GRASP:
                return "Freezes an enemy card for 1 turn (prevents it from attacking).";
            case BURNING_CLAWS:
                return "Deals an additional 3 fire damage for 3 turns after attacking.";
            case RADIANT_BALANCE:
                return "Heals itself for 5 health and deals 5 damage at the end of each turn.";
            case MOLTEN_CORE:
                return "On death, explodes and deals 15 damage to the card that killed it.";
            case RAINBOW_PULSE:
                return "Deals 5 damage to all enemies and heals all allies for 5.";
            case LAVA_SURGE:
                return "Deals 10 damage to all enemy cards, but the golem takes 5 damage each turn after use.";
            case ASHEN_WINGS:
                return "When this card is destroyed, it deals 10 damage to all enemy cards.";
            case GLACIAL_SHIELD:
                return "Reduces incoming damage by 50%.";
            case RADIANT_LIGHT:
                return "Heals all friendly cards for 10 health every 3 turns.";
            case CYCLONE_FURY:
                return "Deals 10 damage to a random enemy every 2 turns.";
            case GALE_FORCE:
                return "Reduces the enemy’s attack by 4 for 2 turns after attacking.";
            case ETERNAL_FLAME:
                return "When destroyed, it revives with full health and gains +5 attack.";
            case FIRESTORM:
                return "Deals 10 damage to all enemy cards.";
            case WATER_SHIELD:
                return "Reduces incoming damage by 5 and heals for 5 every turn.";
            case GEM_SHIELD:
                return "Reduces incoming damage by 20%.";
            case SHADOW_DRAIN:
                return "Steals 5 attack from the opponent card it hits (once per match).";
            case SHIMMERING_RETALIATION:
                return "When hit, deals 5 damage back and gains +2 defense.";
            case VOLCANIC_CORE:
                return "Deals 10 damage to both itself and the enemy on attack.";
            case DARK_VISION:
                return "Reveals a random card in the opponent's hand and weakens it by 3 attack.";
            case SUNSTRIKE:
                return "Deals extra damage if the opponent has a higher attack than this card.";
            case RUST_RESISTANCE:
                return "Immune to debuffs.";
            case METAL_SLUSH:
                return "Has a 50% chance to attack twice in a turn.";
            case SILVER_SHIELD:
                return "Increases defense of a friendly card by 10.";
            case SOLAR_FLARE:
                return "Applies fire to the enemy for 2 turns (deals 3 damage per turn).";
            case SHADOW_SLASH:
                return "Has 30% chance to reduce an enemy card's attack.";
            case WHITE_STRIKE:
                return "Deals triple damage if no other friendly cards are alive.";
            case DUALITY:
                return "Can copy the ability of any dead card.";
            case MIND_WARP:
                return "Reduces a random enemy card’s attack by 3.";
            case PHANTOM_STRIKE:
                return "Has a 20% chance to dodge and attack.";
            case PHOTOSYNTHESIS:
                return "Heals 5 health every time a friendly card gets hit.";
            case HEX:
                return "Disables an enemy’s ability when it attack this card.";
            case SPREADING_ROOTS:
                return "Heals all friendly cards by 3 every 2 turns.";
            case SOOTHING_BLOOM:
                return "Heals 8 health to a random friendly card every 2 turns.";
            case SEED_SCATTER:
                return "On death, summons a 5-health “Dandelion Puff” with 1 attack.";
            case HEALING_SPROUT:
                return "Heals all friendly cards for 15 health.";
            case WILD_ROAR:
                return "Deals 5 damage to all enemy cards and heals itself by 5.";
            case TIDE_TURN:
                return "If this card kills a card, it gains +10 health.";
            case LURING_SONG:
                return "Forces an enemy card to attack it next turn.";
            case DEEP_SLIP:
                return "Dodges the first attack made against it.";
            case OCEANS_PATIENCE:
                return "Gains +2 attack and +5 health every turn it doesn’t attack.";
            case ENTANGLE:
                return "Reduces enemy’s attack by 4.";
            case PROTECTIVE_GLEAM:
                return "On summon, shields the lowest-health friendly card for 15 damage.";
            // Add others
            default:
                return "";
        }
    }

    private static void drawMultilineText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;

        for (String word : words) {
            if (fm.stringWidth(line + word) > maxWidth) {
                g2d.drawString(line.toString(), x, currentY);
                currentY += lineHeight;
                line = new StringBuilder(word + " ");
            } else {
                line.append(word).append(" ");
            }
        }
        g2d.drawString(line.toString(), x, currentY);
    }

    private static BufferedImage applyMetallicTint(BufferedImage image, Color tint) {
        BufferedImage tinted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TRANSLUCENT);
        Graphics2D g2d = tinted.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setColor(tint);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();
        return tinted;
    }

    private static BufferedImage applyGemTint(BufferedImage image, Color tint) {
        BufferedImage tinted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TRANSLUCENT);
        Graphics2D g2d = tinted.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setColor(tint);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();
        return tinted;
    }

    public static Color getCardColor(ICard card) {
        switch (card.getName()) {
            case "Azure Guardian": return Color.BLUE;
            case "Verdant Guardian": return Color.GREEN;
            case "Yellow Avenger": return Color.YELLOW;
            case "Citrine Monk": return Color.ORANGE;
            case "Crimson Berserker": return new Color(178,34,34);
            case "Scarlet Phoenix": return new Color(250,0,63);
            case "Blue Ice Warden": return new Color(0,255,240);
            case "Volcanic Golem": return new Color(255,133,89);
            case "Frost Titan": return new Color(0,255,255);
            case "Flamingo": return new Color(252,142,172);
            case "Golden Warrior": return new Color(255, 215, 0);
            case "Silver Sentinel": return new Color(100, 100, 100);
            case "Luminous Priest": return new Color(253, 253, 150);
            case "Shadow Phantom": return new Color(63,16,79);
            case "Ivory Knight": return new Color(255, 255, 240);
            case "Nightmare Specter": return new Color(0,0,46);
            case "Coral Siren": return new Color(255, 127, 80);
            case "Emerald Sorceress": return new Color(80, 200, 120);
            case "Amethyst Phantom": return new Color(153,102,204);
            case "Obsidian Wraith": return new Color(61,53,75);
            case "Dandelion Puff": return new Color(240,225,48);
            case "Blazing Lion": return new Color(241,61,54);
            case "Solar Paladin": return new Color(238,176,58);
            case "Zephyr Falcon": return new Color(176,211,234);
            case "Steel Knight": return new Color(115, 133, 149);
            case "Void Mage": return new Color(45,0,67);
            case "Tidal Leviathan": return new Color(64, 96, 124);
            case "Sapphire Mage": return new Color(43,61,171);
            case "Diamond Titan": return new Color(203,227,240);
            case "Onyx Oracle": return new Color(53,56,57);
            case "Twilight Witch": return new Color(78, 81, 139);
            case "Wishblossom": return new Color(255, 183, 197);
            case "Sunset Golem": return new Color(251, 158, 58);
            case "Tempest Serpent": return new Color(122,142,167);
            case "Amber Phoenix": return new Color(255, 191, 0);
            case "Copper Golem": return new Color(198,131,70);
            case "Sea Turtle Sentinel": return new Color(73,97,77);
            case "Kraken's Tentacle": return new Color(0,22,40);
            case "Forest Spirit": return new Color(40, 54, 24);
            case "Pearl Warden": return new Color(234,224,200);
            case "Meadow Warden": return new Color(121,203,145);
            case "Lavender Spirit": return new Color(211,211,255);
            case "Prism Dragon": return new Color(136, 9, 181);
            case "Rainbow Phoenix": return new Color(19, 125, 224);
            case "Ruby Dragon": return new Color(155,17,30);
            case "Blooming Dryad": return new Color(254,161,179);
            case "Forest Beast": return new Color(96, 108, 56);
            default: return new Color(100, 100, 100);
        }
    }
}