package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.*;
import br.lawtrel.hero.entities.Character;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;

public class BattleSystem {
    public enum BattleState {
        COMMAND_MENU,
        TARGET_SELECTION,
        MAGIC_MENU,
        ITEM_MENU,
        PLAYER_TURN,
        PLAYER_TARGET_SELECT,
        PLAYER_MAGIC_SELECT,
        PLAYER_ITEM_SELECT,
        ENEMY_TURN,
        VICTORY,
        DEFEAT
    }

    private BattleState currentState = BattleState.COMMAND_MENU;

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
        this.state = BattleState.PLAYER_TURN;
        this.battleMessage = "Uma batalha começou!";
        calculateTurnOrder();
    }

    public void update(float delta) {
        switch (state) {
            case PLAYER_TURN:         updatePlayerTurn(); break;
            case PLAYER_TARGET_SELECT: updateTargetSelection(); break;
            case PLAYER_MAGIC_SELECT: updateMagicSelection(); break;
            case PLAYER_ITEM_SELECT:  updateItemSelection(); break;
            case ENEMY_TURN:         updateEnemyTurn(delta); break;
            case VICTORY:            updateBattleEnd(true); break;
            case DEFEAT:             updateBattleEnd(false); break;
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
        this.selectedOption = actionIndex;

        switch (actionIndex) {
            case 0: state = BattleState.PLAYER_TARGET_SELECT; break; // Ataque
            case 1:
                if (player.getCharacter().getGrimoire() != null &&
                    player.getCharacter().getGrimoire().getSpellCount() > 0) {
                    state = BattleState.PLAYER_MAGIC_SELECT;
                } else {
                    battleMessage = "Nenhuma magia disponível!";
                }
                break;
            case 2:
                if (!player.getInventory().isEmpty()) {
                    state = BattleState.PLAYER_ITEM_SELECT;
                } else {
                    battleMessage = "Nenhum item disponível!";
                }
                break;
            case 3: attemptEscape(); break; // Fugir
        }
    }

    public void playerAttack(int enemyIndex) {
        if (isValidEnemyIndex(enemyIndex)) {
            Character target = enemies.get(enemyIndex).getCharacter();
            player.getCharacter().performAttack(target);
            battleMessage = player.getCharacter().getName() + " atacou " + target.getName();
            handleEnemyDefeated(target);
            advanceTurn();
        }
    }

    public void playerCastSpell(int enemyIndex, Skill spell) {
        if (isValidEnemyIndex(enemyIndex) && spell != null) {
            Character target = enemies.get(enemyIndex).getCharacter();
            player.getCharacter().castSpell(spell.getName(), target);
            battleMessage = player.getCharacter().getName() + " usou " + spell.getName();
            handleEnemyDefeated(target);
            advanceTurn();
        }
    }

    public void playerUseItem(int itemIndex) {
        // Implementação básica - pode ser expandida
        battleMessage = "Item usado!";
        advanceTurn();
    }

    private void executeEnemyActions() {
        for (Character character : turnOrder) {
            Enemy enemy = findEnemyByCharacter(character);
            if (enemy != null) {
                executeEnemyAction(enemy);
                checkPlayerDefeated();
            }
        }
    }

    private void executeEnemyAction(Enemy enemy) {
        boolean shouldCastSpell = Math.random() < ENEMY_SPELL_CHANCE &&
            enemy.getCharacter().getMp() > MIN_MP_FOR_SPELL &&
            enemy.getSpells().size > 0;

        if (shouldCastSpell) {
            Skill spell = enemy.getSpells().random();
            enemy.getCharacter().castSpell(spell.getName(), player.getCharacter());
            battleMessage = enemy.getName() + " usou " + spell.getName();
        } else {
            enemy.performAttack(player.getCharacter());
            battleMessage = enemy.getName() + " atacou";
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
            enemies.removeValue(defeatedEnemy, true);
            removeFromTurnOrder(defeatedEnemy.getCharacter());

            if (enemies.size == 0) {
                state = BattleState.VICTORY;
                battleMessage = "Todos os inimigos foram derrotados!";
            }
        }
    }

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
        if (turnOrder.size == 0) {
            calculateTurnOrder();
        } else {
            turnOrder.removeFirst();
            if (turnOrder.size == 0) calculateTurnOrder();
        }

        if (turnOrder.size > 0) {
            Character next = turnOrder.first();
            state = (next == player.getCharacter()) ?
                BattleState.PLAYER_TURN : BattleState.ENEMY_TURN;
            battleMessage = (state == BattleState.PLAYER_TURN) ?
                "Seu turno - Escolha uma ação" : "Turno do inimigo";
        }
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

    // Métodos de atualização de estado
    private void updatePlayerTurn() {
        battleMessage = "Seu turno - Escolha uma ação";
    }

    private void updateTargetSelection() {
        battleMessage = "Selecione um alvo";
    }

    private void updateMagicSelection() {
        magicMenu.setMagics(player.getCharacter().getGrimoire().getAvailableSpells());
        battleMessage = "Selecione uma magia";
    }

    private void updateItemSelection() {
        itemMenu.setItems(player.getInventory());
        battleMessage = "Selecione um item";
    }

    private void updateEnemyTurn(float delta) {
        battleMessage = "Turno do inimigo";
        executeEnemyActions();
    }

    public boolean isSubWindowVisible() {
        return state == BattleState.PLAYER_TARGET_SELECT ||
            state == BattleState.PLAYER_MAGIC_SELECT ||
            state == BattleState.PLAYER_ITEM_SELECT;
    }

    private void updateBattleEnd(boolean victory) {
        battleMessage = victory ? "Vitória!" : "Derrota...";
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
