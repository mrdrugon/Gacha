import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pack {
    private static final Card[] COMMON_CARDS = {
            new Card("Red", 2, 3, "Common", false, false),
            new Card("Blue", 3, 2, "Common", false, false),
            new Card("Green", 2, 4, "Common", false, false),
            new Card("Yellow", 3, 3, "Common", false, false)
    };

    private static final Card[] RARE_CARDS = {
            new Card("silver", 4, 4, "Rare", false, false),
            new Card("Gold", 5, 3, "Rare", false, false)
    };

    private static final Card[] LEGENDARY_CARDS = {
            new Card("Rainbow", 6, 6, "Legendary", false, false)
    };

    public static List<Card> openPack(){
        List<Card> pack = new ArrayList<>();
        Random rand = new Random();

        int commonChance = 600; //60%
        int rareChance = 990; //39%

        for (int i = 0; i < 5; i++){
            int chance = rand.nextInt(1000);

            if (chance < commonChance){
                pack.add(COMMON_CARDS[rand.nextInt(COMMON_CARDS.length)]);
            } else if (chance < rareChance) {
                pack.add(RARE_CARDS[rand.nextInt(RARE_CARDS.length)]);
            } else {
                pack.add(LEGENDARY_CARDS[rand.nextInt(LEGENDARY_CARDS.length)]);
            }
        }
        return pack;
    }

    private static Card createNewCard(Card[] cardPool) {
        Card template = cardPool[new Random().nextInt(cardPool.length)];
        return new Card(template.getName(), template.getAttack(), template.getHealth(), template.getRarity(), template.hasDoubleAttack(), template.hasRevive());
    }
}
