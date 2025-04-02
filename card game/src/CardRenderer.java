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
    private static BufferedImage brassTexture;
    private static BufferedImage platinumTexture;

    static {
        loadTexture(); // Load textures once
    }

    private static void loadTexture() {
        try {
            metallicTexture = ImageIO.read(new File("card game/cards/metallicTexture.png"));
            goldTexture = applyTint(metallicTexture, new Color(255, 215, 0, 80));
            silverTexture = applyTint(metallicTexture, new Color(100, 100, 100, 50));
            copperTexture = applyTint(metallicTexture, new Color(198,131,70, 80));
            brassTexture = applyTint(metallicTexture, new Color(181,166,66, 80));
            platinumTexture = applyTint(metallicTexture, new Color(217, 217, 217, 50));
        } catch (IOException e) {
            metallicTexture = null;
            goldTexture = null;
            silverTexture = null;
            copperTexture = null;
            brassTexture = null;
            platinumTexture = null;
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
            } else if (card.getName().equals("Brass") && brassTexture != null) {
                g2d.drawImage(brassTexture, 0, 0, width, height, null);
            } else if (card.getName().equals("Platinum") && platinumTexture != null) {
                g2d.drawImage(platinumTexture, 0, 0, width, height, null);
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


        //Draw Double Attack
        if (card instanceof DoubleAttackDecorator){
            DoubleAttackDecorator doubleAttack = (DoubleAttackDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String doubleAttackText = ("If card survived an attack, it attacks again");
            drawMultilineText(g2d, doubleAttackText, 10, 120, width - 20);

        }

        //Draw Revive
        if (card instanceof ReviveDecorator){
            ReviveDecorator Revive = (ReviveDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("After death the card revives with full health");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);

        }

        //Draw Self Observe
        if (card instanceof SelfObserveDecorator){
            SelfObserveDecorator Self = (SelfObserveDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("The less health you have, the more damage you do");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //poison attack
        if (card instanceof PoisonAttackDecorator){
            PoisonAttackDecorator Poison = (PoisonAttackDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("The opponent losses health at the start of every turn");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }


        //regeneration
        if (card instanceof RegenerationDecorator){
            RegenerationDecorator Reg = (RegenerationDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("After taking damage, the card recovers 15% of it's health");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //self-harm
        if (card instanceof SelfHarmDecorator){
            SelfHarmDecorator Harm = (SelfHarmDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("Attacks two times the damage, but has 25% of destroying it self ");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //triple attack
        if (card instanceof TripleSliceDecorator){
            TripleSliceDecorator attack = (TripleSliceDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("Attack three times per turn. Each subsequent attack will deal less damage than the previous one.");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //explosion
        if (card instanceof TripleSliceDecorator){
            TripleSliceDecorator attack = (TripleSliceDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("Deals 3× damage but will cause the card to be stunned for one turn");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //Shadow
        if (card instanceof ShadowDecorator){
            ShadowDecorator attack = (ShadowDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("Reduce the opponent’s health by 10%");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //resilient
        if (card instanceof ResilientDecorator){
            ResilientDecorator attack = (ResilientDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("Reduce incoming damage by 15%");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //barrel-roll
        if (card instanceof BarrelRollDecorator){
            BarrelRollDecorator attack = (BarrelRollDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("30% chance to dodge incoming attacks");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //Damage Boost
        if (card instanceof DamageBoostDecorator){
            DamageBoostDecorator attack = (DamageBoostDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("Card attack twice, with the second attack dealing 20% more damage");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //Fist hit dodge
        if (card instanceof FirstHitDogeDecorator){
            FirstHitDogeDecorator attack = (FirstHitDogeDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("Card always dodges the first attack");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //Booster
        if (card instanceof BoosterDecorator){
            BoosterDecorator attack = (BoosterDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("All cards in deck gain 30% attack boost, on use");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //Robber
        if (card instanceof RobberDecorator){
            RobberDecorator attack = (RobberDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("After defeating a card, steal 30% of their attack and health.");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
        }

        //Increaser
        if (card instanceof IncreaserDecorator){
            IncreaserDecorator attack = (IncreaserDecorator) card;
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ABILITY", 10, 100);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String ReviveText = ("Doubles the card's attack if its attack is lower than the opponent's");
            drawMultilineText(g2d, ReviveText, 10, 120, width - 20);
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

    private static BufferedImage applyTint(BufferedImage image, Color tint) {
        BufferedImage tinted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TRANSLUCENT);
        Graphics2D g2d = tinted.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.setColor(tint);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();
        return tinted;
    }

    private static Color getCardColor(ICard card) {
        switch (card.getName()) {
            case "Red": return Color.RED;
            case "Blue": return Color.BLUE;
            case "Green": return Color.GREEN;
            case "Yellow": return Color.YELLOW;
            case "Orange": return Color.ORANGE;
            case "Pink": return Color.PINK;
            case "Purple": return new Color(157,0,255);
            case "Crimson": return new Color(178,34,34);
            case "Rose": return new Color(250,0,63);
            case "Aqua": return new Color(0,255,240);
            case "Violet": return new Color(127,0,255);
            case "Coral": return new Color(255,133,89);
            case "Cyan": return new Color(0,255,255);
            case "Flamingo": return new Color(252,142,172);

            case "Copper": return new Color(198,131,70);
            case "Brass": return new Color(181,166,66);
            case "Platinum": return new Color(217,217,217);
            case "Silver": return new Color(192, 192, 192);
            case "Gold": return new Color(255, 215, 0);

            case "Sapphire": return new Color(15,82,186);
            case "Ruby": return new Color(224,17,95);
            case "Emerald": return new Color(80,200,120);
            case "Amethyst": return new Color(153,102,204);
            case "Rainbow": return Color.MAGENTA;
            default: return new Color(100, 100, 100);
        }
    }
}