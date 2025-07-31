import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CardLoader {

    /**
     * Loads cards.csv with columns:
     *   name,attack,health,rarity,abilities,color
     * where abilities are semicolon-delimited and color is a hex string (e.g. "#FF8559").
     */
    public static List<ICard> loadFromResource(String resourcePath) throws IOException {
        InputStream in = CardLoader.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IOException("Resource not found on classpath: " + resourcePath);
        }

        List<ICard> cards = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] cols = line.split(",", -1);
                if (cols.length < 6) {
                    throw new IOException("Malformed line: " + line);
                }

                String name   = cols[0].trim();
                int    atk    = Integer.parseInt(cols[1].trim());
                int    hp     = Integer.parseInt(cols[2].trim());
                String rarity = cols[3].trim();

                List<AbilityType> abilities = new ArrayList<>();
                for (String tok : cols[4].split(";", -1)) {
                    tok = tok.trim();
                    if (!tok.isEmpty()) {
                        abilities.add(AbilityType.valueOf(tok));
                    }
                }

                Color color = Color.decode(cols[5].trim());

                cards.add(new BasicCard(name, atk, hp, rarity, abilities, color));
            }
        }
        return cards;
    }
}
