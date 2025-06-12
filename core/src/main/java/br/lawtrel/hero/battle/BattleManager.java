package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.EnemyFactory;
import br.lawtrel.hero.entities.EnemyFactory.EnemyType;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.Enemy;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class BattleManager {
    private final Player player;
    private Array<Enemy> enemies;
    private BattleSystem battleSystem;
    private BattleState currentState;

    // Constante para cálculo de experiência
    private static final int EXP_PER_LEVEL = 10;

    public BattleManager(Player player) {
        this.player = player;
        this.enemies = new Array<>();
        this.currentState = BattleState.EXPLORATION;
    }

    /**
     * Inicia uma batalha com inimigos aleatórios baseados no nível do jogador
     */
    public void startRandomBattle() {
        EnemyType randomType = getRandomEnemyType();
        Enemy enemy = EnemyFactory.createEnemy(randomType, 100, 100);
        startBattleWithEnemy(enemy);
    }

     //Inicia uma batalha com inimigos pré-definidos
    public void startPredefinedBattle(Array<Enemy> predefinedEnemies) {
        if (predefinedEnemies == null || predefinedEnemies.size == 0) {
            throw new IllegalArgumentException("Enemies array cannot be null or empty");
        }
        this.enemies = predefinedEnemies;
        initializeBattleSystem();
    }

    public void update(float delta) {
        if (currentState == BattleState.BATTLE_STARTED) {
            battleSystem.update(delta);
            checkBattleResult();
        }
    }

    public void render(SpriteBatch batch) {
        if (currentState == BattleState.BATTLE_STARTED) {
            battleSystem.render(batch);
        }
    }

    public BattleState getCurrentState() {
        return currentState;
    }

    private EnemyType getRandomEnemyType() {
        EnemyType[] types = EnemyType.values();
        return types[(int)(Math.random() * types.length)];
    }

    private void startBattleWithEnemy(Enemy enemy) {
        enemies.clear();
        enemies.add(enemy);
        initializeBattleSystem();
    }

    private void initializeBattleSystem() {
        this.battleSystem = new BattleSystem(player, enemies);
        this.currentState = BattleState.BATTLE_STARTED;
    }

    private void checkBattleResult() {
        BattleSystem.BattleState systemState = battleSystem.getState();

        if (systemState == BattleSystem.BattleState.VICTORY) {
            endBattle(true);
        } else if (systemState == BattleSystem.BattleState.DEFEAT) {
            endBattle(false);
        }
    }

    private void endBattle(boolean victory) {
        if (victory) {
            grantVictoryRewards();
            battleSystem.setState(BattleSystem.BattleState.VICTORY); // Ou um novo BATTLE_WON_REWARDS
            battleSystem.setBattleMessage(player.getCharacter().getName() + " venceu a batalha!");
        }
        this.currentState = BattleState.EXPLORATION;
    }

    private void grantVictoryRewards() {
        int expGained =  battleSystem.getLastExpGained(); // Pega o EXP do BattleSystem();
        int goldGained = battleSystem.getLastGoldGained();
        if (expGained > 0) {
            player.getCharacter().gainExp(expGained);

            if (player.getCharacter().didLevelUpThisGain()) {
                battleSystem.setBattleMessage(player.getCharacter().getName() + " subiu para o nível " + player.getCharacter().getLevel() + "!");
                System.out.println(player.getCharacter().getName() + " subiu para o nível " + player.getCharacter().getLevel() + "!");
            } else {
                battleSystem.setBattleMessage(player.getCharacter().getName() + " ganhou " + expGained + " EXP.");
                System.out.println(player.getCharacter().getName() + " ganhou " + expGained + " EXP!");
            }
        } else {
            battleSystem.setBattleMessage("Nenhum EXP Ganho.");

        }
        player.addMoney(goldGained);
        System.out.println(player.getCharacter().getName() + " ganhou " + goldGained + " de Ouro!");

        // Adicionar lógica para itens aqui se necessário
    }

    public enum BattleState {
        EXPLORATION,
        BATTLE_STARTED,
        BATTLE_ENDED
    }
}
