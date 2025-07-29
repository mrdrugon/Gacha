import java.util.*;
import java.util.stream.Collectors;

public class CardAbilities {
    public static void onAttack(ICard attacker, ICard defender, ICard target, AbilityType type, Battle battle) {
        if (attacker.isHexed()) return;

        switch (type) {
            case HEX:
                defender.applyHex(1);
                break;
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
            case FIRESTORM:
                applyLavaSurge(attacker, battle);
                break;
            case SHADOW_DRAIN:
                applyShadowDrain(attacker, target);
                break;
            case VOLCANIC_CORE:
                applyVolcanicCore(attacker, target, battle);
                break;
            case DARK_VISION:
                //applyDarkVision(attacker, battle);
                break;
            case SUNSTRIKE:
                if (target.getAttack() > attacker.getAttack()) {
                    target.takeDamageWithAbilities(5, battle); // bonus damage
                }
                break;
            case METAL_SLUSH:
                if (!battle.isSecondAttack() && Math.random() < 0.5) {
                    battle.setSecondAttack(true);
                    attacker.attack(defender, battle); // Perform second attack
                    battle.setSecondAttack(false);
                }
                break;
            case SOLAR_FLARE:
                target.applyBurn(2);
             break;
            case SHADOW_SLASH:
                if (Math.random() < 0.3){
                    target.applyTemporaryAttackReduction(3, 1);
                }
                break;
            case WHITE_STRIKE:
                List<ICard> allies = battle.getPlayerDeck();
                boolean isPlayer = attacker == battle.getSelectedPlayerCard();
                List<ICard> friendlyCards = isPlayer ? battle.getPlayerDeck() : battle.getOpponentDeck();

                long aliveAllies = friendlyCards.stream().filter(c -> c.getHealth() > 0 && c != attacker).count();

                if (aliveAllies == 0){
                    target.takeDamageWithAbilities(attacker.getAttack() * 2, battle);
                    return;
                }
                break;
            case PHANTOM_STRIKE:
                if (Math.random() < 0.20){
                    if (target instanceof ICard){
                        target.setStunned(true);
                    }
                }
                break;
            case WILD_ROAR:
                applyWildRoar(attacker, battle);
                break;
            case ENTANGLE:
                if (target instanceof BasicCard targetCard){
                    targetCard.applyTemporaryAttackReduction(4, 2);
                }
                break;
        }
    }

    public static void onTakeDamage(ICard attacker, ICard defender, ICard card, int damage, AbilityType type, Battle battle) {
        if (attacker.isHexed()) return;

        switch (type) {
            case HEX:
                defender.applyHex(1);
                break;
            case LIGHTNING_CHARGE:
                applyLightningCharge(card);
                break;
            case THORN_ARMOR:
                applyThornArmor(card, damage, battle);
                break;
            case WATER_SHIELD:
                applyWaterShieldDamageReduction(card, damage, battle);
                break;
            case GEM_SHIELD:
                applyGemShield(card, damage);
                break;
            case SHIMMERING_RETALIATION:
                applyShimmeringRetaliation(card, attacker);
                break;
            case PHOTOSYNYHESIS:
                break;
        }
    }

