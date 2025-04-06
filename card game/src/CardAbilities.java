public class CardAbilities {
    public static void onAttack(ICard attacker, ICard target, AbilityType type, Battle battle) {
        switch (type) {
            case FROSTBITE:
                applyFrostbite(target);
                break;
            // Add more attack-based abilities here
        }
    }

    public static void onTakeDamage(ICard card, int damage, AbilityType type, Battle battle) {
        switch (type) {
            case LIGHTNING_CHARGE:
                applyLightningCharge(card);
                break;
            // Add more damage-received abilities here
        }
    }

    public static void onTurnStart(ICard card, AbilityType type, Battle battle) {
        // Example: Add "Regenerate" here
    }

    public static void onTurnEnd(ICard card, AbilityType type, Battle battle) {
        // Example: Add "Decay" here
    }

    private static void applyFrostbite(ICard target) {
        target.setAttack(Math.max(0, target.getAttack() - 2));
    }

    private static void applyLightningCharge(ICard card) {
        card.setAttack(Math.max(0, card.getAttack() + 5));
    }

}
