import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardAbilities {
    public static void onAttack(ICard attacker, ICard target, AbilityType type, Battle battle) {
        switch (type) {
            case FROSTBITE:
                applyFrostbite(target);
                break;
            case FLAME_FURY:
                applyFlameFury(attacker, target);
                break;
            case OCEANS_GRASP:
                applyOceansGrasp(target, battle);
                break;
            case BURNING_CLAWS:
                applyBurningClaws(target, battle);
                break;
            case LAVA_SURGE:
                applyLavaSurge(attacker, battle);
                break;
            case GALE_FORCE:
                applyGaleForce(target, battle);
                break;
            // Add more attack-based abilities here
        }
    }

    public static void onTakeDamage(ICard card, int damage, AbilityType type, Battle battle) {
        switch (type) {
            case LIGHTNING_CHARGE:
                applyLightningCharge(card);
                break;
            case THORN_ARMOR:
                applyThornArmor(card, damage, battle);
                break;
        }
    }

    public static void onTurnStart(ICard card, AbilityType type, Battle battle) {
        switch (type){
            case RAINBOW_PULSE:
                applyRainbowPulse(card,battle);
                break;
            case LAVA_SURGE:
                handleLavaSurgeBacklash(card,battle);
                break;
            case GLACIAL_SHIELD:
                applyGlacialShield(card, battle);
                break;
            case RADIANT_LIGHT:
                applyRadiantLight(card, battle);
                break;
            case CYCLONE_FURY:
                applyCycloneFury(card, battle);
                break;
        }
    }

    public static void onTurnEnd(ICard card, AbilityType type, Battle battle) {
        switch (type){
            case RADIANT_BALANCE:
                applyRadiantBalance(card, battle);
                break;
        }
    }

    public static void onDeath(ICard card, ICard killer, AbilityType type, Battle battle) {
        switch (type) {
            case MOLTEN_CORE:
                applyMoltenCore(card, killer, battle);
                break;
            case ASHEN_WINGS:
                applyAshenWings(card, battle);
                break;
            case ETERNAL_FLAME:
                applyEternalFlame(card);
                break;
            // Add more death-triggered abilities here
        }
    }

    private static void applyMoltenCore(ICard self, ICard killer, Battle battle) {
        if (killer != null && killer.getHealth() > 0) {
            killer.takeDamageWithAbilities(15, battle);
        }
    }

    private static void applyFrostbite(ICard target) {
        target.setAttack(Math.max(0, target.getAttack() - 2));
    }

    private static void applyLightningCharge(ICard card) {
        card.setAttack(Math.max(0, card.getAttack() + 5));
    }

    private static void applyThornArmor(ICard card, int damageTaken, Battle battle) {
        ICard attacker = Battle.getCurrentOpponentCard(); // This gives us the card that just attacked
        if (attacker != null && attacker.getHealth() > 0) {
            int reflectDamage = Math.max(1, damageTaken / 4); // Always reflect at least 1
            attacker.takeDamageWithAbilities(reflectDamage, battle);
        }
    }

    private static void applyFlameFury(ICard attacker, ICard target) {
        if (attacker.getHealth() / 0.3 < attacker.getHealth()) {
            int damage = attacker.getAttack() * 2;
            target.takeDamageWithAbilities(damage, null); // `null` for Battle if you don't need effects
        }
    }

    private static void applyOceansGrasp(ICard target, Battle battle) {
        battle.freezeCard(target, 2);
    }

    private static void applyBurningClaws(ICard target, Battle battle) {
        battle.applyBurn(target, 3);
    }

    private static void applyRadiantBalance(ICard card, Battle battle) {
        if (card.getHealth() <= 0) return; // Don't apply if dead

        card.setHealth(card.getHealth() + 5); // Heal self

        ICard target = (card == battle.selectedPlayerCard)
                ? Battle.getCurrentOpponentCard()
                : battle.selectedPlayerCard;

        if (target != null && target.getHealth() > 0) {
            target.takeDamageWithAbilities(5, battle);
        }
    }

    private static void applyRainbowPulse(ICard card, Battle battle) {
        boolean isPlayerCard = battle.isPlayerCard(card);
        List<ICard> allies = isPlayerCard ? battle.getPlayerDeck() : battle.getOpponentDeck();
        List<ICard> enemies = isPlayerCard ? battle.getOpponentDeck() : battle.getPlayerDeck();

        for (ICard ally : allies) {
            if (ally.getHealth() > 0) {
                ally.setHealth(ally.getHealth() + 5);
            }
        }

        for (ICard enemy : enemies) {
            if (enemy.getHealth() > 0) {
                enemy.takeDamageWithAbilities(5, battle);
            }
        }
    }

    private static void applyLavaSurge(ICard user, Battle battle) {
        boolean isPlayer = battle.isPlayerCard(user);
        List<ICard> enemies = isPlayer ? battle.getOpponentDeck() : battle.getPlayerDeck();
        for (ICard enemy : enemies) {
            if (enemy.getHealth() > 0) {
                enemy.takeDamageWithAbilities(5, battle);
            }
        }

        // Track backlash: 0 means starting next turn
        battle.getLavaSurgeCards().put(user, 0);
    }

    private static void handleLavaSurgeBacklash(ICard card, Battle battle) {
        Map<ICard, Integer> surgeMap = battle.getLavaSurgeCards();
        if (surgeMap.containsKey(card)) {
            int turns = surgeMap.get(card);
            if (turns > 0) {
                card.takeDamageWithAbilities(10, battle);
            }
            surgeMap.put(card, turns + 1); // Track turns since surge
        }
    }

    private static void applyAshenWings(ICard deadCard, Battle battle) {
        boolean isPlayer = battle.isPlayerCard(deadCard);
        List<ICard> enemies = isPlayer ? battle.getOpponentDeck() : battle.getPlayerDeck();
        for (ICard enemy : enemies) {
            if (enemy.getHealth() > 0) {
                enemy.takeDamageWithAbilities(10, battle);
            }
        }
    }

    private static void applyGlacialShield(ICard card, Battle battle) {
        battle.setGlacialShield(card, battle.getGlobalTurnCounter() + 1);
    }

    private static void applyRadiantLight(ICard card, Battle battle) {
        if (battle.getGlobalTurnCounter() % 3 == 0) {
            List<ICard> allies = battle.getPlayerDeck(); // assuming this card is always player-owned
            for (ICard ally : allies) {
                if (ally.getHealth() > 0) {
                    ally.setHealth(ally.getHealth() + 10);
                }
            }
        }
    }

    private static void applyCycloneFury(ICard card, Battle battle) {
        if (battle.getGlobalTurnCounter() % 2 != 0) return;

        List<ICard> enemies = battle.getOpponentDeckFor(card);
        List<ICard> aliveEnemies = new ArrayList<>();
        for (ICard enemy : enemies) {
            if (enemy.getHealth() > 0) {
                aliveEnemies.add(enemy);
            }
        }

        if (!aliveEnemies.isEmpty()) {
            ICard target = aliveEnemies.get((int) (Math.random() * aliveEnemies.size()));
            target.takeDamageWithAbilities(10, battle);
        }
    }

    private static void applyGaleForce(ICard target, Battle battle) {
        battle.applyGaleForceDebuff(target);
    }

    private static void applyEternalFlame(ICard card) {
        if (card instanceof BasicCard basicCard && !basicCard.hasRevived()) {
            basicCard.setHealth(basicCard.getOriginalHealth());
            basicCard.setAttack(basicCard.getAttack() + 5);
            basicCard.setHasRevived(true);
        }
    }
}