    public static void onTurnStart(ICard attacker, ICard defender, ICard card, AbilityType type, Battle battle) {
        if (attacker.isHexed()) return;

        card.tickAttackReduction();

        if (card.isBurning()){
            card.tickBurn();
        }
        switch (type){
            case HEX:
                defender.applyHex(1);
                break;
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
            case WATER_SHIELD:
                applyWaterShieldHealing(card);
                break;
            case SILVER_SHIELD:
                List<ICard> team = battle.isPlayerCard(card) ? battle.getPlayerDeck() : battle.getOpponentDeck();
                List<ICard> eligibleTargets = team.stream()
                        .filter(c -> c != card && c.getHealth() > 0)
                        .toList();

                if (!eligibleTargets.isEmpty()) {
                    ICard target = eligibleTargets.get(battle.random.nextInt(eligibleTargets.size()));
                    if (target instanceof BasicCard basicTarget) {
                        basicTarget.setHealth(basicTarget.getHealth() + 10);
                    }
                }
                break;
            case DUALITY:
                if (card instanceof BasicCard basicCard){
                    if (basicCard.getAbilities().stream().anyMatch(a -> a != AbilityType.DUALITY)) return;;

                    List<ICard> allDeadCards = new ArrayList<>();
                    for (ICard c : battle.getPlayerDeck()){
                        if (c.getHealth() <= 0) allDeadCards.add(c);
                    }
                    for (ICard c : battle.getOpponentDeck()){
                        if (c.getHealth() <= 0) allDeadCards.add(c);
                    }

                    List<AbilityType> allAbilities = new ArrayList<>();
                    for (ICard dead : allDeadCards){
                        for (AbilityType ab : dead.getAbilities()){
                            if (ab != AbilityType.DUALITY){
                                allAbilities.add(ab);
                            }
                        }
                    }

                    if (!allAbilities.isEmpty()){
                        AbilityType copied = allAbilities.get((int) (Math.random() * allAbilities.size()));
                        basicCard.addAbility(copied);
                    }
                }
                break;
            case MIND_WARP:
                List<ICard> enemyDeck = battle.getOpponentDeckFor(card);
                List<ICard> validTargets = new ArrayList<>();

                for (ICard enemy : enemyDeck){
                    if (enemy.getHealth() > 0 && enemy != card){
                        validTargets.add(enemy);
                    }
                }

                if (!validTargets.isEmpty()){
                    ICard chosen = validTargets.get((int) (Math.random() * validTargets.size()));
                    if (chosen instanceof BasicCard basicEnemy){
                        int oldAtk = basicEnemy.getAttack();
                        basicEnemy.setAttack(oldAtk - 3);
                    }
                }
                break;
            case SPREADING_ROOTS:
                if (card instanceof BasicCard basic){
                    basic.incrementRootsCounter();
                    if (basic.shouldTriggerRootsHeal()){
                        List<ICard> friendly = battle.isPlayerCard(card) ? battle.getPlayerDeck() : battle.getOpponentDeck();
                        for (ICard ally : friendly){
                            if (ally.getHealth() > 0){
                                ally.setHealth(ally.getHealth() + 3);
                            }
                        }
                    }
                }
                break;
            case SOOTHING_BLOOM:
                if (card instanceof BasicCard basic){
                    basic.incrementSoothingBloomCounter();
                    if (basic.shouldTriggerSoothingBlom()){
                        List<ICard> friendly = battle.isPlayerCard(card) ? battle.getPlayerDeck() : battle.getOpponentDeck();
                        List<ICard> aliveAllies = friendly.stream().filter(c -> c.getHealth() > 0 && c != card).toList();

                        if (!aliveAllies.isEmpty()){
                            ICard target = aliveAllies.get(battle.random.nextInt(aliveAllies.size()));
                            target.setHealth(target.getHealth() + 8);
                        }
                    }
                }
                break;
            case HEALING_SPROUT:
                //applyHealingSprout(card, battle);
                break;
            case LURING_SONG:
                ICard enemyTarget = battle.getNextOpponentCard();
                if (enemyTarget != null){
                    if (enemyTarget instanceof  BasicCard){
                        ((BasicCard) enemyTarget).setLuringTarget(true);
                    }
                }
                break;
        }
    }

    public static void onTurnEnd(ICard attacker, ICard defender, ICard card, AbilityType type, Battle battle) {
        if (attacker.isHexed()) return;
        switch (type){
            case HEX:
                defender.applyHex(1);
                break;
            case RADIANT_BALANCE:
                applyRadiantBalance(card, battle);
                break;
            case OCEANS_PATIENCE:
                if (!card.hasAttackedThisTurn()){
                    card.setAttack(card.getAttack() + 2);
                    card.setHealth(card.getHealth() + 5);
                }
                break;
        }
    }

    public static void onDeath(ICard attacker, ICard defender, ICard card, ICard killer, AbilityType type, Battle battle) {
        if (attacker.isHexed()) return;
        switch (type) {
            case HEX:
                defender.applyHex(1);
                break;
            case MOLTEN_CORE:
                applyMoltenCore(card, killer, battle);
                break;
            case ASHEN_WINGS:
                applyAshenWings(card, battle);
                break;
            case ETERNAL_FLAME:
                applyEternalFlame(card);
                break;
            case SEED_SCATTER:
                applySeedScatter(card, battle);
                break;
            // Add more death-triggered abilities here
        }
    }

    public static void onSummon(ICard attacker, ICard defender, BasicCard summoned, AbilityType type, Battle battle){
        if (attacker.isHexed()) return;
        switch (type){
            case HEX:
                defender.applyHex(1);
                break;
            case PROTECTIVE_GLEAM:
                List<ICard> friendly = battle.getPlayerDeck();
                ICard lowest = friendly.stream().filter(c -> c != summoned && c.getHealth() > 0).min(Comparator.comparingInt(ICard::getHealth)).orElse(null);
                if (lowest instanceof BasicCard basic){
                    basic.applyShield(15);
                }
                break;
        }
    }

