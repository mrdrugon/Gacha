// File: BattleTowerManager.java
// (default package; alongside BasicCard.java, ICard.java, etc.)

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleTowerManager {
    private int currentLevel;
    private int totalPointsEarned;
    private int points;
    private int currentPoints = 0;  // Spendable points
    private List<Pack> earnedPacks;

    private final Random random = new Random();

    public BattleTowerManager() {
        currentLevel = 1;
        totalPointsEarned = 0;
        earnedPacks = new ArrayList<>();
    }

    public int getTotalPointsEarned() {
        return totalPointsEarned;
    }

    public int getPoints() {
        return points;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public List<Pack> getEarnedPacks() {
        return earnedPacks;
    }

    public void spendPoints(int amount) {
        if (currentPoints >= amount) {
            currentPoints -= amount;
        }
    }

    public void advanceLevel() {
        currentLevel++;
    }

    public void resetProgress() {
        currentLevel = 1;
        points = 0;
        earnedPacks.clear();
    }

    public int previewPoints() {
        return 10 + (currentLevel - 1) * 5;
    }

    public List<Pack> previewPacks() {
        List<Pack> preview = new ArrayList<>();

        // 25% chance to earn a pack this level
        if (random.nextFloat() < 0.25f) {
            preview.add(getRandomPack());
        }
        // 10% chance to earn a second bonus pack
        if (random.nextFloat() < 0.10f) {
            preview.add(getRandomPack());
        }
        return preview;
    }

    private Pack getRandomPack() {
        PackType[] types = PackType.values();
        PackType randomType = types[random.nextInt(types.length)];
        return PackFactory.createPack(randomType);
    }

    public void finalizeRewards() {
        int pts = previewPoints();
        points += pts;
        totalPointsEarned += pts;
        currentPoints += pts;
        earnedPacks.addAll(previewPacks());
    }

    public List<ICard> generateOpponentDeck() {
        List<ICard> deck = new ArrayList<>();

        int baseHealth = 20 + (currentLevel * 5);
        int baseAttack = 5 + (currentLevel * 2);
        int deckSize   = Math.min(5 + currentLevel / 3, 10);

        for (int i = 0; i < deckSize; i++) {
            int health = baseHealth + random.nextInt(10);
            int attack = baseAttack + random.nextInt(3);

            String name   = "Tower Lvl " + currentLevel + " Foe";
            String rarity = getRarityByLevel(currentLevel);
            List<AbilityType> abilities = new ArrayList<>();

            // Add abilities based on level scaling
            if (currentLevel >= 3 && random.nextFloat() < 0.6f) {
                abilities.add(getRandomTowerAbility());
                if (currentLevel >= 7 && random.nextFloat() < 0.3f) {
                    abilities.add(getRandomTowerAbility());
                }
            }

            // Supply a color based on rarity
            Color color = getColorByRarity(rarity);

            // Use the 6-arg constructor now
            BasicCard card = new BasicCard(name, attack, health, rarity, abilities, color);
            deck.add(card);
        }
        return deck;
    }

    private String getRarityByLevel(int level) {
        if (level >= 15) return "Legendary";
        if (level >= 10) return "Epic";
        if (level >=  5) return "Rare";
        return "Common";
    }

    private AbilityType getRandomTowerAbility() {
        AbilityType[] options = AbilityType.values();
        return options[random.nextInt(options.length)];
    }

    /** Map rarity string to a default color for tower cards */
    private Color getColorByRarity(String rarity) {
        return switch (rarity) {
            case "Legendary" -> new Color(255, 215, 0);  // gold
            case "Epic"      -> new Color(186, 85, 211); // medium orchid
            case "Rare"      -> new Color(65, 105, 225); // royal blue
            default          -> new Color(192, 192, 192); // silver
        };
    }
}
