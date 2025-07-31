// File: AbilityDescriptionLoader.java
// (default package; place alongside CardRenderer.java)

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

public class AbilityDescriptionLoader {
    private static final Map<AbilityType, String> DESCRIPTIONS = loadDescriptions();

    private static Map<AbilityType, String> loadDescriptions() {
        Map<AbilityType, String> map = new EnumMap<>(AbilityType.class);
        try (InputStream in = AbilityDescriptionLoader.class
                .getResourceAsStream("/ability_descriptions.csv")) {
            if (in == null) {
                throw new IOException("Could not find /ability_descriptions.csv on classpath");
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    // skip comments and blank lines
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    // split into two parts: ability key and description
                    String[] parts = line.split(",", 2);
                    if (parts.length < 2) continue;
                    AbilityType type = AbilityType.valueOf(parts[0].trim());
                    String desc = parts[1].trim();
                    map.put(type, desc);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load ability_descriptions.csv", e);
        }
        return map;
    }

    /** Returns the description for the given ability, or empty string if none. */
    public static String getDescription(AbilityType ability) {
        return DESCRIPTIONS.getOrDefault(ability, "");
    }
}
