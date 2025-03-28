import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CardRenderer {
    private static BufferedImage metallicTexture;

    private static void loadTexture() {
        try {
            metallicTexture = ImageIO.read(new File("card game/cards/metallicTexture.png"));
        } catch (IOException e) {
            metallicTexture = null;
        }
    }

    public static void renderCard(Graphics2D g2d, ICard card, int width, int height){
        Color baseColor = getCardColor(card);
        RadialGradientPaint gradient = new RadialGradientPaint(
                new Point2D.Double(width / 2.0, height / 2.0),
                Math.max(width, height) / 2.0f,
                new float[]{0f, 1f},
                new Color[]{baseColor.brighter(), baseColor.darker()}
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        if (metallicTexture != null) {
            if (card.getName().equals("Gold")) {
                BufferedImage tintedTexture = applyTint(metallicTexture, new Color(255, 215, 0, 80));
                g2d.drawImage(tintedTexture, 0, 0, width, height, null);
            } else if (card.getName().equals("Silver")) {
                BufferedImage tintedTexture = applyTint(metallicTexture, new Color(100, 100, 100, 50));
                g2d.drawImage(tintedTexture, 0, 0, width, height, null);
            }
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String stats = "HP: " + card.getHealth() + " ATK: " + card.getAttack();
        g2d.drawString(stats, 10, height - 20);
        loadTexture();
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
