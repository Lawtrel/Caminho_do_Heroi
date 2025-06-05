package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.*;
import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.items.Item;
import br.lawtrel.hero.entities.items.ItemFactory;
import br.lawtrel.hero.entities.items.drops.DropTableEntry;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;

import java.util.ArrayList;
import java.util.List;

public class BattleSystem {
    public void setBattleMessage(String battleMessage) {
        this.battleMessage = battleMessage;
    }

    public enum BattleState {
        COMMAND_MENU,
        PLAYER_TURN,
        PLAYER_TARGET_SELECT,
        PLAYER_MAGIC_SELECT,
        PLAYER_ITEM_SELECT,
        ENEMY_TURN,
        VICTORY,
        DEFEAT
    }

    private BattleState currentState = BattleState.COMMAND_MENU;
    private Character currentActor;
    private int totalExpGainedInBattle = 0;
    private int totalGoldGainedInBattle = 0;
    private boolean playerLeveledUpInThisBattle = false;
    private boolean rewardsProcessed = false;
    private List<Item> droppedItemsThisBattle;
    // Configurações de batalha
    private static final float ESCAPE_SUCCESS_RATE = 0.7f;
    private static final float ENEMY_SPELL_CHANCE = 0.3f;
    private static final int MIN_MP_FOR_SPELL = 10;

    // Componentes do sistema
    private final Player player;
    private final Array<Enemy> enemies;
    private Queue<Character> turnOrder;
    private final BattleHUD hud;
    private final TargetSelector targetSelector;
    private final BattleMagicMenu magicMenu;
    private final BattleItemMenu itemMenu;

    // Estado atual
    private BattleState state;
    private String battleMessage;
    private int selectedOption = 0;
    private final String[] menuOptions = {"ATACAR", "MAGIA", "ITENS", "FUGIR"};

    public BattleSystem(Player player, Array<Enemy> enemies) {
        this.player = player;
        this.enemies = enemies;
        this.turnOrder = new Queue<>();
        this.hud = new BattleHUD(this);
        this.battleMessage = "O que deseja fazer?";
        this.targetSelector = new TargetSelector();
        this.magicMenu = new BattleMagicMenu();
        this.itemMenu = new BattleItemMenu();

        initializeBattle();
    }

    private void initializeBattle() {
        resetBattleRewards();
        this.state = BattleState.COMMAND_MENU;
        this.battleMessage = "Uma batalha começou!";
        calculateTurnOrder();
        prepareNextTurn();
    }

    public void resetBattleRewards() {
        totalExpGainedInBattle = 0;
        totalGoldGainedInBattle = 0;
        playerLeveledUpInThisBattle = false;
        rewardsProcessed = false; /* resetar xp e gold  */
        if (droppedItemsThisBattle  == null) {
            droppedItemsThisBattle = new ArrayList<>();
        }
        droppedItemsThisBattle.clear();

    }

    public void update(float delta) {
        if (state == BattleState.VICTORY || state == BattleState.DEFEAT) {
            updateBattleEnd(state == BattleState.VICTORY);
            return;
        }

        // Se for o turno do inimigo, ele age imediatamente.
        if (state == BattleState.ENEMY_TURN && currentActor != null && currentActor != player.getCharacter()) {
            updateEnemyTurn(delta); // Inimigo age
        }

    }

    public void render(SpriteBatch batch) {
        hud.render(batch);
    }

    // Métodos de navegação no menu
    public void moveSelectionUp() {
        selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
    }

    public void moveSelectionDown() {
        selectedOption = (selectedOption + 1) % menuOptions.length;
    }


    public void calculateTurnOrder() {
        turnOrder.clear();
        Array<Character> allCombatants = new Array<>();
        allCombatants.add(player.getCharacter());

        // Adiciona personagens dos inimigos
        for (Enemy enemy : enemies) {
            allCombatants.add(enemy.getCharacter());
        }

        // Ordena por velocidade (maior primeiro)
        allCombatants.sort((c1, c2) -> Integer.compare(c2.getSpeed(), c1.getSpeed()));

        // Adiciona à fila de turnos
        for (Character c : allCombatants) {
            turnOrder.addLast(c);
        }
    }

    public void playerSelectAction(int actionIndex) {
        if (state != BattleState.PLAYER_TURN) return; // Só permite selecionar ação no turno do jogador

        this.selectedOption = actionIndex;
        if (actionIndex == 0) { // ATACAR
            setState(BattleState.PLAYER_TARGET_SELECT);
            battleMessage = "Selecione um alvo para ATACAR.";
        } else if (actionIndex == 1) { // MAGIA
            if (player.getCharacter().getGrimoire() != null &&
                player.getCharacter().getGrimoire().getSpellCount() > 0) {
                magicMenu.setMagics(player.getCharacter().getGrimoire().getAvailableSpells());
                setState(BattleState.PLAYER_MAGIC_SELECT);
                battleMessage = "Selecione uma MAGIA.";
            } else {
                battleMessage = "Nenhuma magia disponível!";
                // Mantém o estado PLAYER_TURN para o jogador tentar outra coisa
            }
        } else if (actionIndex == 2) { // ITENS
            if (!player.getInventory().isEmpty()) {
                itemMenu.setItems(player.getInventory());
                setState(BattleState.PLAYER_ITEM_SELECT);
                battleMessage = "Selecione um ITEM.";
            } else {
                battleMessage = "Nenhum item disponível!";
            }
        } else if (actionIndex == 3) { // FUGIR
            attemptEscape();
        }
    }

