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

    static {
        loadTexture(); // Load textures once
    }

    private static void loadTexture() {
        try {
            metallicTexture = ImageIO.read(new File("card game/cards/metallicTexture.png"));
            goldTexture = applyTint(metallicTexture, new Color(255, 215, 0, 80));
            silverTexture = applyTint(metallicTexture, new Color(100, 100, 100, 50));
        } catch (IOException e) {
            metallicTexture = null;
            goldTexture = null;
            silverTexture = null;
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
            case "Silver": return new Color(192, 192, 192);
            case "Gold": return new Color(255, 215, 0);
            case "Rainbow": return Color.MAGENTA;
            default: return new Color(100, 100, 100);
        }
    }
}