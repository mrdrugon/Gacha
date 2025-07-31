// File: CardRenderer.java
// (default package; place alongside AbilityDescriptionLoader.java)

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class CardRenderer {
    private static BufferedImage metallicTexture,
            goldTexture,
            silverTexture,
            copperTexture,
            leadTexture,
            platinumTexture;
    private static BufferedImage gemTexture,
            rubyTexture,
            sapphireTexture,
            emeraldTexture,
            amethystTexture;

    static {
        loadTexture();
    }

    private static void loadTexture() {
        try {
            metallicTexture = ImageIO.read(new File("card game/cards/metallicTexture.png"));
            goldTexture     = applyMetallicTint(metallicTexture, new Color(255,215,0,80));
            silverTexture   = applyMetallicTint(metallicTexture, new Color(100,100,100,50));
            copperTexture   = applyMetallicTint(metallicTexture, new Color(198,131,70,80));
            leadTexture     = applyMetallicTint(metallicTexture, new Color(76,87,108,80));
            platinumTexture = applyMetallicTint(metallicTexture, new Color(217,217,217,50));

            gemTexture      = ImageIO.read(new File("card game/cards/gemTexture.jpg"));
            sapphireTexture = applyGemTint(gemTexture, new Color(15,82,186,80));
            rubyTexture     = applyGemTint(gemTexture, new Color(224,17,95,80));
            emeraldTexture  = applyGemTint(gemTexture, new Color(80,200,120,80));
            amethystTexture = applyGemTint(gemTexture, new Color(153,102,204,80));
        } catch (IOException e) {
            metallicTexture = goldTexture = silverTexture =
                    copperTexture = leadTexture = platinumTexture = null;
            gemTexture = rubyTexture = sapphireTexture =
                    emeraldTexture = amethystTexture = null;
        }
    }

    public static void renderCard(Graphics2D g2d, ICard card, int width, int height) {
        // Background gradient
        Color base = card.getColor();
        RadialGradientPaint grad = new RadialGradientPaint(
                new Point2D.Double(width/2.0, height/2.0),
                Math.max(width, height)/2.0f,
                new float[]{0f,1f},
                new Color[]{base.brighter(), base.darker()}
        );
        g2d.setPaint(grad);
        g2d.fillRect(0, 0, width, height);

        // Metallic overlays
        if (metallicTexture != null) {
            String n = card.getName();
            if (n.equals("Gold"))       g2d.drawImage(goldTexture, 0,0,width,height,null);
            else if (n.equals("Silver")) g2d.drawImage(silverTexture,0,0,width,height,null);
            else if (n.equals("Copper")) g2d.drawImage(copperTexture,0,0,width,height,null);
            else if (n.equals("Lead"))   g2d.drawImage(leadTexture,0,0,width,height,null);
            else if (n.equals("Platinum")) g2d.drawImage(platinumTexture,0,0,width,height,null);
        }
        // Gem overlays
        if (gemTexture != null) {
            String n = card.getName();
            if (n.equals("Sapphire"))    g2d.drawImage(sapphireTexture, 0,0,width,height,null);
            else if (n.equals("Ruby"))    g2d.drawImage(rubyTexture,    0,0,width,height,null);
            else if (n.equals("Emerald")) g2d.drawImage(emeraldTexture, 0,0,width,height,null);
            else if (n.equals("Amethyst"))g2d.drawImage(amethystTexture,0,0,width,height,null);
        }

        // Draw card name
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(card.getName()))/2;
        g2d.drawString(card.getName(), x, 20);

        // Draw stats
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("HP: " + card.getHealth(), 10, 50);
        g2d.drawString("ATK: " + card.getAttack(), 10, 70);

        // Draw abilities
        List<AbilityType> abilities = card.getAbilities();
        int y = 100;
        boolean dual = abilities.contains(AbilityType.DUALITY);

        for (int i = 0; i < abilities.size(); i++) {
            AbilityType t = abilities.get(i);
            String label = (dual && i == 1) ? "COPIED ABILITY" : "ABILITY";
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(label, 10, y);
            y += 20;

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String desc = AbilityDescriptionLoader.getDescription(t);
            drawMultilineText(g2d, desc, 10, y, width - 20);
            y += 40;
        }
    }

    private static void drawMultilineText(Graphics2D g2d, String text,
                                          int x, int y, int maxWidth) {
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;

        for (String w : words) {
            if (fm.stringWidth(line + w) > maxWidth) {
                g2d.drawString(line.toString(), x, currentY);
                currentY += lineHeight;
                line = new StringBuilder(w + " ");
            } else {
                line.append(w).append(" ");
            }
        }
        g2d.drawString(line.toString(), x, currentY);
    }

    private static BufferedImage applyMetallicTint(BufferedImage img, Color tint) {
        BufferedImage tinted = new BufferedImage(
                img.getWidth(), img.getHeight(),
                BufferedImage.TRANSLUCENT
        );
        Graphics2D g = tinted.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.setColor(tint);
        g.setComposite(AlphaComposite.SrcOver);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.dispose();
        return tinted;
    }

    private static BufferedImage applyGemTint(BufferedImage img, Color tint) {
        return applyMetallicTint(img, tint);
    }
}
