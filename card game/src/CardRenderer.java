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
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));

        // Draw Name (centered)
        FontMetrics fm = g2d.getFontMetrics();
        int nameX = (width - fm.stringWidth(card.getName())) / 2;
        g2d.drawString(card.getName(), nameX, 20);

        // Draw Stats (left-aligned)
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("HP: " + card.getHealth(), 10, 50);
        g2d.drawString("ATK: " + card.getAttack(), 10, 70);

        int abilityY = 100;
        for (AbilityType type : card.getAbilities()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, abilityY);
            abilityY += 20;

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            drawMultilineText(g2d, getAbilityDescription(type), 10, abilityY, width - 20);
            abilityY += 40; // Adjust for multiline spacing
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
            case "Red": return Color.RED;
            case "Azure Guardian": return Color.BLUE;
            case "Verdant Guardian": return Color.GREEN;
            case "Yellow Avenger": return Color.YELLOW;
            case "Citrine Monk": return Color.ORANGE;
            case "Pink": return Color.PINK;
            case "Purple": return new Color(157,0,255);
            case "Crimson Berserker": return new Color(178,34,34);
            case "Scarlet Phoenix": return new Color(250,0,63);
            case "Blue Ice Warden": return new Color(0,255,240);
            case "Nightmare Specter": return new Color(127,0,255);
            case "Volcanic Golem": return new Color(255,133,89);
            case "Frost Titan": return new Color(0,255,255);
            case "Flamingo": return new Color(252,142,172);

            case "Blazing Lion": return new Color(198,131,70);
            case "Lead": return new Color(76,87,108);
            case "Platinum": return new Color(217,217,217);
            case "Silver": return new Color(192, 192, 192);
            case "Sunset Golem": return new Color(255, 215, 0);

            case "Sapphire": return new Color(15,82,186);
            case "Ruby": return new Color(224,17,95);
            case "Zephyr Falcon": return new Color(80,200,120);
            case "Prism Dragon": return new Color(153,102,204);
            case "Rainbow": return Color.MAGENTA;
            default: return new Color(100, 100, 100);
        }
    }
}