    public void playerAttack(int enemyIndex) {
        if (state != BattleState.PLAYER_TARGET_SELECT) return;
        if (isValidEnemyIndex(enemyIndex)) {
            Character target = enemies.get(enemyIndex).getCharacter();
            player.getCharacter().performAttack(target);
            battleMessage = player.getCharacter().getName() + " atacou " + target.getName();
            handleEnemyDefeated(target); // mudar o estado para VICTORY
            if (state != BattleState.VICTORY) { // Só avança o turno se a batalha não acabou
                advanceTurn();
            }
        }
    }

    public void playerCastSpell(int enemyIndex, Skill spell) {
        if (state != BattleState.PLAYER_TARGET_SELECT) return;
        if (isValidEnemyIndex(enemyIndex) && spell != null) {
            Character target = enemies.get(enemyIndex).getCharacter();
            player.getCharacter().castSpell(spell.getName(), target); // assumindo que castSpell usa MP e aplica efeitos
            battleMessage = player.getCharacter().getName() + " usou " + spell.getName() + " em " + target.getName();
            handleEnemyDefeated(target);
            if (state != BattleState.VICTORY) {
                advanceTurn();
            }
        } else {
            battleMessage = "Alvo ou magia inválida.";
            setState(BattleState.PLAYER_TURN); // Volta para seleção de ação principal
        }
    }

    private void executeEnemyAction(Enemy enemy) {
        boolean shouldCastSpell = Math.random() < ENEMY_SPELL_CHANCE &&
            enemy.getCharacter().getMp() > MIN_MP_FOR_SPELL &&
            enemy.getSpells().size > 0;

        if (shouldCastSpell) {
            Skill spell = enemy.getSpells().random();
            enemy.getCharacter().castSpell(spell.getName(), player.getCharacter());
            battleMessage = enemy.getName() + " usou " + spell.getName() + " em " + player.getCharacter().getName();
        } else {
            enemy.performAttack(player.getCharacter());
            battleMessage = enemy.getName() + " atacou " + player.getCharacter().getName();
        }
    }

    public void attemptEscape() {
        if (Math.random() < ESCAPE_SUCCESS_RATE) {
            state = BattleState.VICTORY;
            battleMessage = "Fuga bem sucedida!";
        } else {
            battleMessage = "Fuga falhou!";
            advanceTurn();
        }
    }

    // Métodos auxiliares
    private void handleEnemyDefeated(Character target) {
        Enemy defeatedEnemy = findEnemyByCharacter(target);
        if (defeatedEnemy != null && !target.isAlive()) {
            totalExpGainedInBattle += defeatedEnemy.getCharacter().getExpYieldOnDefeat(); // Acumula EXP
            totalGoldGainedInBattle += defeatedEnemy.getCharacter().getGoldYieldOnDefeat(); // Acumula Money

            if (defeatedEnemy.getCharacter().getDropTable() != null) {// Processar drops dos inimigos
                for (DropTableEntry entry : defeatedEnemy.getCharacter().getDropTable()) {
                    if (Math.random() < entry.getDropChance()) { // Verifica a chance de drop
                        Item droppedItem = ItemFactory.createItem(entry.getItemId());
                        if (droppedItem != null) {
                            player.addItem(droppedItem); // Adiciona ao inventário do jogador
                            if (this.droppedItemsThisBattle == null) { // Garante que a lista exista
                                this.droppedItemsThisBattle = new ArrayList<>();
                            }
                            this.droppedItemsThisBattle.add(droppedItem); // Adiciona à lista para exibição
                            System.out.println(player.getCharacter().getName() + " obteve: " + droppedItem.getName() + "!"); // Log
                        }
                    }
                }
            }
            enemies.removeValue(defeatedEnemy, true);
            removeFromTurnOrder(defeatedEnemy.getCharacter());

            if (enemies.size == 0) {
                state = BattleState.VICTORY;
            }
        }
    }

    public List<Item> getDroppedItemsThisBattle() {
        if (this.droppedItemsThisBattle == null) {
            return new ArrayList<>(); // Retorna uma lista vazia se for nula, para evitar NullPointerExceptions
        }
        return droppedItemsThisBattle;
    }

    public int getLastExpGained() { return totalExpGainedInBattle; }
    public int getLastGoldGained() { return totalGoldGainedInBattle; }
    public boolean didPlayerLevelUpInThisBattle() { return playerLeveledUpInThisBattle;}

    private void removeFromTurnOrder(Character character) {
        Queue<Character> newTurnOrder = new Queue<>();
        for (Character c : turnOrder) {
            if (c != character) {
                newTurnOrder.addLast(c);
            }
        }
        turnOrder = newTurnOrder;
    }