    public static void onDefend(ICard attacker, ICard defender, AbilityType type, Battle battle){
        if (attacker.isHexed()) return;
        switch (type){
            case HEX:
                defender.applyHex(1);
                break;
        }
    }

    private static  void applyWildRoar(ICard card, Battle battle){
        List<ICard> enemies = battle.isPlayerCard(card) ? battle.getOpponentDeck() : battle.getPlayerDeck();

        for (ICard enemy : enemies){
            if (enemy.getHealth() > 0){
                enemy.takeDamage(5);
            }
        }

        int newHealth = card.getHealth() + 5;
        card.setHealth(newHealth);
    }

    /*private static void applyHealingSprout(ICard card, Battle battle){
        int turn = battle.getTurnNumber();
        if (turn % 2 != 0) return;

        List<ICard> team = battle.isPlayerCard(card) ? battle.getPlayerDeck() : battle.getOpponentDeck();

        List<ICard> healable = team.stream().filter(c -> c.getHealth() > 0 && c.getHealth() < c.getOriginalHealth()).collect(Collectors.toList());

        if (healable.isEmpty()) return;

        ICard target = healable.get(new Random().nextInt(healable.size()));
        int original = target.getHealth();
        target.setHealth(original + 15);
    }
    */
    private static void applySeedScatter(ICard card, Battle battle){
        if(!(card instanceof BasicCard)) return;

        boolean isPlayer = battle.isPlayerCard(card);
        List<ICard> team = isPlayer ? battle.getPlayerDeck() : battle.getOpponentDeck();

        long aliveCount = team.stream().filter(c -> c.getHealth() > 0).count();
        if (aliveCount >= 5) return;
    }

    /*public static void applyDarkVision(ICard card, Battle battle) {
        List<ICard> opponentHand = battle.getOpponentDeck();
        List<ICard> hiddenCards = opponentHand.stream()
                .filter(c -> !c.isRevealed() && c.getHealth() > 0)
                .collect(Collectors.toList());

        if (!hiddenCards.isEmpty()) {
            ICard selected = hiddenCards.get(battle.getRandom().nextInt(hiddenCards.size()));
            selected.isRevealed();
            selected.setAttack(Math.max(0, selected.getAttack() - 3));
        }
    }
*/

    public static void applyVolcanicCore(ICard card, ICard target, Battle battle) {
        if (card != null && target != null) {
            card.takeDamageWithAbilities(10, battle);
            target.takeDamageWithAbilities(10, battle);
        }
    }

    private static void applyShimmeringRetaliation(ICard card, ICard attacker) {
        if (attacker != null) {
            attacker.takeDamage(5);
        }
        card.setHealth(card.getHealth() + 2); // Boost current health
    }

    private static void applyShadowDrain(ICard attacker, ICard target) {
        if (attacker instanceof BasicCard basicAttacker && !basicAttacker.hasUsedShadowDrain()) {
            if (!hasRustResistance(target)) {
                int stolenAmount = Math.min(5, target.getAttack());
                target.setAttack(target.getAttack() - stolenAmount);
                basicAttacker.setAttack(basicAttacker.getAttack() + stolenAmount);
            }
            basicAttacker.setHasUsedShadowDrain(true);
        }
    }
    private static void applyGemShield(ICard card, int incomingDamage){
        int reducedDamage = (int) Math.ceil(incomingDamage * 0.8); // 20% damage reduction
        int difference = incomingDamage - reducedDamage;
        card.setHealth(card.getHealth() + difference);
    }

    private static void applyWaterShieldDamageReduction(ICard card, int damage, Battle battle) {
        int reducedDamage = Math.max(0, damage - 5);
        if (reducedDamage > 0) {
            card.takeDamageWithAbilities(reducedDamage, battle);
        }
    }

    private static void applyWaterShieldHealing(ICard card) {
        if (card.getHealth() > 0) {
            card.setHealth(card.getHealth() + 5);
        }
    }

    private static void applyMoltenCore(ICard self, ICard killer, Battle battle) {
        if (killer != null && killer.getHealth() > 0) {
            killer.takeDamageWithAbilities(15, battle);
        }
    }

    private static void applyFrostbite(ICard target) {
        if (!hasRustResistance(target)) {
            target.setAttack(Math.max(0, target.getAttack() - 2));
        }
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
                enemy.takeDamageWithAbilities(10, battle);
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

    private static boolean hasRustResistance(ICard card) {
        return card.getAbilities().contains(AbilityType.RUST_RESISTANCE);
    }

}