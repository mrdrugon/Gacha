// File: CardLoader.java
// (no package statement; place alongside ICard.java, BasicCard.java, etc.)

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CardLoader {

    /**
     * Load cards.csv from the classpath (resources folder).
     * @param resourcePath e.g. "/cards.csv"
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
                // skip blank lines or comments
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] cols = line.split(",", -1);
                if (cols.length < 5) {
                    throw new IOException("Malformed line in cards.csv: " + line);
                }

                String name   = cols[0].trim();
                int    attack = Integer.parseInt(cols[1].trim());
                int    health = Integer.parseInt(cols[2].trim());
                String rarity = cols[3].trim();

                // abilities are semicolon‐delimited
                List<AbilityType> abilities = new ArrayList<>();
                for (String tok : cols[4].split(";", -1)) {
                    tok = tok.trim();
                    if (!tok.isEmpty()) {
                        abilities.add(AbilityType.valueOf(tok));
                    }
                }

                cards.add(new BasicCard(name, attack, health, rarity, abilities));
            }
        }
        return cards;
    }
}