    private void checkPlayerDefeated() {
        if (!player.getCharacter().isAlive()) {
            state = BattleState.DEFEAT;
        }
    }

    private void advanceTurn() {
        if (state == BattleState.VICTORY || state == BattleState.DEFEAT) {
            return; // Batalha já terminou
        }

        if (turnOrder.size > 0) {
            turnOrder.removeFirst(); // Remove o personagem que acabou de agir
        }

        if (turnOrder.size == 0) {
            // Checa condições de fim de batalha antes de recalcular,
            // para evitar loops se todos os inimigos morrerem no último turno de um ciclo.
            if (enemies.size == 0) {
                setState(BattleState.VICTORY);
                battleMessage = "Todos os inimigos foram derrotados!";
                return;
            }
            if (!player.getCharacter().isAlive()) {
                setState(BattleState.DEFEAT);
                return;
            }
            calculateTurnOrder(); // Recalcula se a fila esvaziou (fim de um "round")
        }

        if (turnOrder.size > 0) {
            prepareNextTurn();
        } else {
            checkBattleEnd(); // Força uma checagem de fim de batalha.
        }
    }

    private void checkBattleEnd() {
        if (enemies.size == 0 && player.getCharacter().isAlive()) {
            setState(BattleState.VICTORY);
            battleMessage = "Todos os inimigos foram derrotados!";
        } else if (!player.getCharacter().isAlive()) {
            setState(BattleState.DEFEAT);
        }
        // Se nenhuma das condições acima, a batalha continua.
    }


    private Enemy findEnemyByCharacter(Character character) {
        for (Enemy enemy : enemies) {
            if (enemy.getCharacter() == character) {
                return enemy;
            }
        }
        return null;
    }

    private boolean isValidEnemyIndex(int index) {
        return index >= 0 && index < enemies.size;
    }

    private void updateEnemyTurn(float delta) {
        if (currentActor == null || currentActor == player.getCharacter() || !currentActor.isAlive()) {
            // Se o ator atual não é um inimigo válido, algo está errado, tenta avançar.
            advanceTurn();
            return;
        }

        Enemy currentEnemy = findEnemyByCharacter(currentActor);
        if (currentEnemy != null) {
            battleMessage = currentEnemy.getName() + " está agindo...";

            executeEnemyAction(currentEnemy); // Inimigo ataca

            checkPlayerDefeated();

            if (state != BattleState.DEFEAT) { // Só avança o turno se a batalha não acabou
                advanceTurn();
            }
        } else {
            advanceTurn();
        }
    }

    public boolean isSubWindowVisible() {
        return state == BattleState.PLAYER_TARGET_SELECT ||
            state == BattleState.PLAYER_MAGIC_SELECT ||
            state == BattleState.PLAYER_ITEM_SELECT;
    }

    private void updateBattleEnd(boolean victory) {
        if (rewardsProcessed) {
            return;
        }
        if (victory) {
            this.battleMessage = "Vitória!";
            // Aplica EXP e Gold ao personagem
            if (player  != null && player.getCharacter() != null) {
                if (totalExpGainedInBattle > 0) {
                    player.getCharacter().gainExp(totalExpGainedInBattle);
                    this.playerLeveledUpInThisBattle = player.getCharacter().didLevelUpThisGain();
                }
                if (totalGoldGainedInBattle > 0) {
                    player.addMoney(totalGoldGainedInBattle);
                }
                //logica de item
            }
            rewardsProcessed = true; // foi processado recompensas
        } else {
            this.battleMessage = "DERROTA";
        }
    }
    private void prepareNextTurn() {
        if (turnOrder.size == 0) {
            if (enemies.size == 0) {
                setState(BattleState.VICTORY);
                battleMessage = "Todos os inimigos foram derrotados!";
                return;
            }
            if (!player.getCharacter().isAlive()) {
                setState(BattleState.DEFEAT);
                return;
            }
            calculateTurnOrder();
        }

        currentActor = turnOrder.first(); // Pega o próximo da fila SEM REMOVER ainda

        if (currentActor == player.getCharacter()) {
            setState(BattleState.PLAYER_TURN);
            battleMessage = player.getCharacter().getName() + " - Escolha uma ação";
            selectedOption = 0; // Reseta a seleção do menu
        } else {
            setState(BattleState.ENEMY_TURN);
            // battleMessage será definido em updateEnemyTurn
        }
    }

    // Getters
    public BattleState getState() { return state; }
    public String getBattleMessage() { return battleMessage; }
    public int getSelectedOption() { return selectedOption; }
    public Array<Enemy> getEnemies() { return enemies; }
    public Player getPlayer() { return player; }
    public BattleHUD getHud() { return hud; }
    public BitmapFont getFont() { return hud.getFont(); }
    public TargetSelector getTargetSelector() {
        return targetSelector;
    }

    public BattleMagicMenu getMagicMenu() {
        return magicMenu;
    }

    public BattleItemMenu getItemMenu() {
        return itemMenu;
    }

    public void setState(BattleState state) {
        this.state = state;
    }

    public void setSelectedMagic(Skill spell) {
        this.magicMenu.setSelectedMagic(spell);
    }
}
