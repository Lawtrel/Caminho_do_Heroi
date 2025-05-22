package br.lawtrel.hero.screens;

import br.lawtrel.hero.battle.*;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.Enemy;
import br.lawtrel.hero.utils.BackgroundManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

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
            // Retorna para a tela anterior ou mapa do jogo
            // Exemplo: game.setScreen(new WorldMapScreen(game));
        }
    }

    private void handleBattleEnd() {
        // Lógica para quando a batalha termina (vitória ou derrota)
        // Pode exibir uma mensagem final e esperar input do jogador
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
