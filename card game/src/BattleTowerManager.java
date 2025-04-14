import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleTowerManager {
    private int currentLevel;
    public static int totalPointsEarned;
    private List<Pack> earnedPacks;

    private final Random random = new Random();

    public BattleTowerManager(){
        currentLevel = 1;
        totalPointsEarned = 0;
        earnedPacks = new ArrayList<>();
    }

    public int getCurrentLevel(){
        return currentLevel;
    }

    public static int getTotalPointsEarned(){
        return totalPointsEarned;
    }

    public List<Pack> getEarnedPacks(){
        return earnedPacks;
    }

    public void advanceLevel(){
        currentLevel++;
    }

    public void resetProgress(){
        currentLevel = 1;
        totalPointsEarned = 0;
        earnedPacks.clear();
    }

    public void exitTower(){
    }

    public int grantRewards(){
        int points = 10 + (currentLevel - 1) * 5;
        totalPointsEarned += points;

        if (currentLevel % 3 == 0){
            earnedPacks.add(new NormalPack());
        }

        if (currentLevel % 5 == 0){
            earnedPacks.add(new GemStonesPack());
        }
        return points;
    }

    public List<ICard> generateOpponentDeck() {
        List<ICard> deck = new ArrayList<>();

        int baseHealth = 20 + (currentLevel * 5);
        int baseAttack = 5 + (currentLevel * 2);
        int deckSize = Math.min(5 + currentLevel / 3, 10);

        for (int i = 0; i < deckSize; i++) {
            int health = baseHealth + random.nextInt(10);
            int attack = baseAttack + random.nextInt(3);

            String name = "Tower Lvl " + currentLevel + " Foe";
            String rarity = getRarityByLevel(currentLevel);
            List<AbilityType> abilities = new ArrayList<>();

            // Add abilities based on level scaling
            if (currentLevel >= 3 && random.nextFloat() < 0.6f) {
                abilities.add(getRandomTowerAbility());

                if (currentLevel >= 7 && random.nextFloat() < 0.3f) {
                    abilities.add(getRandomTowerAbility()); // Chance for second ability
                }
            }

            BasicCard card = new BasicCard(name, attack, health, rarity, abilities);
            deck.add(card);
        }

        return deck;
    }

    private String getRarityByLevel(int level) {
        if (level >= 15) return "Legendary";
        if (level >= 10) return "Epic";
        if (level >= 5) return "Rare";
        return "Common";
    }

    private AbilityType getRandomTowerAbility() {
        AbilityType[] options = AbilityType.values();
        return options[random.nextInt(options.length)];
    }
}
