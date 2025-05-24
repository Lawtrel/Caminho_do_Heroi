package br.lawtrel.hero.screens;

import br.lawtrel.hero.battle.*;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.Enemy;
import br.lawtrel.hero.entities.items.Item;
import br.lawtrel.hero.utils.BackgroundManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public class BattleScreen implements Screen, InputProcessor {
    private final BattleSystem battleSystem;
    private final SpriteBatch batch;
    private final BackgroundManager bgManager;
    protected final Player player;

    // Posições de renderização
    private static final int PLAYER_X = Gdx.graphics.getWidth() * 3/4;
    private static final int ENEMIES_X = Gdx.graphics.getWidth() /4;
    private static final int BASE_Y = Gdx.graphics.getHeight() / 2;

    public BattleScreen(Player player, Array<Enemy> enemies) {
        this.player = player;
        this.bgManager = new BackgroundManager();
        bgManager.setCurrentArea(player.getCurrentArea()); // Define bg pela área atual
        this.batch = new SpriteBatch();
        this.battleSystem = new BattleSystem(player, enemies);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        clearScreen();
        renderBackground();
        renderBattleEntities();
        battleSystem.update(delta);
        renderHUD();

        // Verifica se a batalha terminou
        if (battleSystem.getState() == BattleSystem.BattleState.VICTORY ||
            battleSystem.getState() == BattleSystem.BattleState.DEFEAT) {
            handleBattleEnd();
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    protected void renderBackground() {
        batch.begin();
        batch.draw(bgManager.getCurrentBackground(),
            0, 0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight());
        batch.end();
    }

    private void renderBattleEntities() {
        batch.begin();
        // Renderiza o jogador
        battleSystem.getPlayer().render(batch,
            PLAYER_X - battleSystem.getPlayer().getBounds().width/2,
            BASE_Y - battleSystem.getPlayer().getBounds().height/2);

        // Renderiza os inimigos
        Array<Enemy> enemies = battleSystem.getEnemies();
        for (int i = 0; i < enemies.size; i++) {
            float Y = (enemies.size > 1) ? (i * 80) - ((enemies.size-1) * 40) : 0;
            enemies.get(i).render(batch,
                ENEMIES_X - enemies.get(i).getBounds().width/2,
                (int) (BASE_Y + Y - enemies.get(i).getBounds().height/2));
        }
        batch.end();
    }

    private void renderHUD() {
        batch.begin();
        battleSystem.render(batch);
        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (battleSystem.getState()) {
            case PLAYER_TURN:
                handlePlayerTurnInput(keycode);
                break;
            case PLAYER_TARGET_SELECT:
                handleTargetSelectInput(keycode);
                break;
            case PLAYER_MAGIC_SELECT:
                handleMagicSelectInput(keycode);
                break;
            case PLAYER_ITEM_SELECT:
                handleItemSelectInput(keycode);
                break;
            case VICTORY:
            case DEFEAT:
                handleBattleEndInput(keycode);
                break;
        }
        return true;
    }

    private void handlePlayerTurnInput(int keycode) {
        if (keycode == Input.Keys.DOWN) {
            battleSystem.moveSelectionDown();
        } else if (keycode == Input.Keys.UP) {
            battleSystem.moveSelectionUp();
        } else if (keycode == Input.Keys.ENTER) {
            battleSystem.playerSelectAction(battleSystem.getSelectedOption());
        } else if (keycode == Input.Keys.ESCAPE) {
            // Pode adicionar lógica para sair da batalha
        }
    }

    private void handleTargetSelectInput(int keycode) {
        if (keycode == Input.Keys.DOWN) {
            battleSystem.getTargetSelector().nextTarget(battleSystem.getEnemies().toArray(Enemy.class));
        } else if (keycode == Input.Keys.UP) {
            battleSystem.getTargetSelector().previousTarget(battleSystem.getEnemies().toArray(Enemy.class));
        } else if (keycode == Input.Keys.ENTER) {
            if (battleSystem.getState() == BattleSystem.BattleState.PLAYER_TARGET_SELECT) {
                if (battleSystem.getState() == BattleSystem.BattleState.PLAYER_MAGIC_SELECT) {
                    battleSystem.playerCastSpell(
                        battleSystem.getTargetSelector().getSelectedTarget(),
                        battleSystem.getMagicMenu().getSelectedMagic()
                    );
                } else {
                    battleSystem.playerAttack(battleSystem.getTargetSelector().getSelectedTarget());
                }
            }
        } else if (keycode == Input.Keys.ESCAPE) {
            battleSystem.setState(BattleSystem.BattleState.PLAYER_TURN);
        }
    }

    private void handleMagicSelectInput(int keycode) {
        if (keycode == Input.Keys.DOWN) {
            battleSystem.getMagicMenu().nextMagic();
        } else if (keycode == Input.Keys.UP) {
            battleSystem.getMagicMenu().previousMagic();
        } else if (keycode == Input.Keys.ENTER) {
            battleSystem.setSelectedMagic(battleSystem.getMagicMenu().getSelectedMagic());
            battleSystem.setState(BattleSystem.BattleState.PLAYER_TARGET_SELECT);
        } else if (keycode == Input.Keys.ESCAPE) {
            battleSystem.setState(BattleSystem.BattleState.PLAYER_TURN);
        }
    }

    private void handleItemSelectInput(int keycode) {
        if (keycode == Input.Keys.DOWN) {
            battleSystem.getItemMenu().nextItem();
        } else if (keycode == Input.Keys.UP) {
            battleSystem.getItemMenu().previousItem();
        } else if (keycode == Input.Keys.ENTER) {
            //battleSystem.playerUseItem(battleSystem.getSelectedItem());
        } else if (keycode == Input.Keys.ESCAPE) {
            battleSystem.setState(BattleSystem.BattleState.PLAYER_TURN);
        }
    }

    private void handleBattleEndInput(int keycode) {
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.ESCAPE) {
        }
    }

    private void handleBattleEnd() {
        renderVictoryOrDefeatSummary();
    }

    private void renderVictoryOrDefeatSummary() {
        batch.begin();
        BitmapFont font = battleSystem.getHud().getFont();
        float centerX = Gdx.graphics.getWidth() / 2f;
        float startY = Gdx.graphics.getHeight() * 0.8f; // Começar um pouco mais alto
        float lineSpacing = 25f; // Ajuste conforme necessário

        if (battleSystem.getState() == BattleSystem.BattleState.VICTORY) {
            font.draw(batch, "VITÓRIA!", centerX - font.getXHeight() * 3, startY);
            startY -= lineSpacing * 1.5f;

            font.draw(batch, "EXP Ganho: " + battleSystem.getLastExpGained(), centerX - 100, startY);
            startY -= lineSpacing;

            font.draw(batch, "Ouro Ganho: " + battleSystem.getLastGoldGained(), centerX - 100, startY);
            startY -= lineSpacing;

            if (battleSystem.didPlayerLevelUpInThisBattle()) {
                font.draw(batch, player.getCharacter().getName() + " subiu para o Nível " + player.getCharacter().getLevel() + "!", centerX - 150, startY);
                startY -= lineSpacing;
            }

            // Exibir Itens Dropados
            List<Item> itemsDropped = battleSystem.getDroppedItemsThisBattle();
            if (itemsDropped != null && !itemsDropped.isEmpty()) {
                startY -= lineSpacing * 0.5f; // Espaço antes da lista de itens
                font.draw(batch, "Itens Obtidos:", centerX - 100, startY);
                startY -= lineSpacing;
                for (Item item : itemsDropped) {
                    font.draw(batch, "- " + item.getName(), centerX - 90, startY);
                    startY -= lineSpacing;
                    if (startY < Gdx.graphics.getHeight() * 0.15f) { // Evita desenhar fora da tela
                        // TODO: Implementar paginação ou scroll se muitos itens droparem
                        font.draw(batch, "  (e mais...)", centerX - 90, startY);
                        break;
                    }
                }
            } else {
                font.draw(batch, "Nenhum item obtido.", centerX - 100, startY);
                startY -= lineSpacing;
            }
            startY -= lineSpacing * 0.5f; // Espaço após lista de itens

        } else { // DEFEAT
            font.draw(batch, "DERROTA...", centerX - font.getXHeight() * 4, startY);
            startY -= lineSpacing * 1.5f;
        }

        font.draw(batch, "Pressione ENTER para continuar...", centerX - 150, startY);
        batch.end();
    }


    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        bgManager.dispose();
        battleSystem.getHud().dispose();
    }

    // Métodos não utilizados da interface InputProcessor
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
