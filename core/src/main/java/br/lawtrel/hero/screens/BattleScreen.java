package br.lawtrel.hero.screens;

import br.lawtrel.hero.battle.*;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.Enemy;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class BattleScreen implements Screen {
    private final BattleSystem battleSystem;
    private final SpriteBatch batch;
    private final TargetSelector targetSelector;
    private final BattleMagicMenu magicMenu;

    // Posições de renderização
    private static final int PLAYER_X = Gdx.graphics.getWidth() * 3/4;
    private static final int ENEMIES_X = Gdx.graphics.getWidth() * 1/4;
    private static final int BASE_Y = Gdx.graphics.getHeight() / 2;

    public BattleScreen(Player player, Array<Enemy> enemies) {
        this.batch = new SpriteBatch();
        this.battleSystem = new BattleSystem(player, enemies);
        this.targetSelector = new TargetSelector();
        this.magicMenu = new BattleMagicMenu();

        if (player.getCharacter().getGrimoire() != null) {
            magicMenu.setMagics(player.getCharacter().getGrimoire().getAvailableSpells());
        }
    }

    @Override
    public void render(float delta) {
        clearScreen();
        renderBattleEntities();
        battleSystem.update(delta);
        renderHUD();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(BattleHUD.NES_BLACK.r, BattleHUD.NES_BLACK.g, BattleHUD.NES_BLACK.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void renderBattleEntities() {
        batch.begin();
        // Renderiza o jogador no lado DIREITO
        battleSystem.getPlayer().render(batch,
            PLAYER_X - battleSystem.getPlayer().getBounds().width/2,
            BASE_Y - battleSystem.getPlayer().getBounds().height/2);

        // Renderiza os inimigos no lado ESQUERDO (distribuídos verticalmente)
        Array<Enemy> enemies = battleSystem.getEnemies();
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).render(batch,
                ENEMIES_X - enemies.get(i).getBounds().width/2,
                BASE_Y + (i * 80) - ((enemies.size-1) * 40));
        }
        batch.end();
    }

    private void renderHUD() {
        batch.begin();
        battleSystem.getHud().render(batch);

        switch (battleSystem.getState()) {
            case PLAYER_TARGET_SELECT:
                targetSelector.render(batch, battleSystem.getHud().getFont(),
                    battleSystem.getEnemies().toArray(Enemy.class));
                break;

            case PLAYER_MAGIC_SELECT:
                magicMenu.render(batch, battleSystem.getHud().getFont());
                break;
        }

        batch.end();
    }

    public boolean keyDown(int keycode) {
        switch (battleSystem.getState()) {
            case PLAYER_TURN:
                if (keycode == Input.Keys.UP) {
                    battleSystem.moveSelectionUp();
                } else if (keycode == Input.Keys.DOWN) {
                    battleSystem.moveSelectionDown();
                } else if (keycode == Input.Keys.ENTER) {
                    battleSystem.playerSelectAction(battleSystem.getSelectedOption());
                }
                return true;
            case PLAYER_TARGET_SELECT:
                if (keycode == Input.Keys.UP) {
                    targetSelector.previousTarget(battleSystem.getEnemies().toArray(Enemy.class));
                } else if (keycode == Input.Keys.DOWN) {
                    targetSelector.nextTarget(battleSystem.getEnemies().toArray(Enemy.class));
                } else if (keycode == Input.Keys.ENTER) {
                    battleSystem.playerSelectAction(targetSelector.getSelectedTarget());
                } else if (keycode == Input.Keys.ESCAPE) {
                    battleSystem.setState(BattleSystem.BattleState.PLAYER_TURN);
                }
                return true;
            case PLAYER_MAGIC_SELECT:
                if (keycode == Input.Keys.UP) {
                    magicMenu.previousMagic();
                } else if (keycode == Input.Keys.DOWN) {
                    magicMenu.nextMagic();
                } else if (keycode == Input.Keys.ENTER) {
                    battleSystem.setSelectedMagic(magicMenu.getSelectedMagic());
                    battleSystem.setState(BattleSystem.BattleState.PLAYER_TARGET_SELECT);
                } else if (keycode == Input.Keys.ESCAPE) {
                    battleSystem.setState(BattleSystem.BattleState.PLAYER_TURN);
                }
                return true;
        }
        return false;
    }

    private void handlePlayerTurnInput(int keycode) {
        if (keycode == Input.Keys.DOWN) {
            battleSystem.moveSelectionDown();
        } else if (keycode == Input.Keys.UP) {
            battleSystem.moveSelectionUp();
        } else if (keycode == Input.Keys.ENTER) {
            battleSystem.playerSelectAction(battleSystem.getSelectedOption());
        }
    }


    private void handleTargetSelectInput(int keycode) {
        if (keycode == Input.Keys.DOWN) {
            targetSelector.nextTarget(battleSystem.getEnemies().toArray(Enemy.class));
        } else if (keycode == Input.Keys.UP) {
            targetSelector.previousTarget(battleSystem.getEnemies().toArray(Enemy.class));
        } else if (keycode == Input.Keys.ENTER) {
            battleSystem.playerAttack(targetSelector.getSelectedTarget());
        } else if (keycode == Input.Keys.ESCAPE) {
            battleSystem.setState(BattleSystem.BattleState.PLAYER_TURN);
        }
    }

    private void handleMagicSelectInput(int keycode) {
        if (keycode == Input.Keys.DOWN) {
            magicMenu.nextMagic();
        } else if (keycode == Input.Keys.UP) {
            magicMenu.previousMagic();
        } else if (keycode == Input.Keys.ENTER) {
            battleSystem.setSelectedMagic(magicMenu.getSelectedMagic());
            battleSystem.setState(BattleSystem.BattleState.PLAYER_TARGET_SELECT);
        } else if (keycode == Input.Keys.ESCAPE) {
            battleSystem.setState(BattleSystem.BattleState.PLAYER_TURN);
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        battleSystem.getHud().dispose();
    }
}
