package br.lawtrel.hero.screens;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.battle.BattleEventCallback;
import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.Enemy;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.Skill;
import br.lawtrel.hero.magic.Magics;
import br.lawtrel.hero.utils.BackgroundManager;
import br.lawtrel.hero.battle.BattleSystem;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import br.lawtrel.hero.battle.VFX;
import br.lawtrel.hero.battle.VisualEffect;
import br.lawtrel.hero.battle.FloatingText;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import br.lawtrel.hero.entities.items.Item;
import java.util.List;

public class BattleScreen implements Screen, InputProcessor, BattleEventCallback {
    private final Hero game;
    private final BattleSystem battleSystem;
    private final SpriteBatch batch;
    private final BackgroundManager bgManager;
    protected final Player player;
    private final VFX vfx;
    private final Array<VisualEffect> activeEffects;
    private final Array<FloatingText> floatingTexts;

    // Posições de renderização
    private static final int PLAYER_X = Gdx.graphics.getWidth() * 3/4;
    private static final int ENEMIES_X = Gdx.graphics.getWidth() / 4; // Eixo X base para formações inimigas
    private static final int BASE_Y = Gdx.graphics.getHeight() / 2;   // Linha de "chão" de referência para inimigos e jogador

    private static final float[] X_OFFSETS_FORMATION_1_ENEMY = {0f}; // Centralizado em ENEMIES_X
    private static final float[] X_OFFSETS_FORMATION_2_ENEMIES = {-60f, 60f}; // Ajuste o espaçamento
    private static final float[] X_OFFSETS_FORMATION_3_ENEMIES = {-90f, 0f, 90f};
    private static final float[] X_OFFSETS_FORMATION_4_ENEMIES = {-120f, -40f, 40f, 120f}; // Ou uma formação 2x2 em X e Y

    private boolean isPlayerAttacking = false;
    private Character attackingCharacter = null;
    private Character attackTarget = null;
    private Skill spellToCast = null;

    // Distância que o personagem se moverá para atacar
    private static final float ATTACK_MOVE_DISTANCE = 50f;
    private boolean victoryMusicStarted;
    private boolean endMusicStarted = false;

    public BattleScreen(Hero game, Player player, Array<Enemy> enemies) {
        this.game = game;
        this.player = player;
        this.player.setInBattleView(true);
        this.bgManager = new BackgroundManager();
        if (player.getCurrentArea() != null) {
            bgManager.setCurrentArea(player.getCurrentArea());
        } else {
            bgManager.setCurrentArea("floresta");
        }
        this.batch = new SpriteBatch();
        this.vfx = new VFX();
        this.activeEffects = new Array<>();
        this.floatingTexts = new Array<>();
        this.battleSystem = new BattleSystem(player, enemies, this);
        Gdx.input.setInputProcessor(this);
    }

    public void showFloatingText(String text, Character target, Color color) {
        float[] dims = getTargetDimensions(target);
        if (dims == null) return;
        float targetWidth = dims[0];

        // Posição inicial do texto no centro do personagem
        float x = target.getOriginalBattleX() + (targetWidth / 2f);
        float y = target.getOriginalBattleY() + target.getRenderScale() * 32; // Um pouco acima do personagem
        floatingTexts.add(new FloatingText(text, x, y, color));
    }

    @Override
    public void render(float delta) {
        clearScreen();
        renderBackground();
        renderBattleEntities();
        renderFloatingTexts();
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
        Texture bgTexture = bgManager.getCurrentBackground();
        if (bgTexture != null) {
            batch.draw(bgTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        batch.end();
    }
    private void renderFloatingTexts() {
        batch.begin();
        BitmapFont font = battleSystem.getHud().getFont();
        for (int i = floatingTexts.size - 1; i >= 0; i--) {
            FloatingText ft = floatingTexts.get(i);
            ft.update(Gdx.graphics.getDeltaTime());
            if (ft.isFinished()) {
                floatingTexts.removeIndex(i);
            } else {
                ft.render(batch, font);
            }
        }
        batch.end();
    }

    private void renderBattleEntities() {
        batch.begin();

        //logica da animaçao
        if (attackingCharacter != null && attackingCharacter.getBattleState() != Character.BattleAnimationState.IDLE) {
            updateAttackerAnimation(Gdx.graphics.getDeltaTime());
        }

        // Renderiza o jogador
        float playerRenderX = PLAYER_X - player.getWidth() / 2;
        float playerRenderY = BASE_Y - player.getHeight() / 2;

        if (attackingCharacter == player.getCharacter()) {
            playerRenderX = player.getCharacter().getOriginalBattleX(); //Usa a posição controlada pela animação
        } else {
            //Guarda a possicao inicial
            if (player.getCharacter().getBattleState() == Character.BattleAnimationState.IDLE && player.getCharacter().getOriginalBattleX() == 0) {
                player.getCharacter().setOriginalBattleX(playerRenderX);
                player.getCharacter().setOriginalBattleY(playerRenderY);
            }
        }

        player.update(Gdx.graphics.getDeltaTime(), false, false, false, false);
        player.render(batch, playerRenderX, playerRenderY);
        // Renderiza os inimigos
        Array<Enemy> enemies = battleSystem.getEnemies();
        int numberOfEnemiesToDisplay = Math.min(enemies.size, 4);

        if (numberOfEnemiesToDisplay == 0) {
            batch.end();
            return;
        }

        // Caso especial: 1 Inimigo Gigante
        if (numberOfEnemiesToDisplay == 1 && enemies.get(0).getCharacter().isLargeEnemy()) {
            Enemy giantEnemy = enemies.get(0);
            Character giantChar = giantEnemy.getCharacter();
            Texture giantSprite = giantEnemy.getSpriteTexture(); // Método adicionado em Enemy.java

            if (giantSprite != null && giantChar.isAlive()) {
                float scale = giantChar.getRenderScale();
                float spriteWidth = giantSprite.getWidth();
                float scaledWidth = spriteWidth * scale;
                float visualAnchorY = giantChar.getVisualAnchorYOffset() * scale;

                // Centraliza o gigante na tela
                float renderX = (Gdx.graphics.getWidth() / 2.5f) - (scaledWidth / 1.1f);
                // Alinha pela base (BASE_Y, que é a mesma para todos os inimigos agora)
                float renderY = BASE_Y + visualAnchorY;

                giantEnemy.render(batch, renderX, (int)renderY, scale); // Passa a escala
            }
        } else { // Formação para 1-4 inimigos (não gigantes ou múltiplos gigantes tratados como normais na formação)
            float[] currentXOffsets;
            // Determina qual array de X offsets usar
            switch (numberOfEnemiesToDisplay) {
                case 1: currentXOffsets = X_OFFSETS_FORMATION_1_ENEMY; break;
                case 2: currentXOffsets = X_OFFSETS_FORMATION_2_ENEMIES; break;
                case 3: currentXOffsets = X_OFFSETS_FORMATION_3_ENEMIES; break;
                case 4: default: currentXOffsets = X_OFFSETS_FORMATION_4_ENEMIES; break;
            }

            for (int i = 0; i < numberOfEnemiesToDisplay; i++) {
                Enemy enemy = enemies.get(i);
                Character enemyChar = enemy.getCharacter();
                Texture enemySprite = enemy.getSpriteTexture();

                if (enemySprite == null || !enemyChar.isAlive()) {
                    continue;
                }

                float scale = enemyChar.getRenderScale();
                float spriteWidth = enemySprite.getWidth();
                float scaledWidth = spriteWidth * scale;
                float visualAnchorY = enemyChar.getVisualAnchorYOffset() * scale;

                // O X é o ENEMIES_X (eixo de formação) + offset da formação - metade da largura escalada para centralizar
                float renderX = ENEMIES_X + (currentXOffsets[i] * scale) - (scaledWidth / 2f) ;
                // O Y é a linha do chão (BASE_Y) + o offset de âncora visual do sprite
                float renderY = BASE_Y + visualAnchorY;
                if (attackingCharacter == enemyChar) {
                    renderX = enemyChar.getOriginalBattleX();
                } else {
                    //Salva a possicao original do inimigo
                    if (enemyChar.getBattleState() == Character.BattleAnimationState.IDLE) {
                        enemyChar.setOriginalBattleX(renderX);
                        enemyChar.setOriginalBattleY(renderY);
                    }
                }
                enemy.render(batch, renderX, (int)renderY, scale);
            }
        }

        //Renderizar efeitos visuais
        for (int i = activeEffects.size - 1; i >= 0; i--) {
            VisualEffect effect = activeEffects.get(i);
            effect.update(Gdx.graphics.getDeltaTime());
            if (effect.isFinished()) {
                activeEffects.removeIndex(i);
            } else {
                effect.render(batch);
            }
        }

        batch.end();
    }

    private void startAttackAnimation(Character attacker, Character target, Skill skill) {
        this.attackingCharacter = attacker;
        this.attackTarget = target;
        this.spellToCast = skill; // Pode ser nulo para ataques físicos

        // Define as posições de início e fim da animação
        float startX = (attacker == player.getCharacter()) ? PLAYER_X - player.getWidth() / 2 : attackingCharacter.getOriginalBattleX();
        float targetX;

        if (attacker == player.getCharacter()) {
            // Jogador se move para a esquerda
            targetX = startX - ATTACK_MOVE_DISTANCE;
        } else {
            // Inimigo se move para a direita
            targetX = startX + ATTACK_MOVE_DISTANCE;
        }

        attacker.setOriginalBattleX(startX);
        attacker.setTargetBattleX(targetX);
        attacker.setAnimationTimer(0);
        attacker.setBattleState(Character.BattleAnimationState.MOVING_FORWARD);
    }

    private void updateAttackerAnimation(float delta) {
        if (attackingCharacter == null) return;

        attackingCharacter.setAnimationTimer(attackingCharacter.getAnimationTimer() + delta);
        float progress = Math.min(1f, attackingCharacter.getAnimationTimer() / Character.getAnimationDuration());

        switch (attackingCharacter.getBattleState()) {
            case MOVING_FORWARD:
                // Interpola a posição para frente
                float currentX = attackingCharacter.getOriginalBattleX() + (attackingCharacter.getTargetBattleX() - attackingCharacter.getOriginalBattleX()) * progress;
                attackingCharacter.setOriginalBattleX(currentX); // Usando originalX para guardar a posição atual da animação

                if (progress >= 1f) {
                    attackingCharacter.setBattleState(Character.BattleAnimationState.ATTACKING);
                    attackingCharacter.setAnimationTimer(0); // Reseta para a próxima fase

                    // --- EXECUTA A AÇÃO NO PICO DA ANIMAÇÃO ---
                    if (attackTarget != null && attackTarget.isAlive()) {
                        if (spellToCast != null) { // É uma magia
                            game.soundManager.playSound("magic_cast");
                            onMagicVFX(attackTarget, spellToCast);
                            attackingCharacter.useSkill(spellToCast, attackTarget); // **CHAMADA DIRETA E CORRETA**
                        } else { // É um ataque físico
                            game.soundManager.playSound("attack_hit");
                            onAttackVFX(attackTarget);
                            attackingCharacter.performAttack(attackTarget);
                        }
                        battleSystem.handleEnemyDefeated(attackTarget);
                    }
                }
                break;

            case ATTACKING:
                if (attackingCharacter.getAnimationTimer() > 0.5f) {
                    attackingCharacter.setBattleState(Character.BattleAnimationState.MOVING_BACK);
                    attackingCharacter.setAnimationTimer(0);
                }
                break;

            case MOVING_BACK:
                // Interpola a posição de volta
                float returnX = attackingCharacter.getTargetBattleX() + (player.getCharacter().getOriginalBattleX() - attackingCharacter.getTargetBattleX()) * progress;
                attackingCharacter.setOriginalBattleX(returnX);

                if (progress >= 1f) {
                    attackingCharacter.setOriginalBattleX(player.getCharacter().getOriginalBattleX()); // Garante a posição final exata
                    attackingCharacter.setBattleState(Character.BattleAnimationState.IDLE);
                    attackingCharacter = null;
                    attackTarget = null;
                    spellToCast = null;

                    // Ação finalizada, avança para o próximo turno
                    if (battleSystem.getState() != BattleSystem.BattleState.VICTORY && battleSystem.getState() != BattleSystem.BattleState.DEFEAT) {
                        battleSystem.advanceTurn();
                    }
                }
                break;
        }
    }

    @Override
    public void onAttackVFX(Character target) {
        if (target == null) return;

        Animation<TextureRegion> slashAnimation = vfx.getEffect("slash_critical");
        if (slashAnimation == null) return;

        float[] dims = getTargetDimensions(target);
        if (dims == null) return;

        float targetWidth = dims[0];
        float targetHeight = dims[1];

        // Calcula a posição para centralizar o efeito no alvo
        float effectX = target.getOriginalBattleX() + (targetWidth / 2f) - (slashAnimation.getKeyFrame(0).getRegionWidth() / 2f);
        float effectY = target.getOriginalBattleY() + (targetHeight / 2f) - (slashAnimation.getKeyFrame(0).getRegionHeight() / 2f);

        activeEffects.add(new VisualEffect(slashAnimation, effectX, effectY));
    }

    private void onMagicVFX(Character target, Skill spell) {
        // Garante que o alvo existe e que a habilidade é uma magia válida
        if (target == null || spell == null || !(spell instanceof Magics)) {
            return;
        }

        Magics magic = (Magics) spell;
        String vfxKey = magic.getVfxKey();

        // Se a magia não tiver uma chave de efeito definida, usa um efeito padrão
        if (vfxKey == null) {
            vfxKey = "slash_critical"; // Efeito genérico para magias sem animação específica
        }

        // Pede a animação à classe VFX
        Animation<TextureRegion> spellAnimation = vfx.getEffect(vfxKey);
        if (spellAnimation == null) {
            Gdx.app.error("BattleScreen", "VFX para a chave '" + vfxKey + "' nao foi encontrado!");
            return; // A animação não existe, então não fazemos nada
        }

        // Obtém as dimensões do alvo para poder centralizar o efeito
        float[] dims = getTargetDimensions(target);
        if (dims == null) return;

        float targetWidth = dims[0];
        float targetHeight = dims[1];

        TextureRegion frame = spellAnimation.getKeyFrame(0);

        // Calcula a posição para centralizar a animação no meio do alvo
        float effectX = target.getOriginalBattleX() + (targetWidth / 2f) - (frame.getRegionWidth() / 2f);
        float effectY = target.getOriginalBattleY() + (targetHeight / 2f) - (frame.getRegionHeight() / 2f);

        // Adiciona o efeito visual à lista para ser renderizado
        activeEffects.add(new VisualEffect(spellAnimation, effectX, effectY));
    }

    private float[] getTargetDimensions(Character target) {
        if (target == player.getCharacter()) {
            // Se o alvo for o jogador, obtém as dimensões da classe Player
            return new float[]{player.getWidth(), player.getHeight()};
        } else {
            // Se for um inimigo, procura na lista de inimigos
            for (Enemy enemy : battleSystem.getEnemies()) {
                if (enemy.getCharacter() == target) {
                    if (enemy.getSpriteTexture() == null) return new float[]{0, 0};
                    float scale = enemy.getCharacter().getRenderScale();
                    float width = enemy.getSpriteTexture().getWidth() * scale;
                    float height = enemy.getSpriteTexture().getHeight() * scale;
                    return new float[]{width, height};
                }
            }
        }
        return null; // Retorna null se o alvo não for encontrado
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

            case ACTION_COMPLETE:
                if (keycode == Input.Keys.ENTER) {
                    // Ao pressionar ENTER, avança para o próximo turno
                    battleSystem.advanceTurn();
                }
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
            game.soundManager.playSound("menu_select");
        } else if (keycode == Input.Keys.UP) {
            battleSystem.moveSelectionUp();
            game.soundManager.playSound("menu_select");
        } else if (keycode == Input.Keys.ENTER) {
            battleSystem.playerSelectAction(battleSystem.getSelectedOption());
            game.soundManager.playSound("menu_confirm");
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
                Enemy selectedEnemy = battleSystem.getEnemies().get(battleSystem.getTargetSelector().getSelectedTarget());

                if (battleSystem.getCurrentAction() == BattleSystem.ActionType.MAGIC) {
                    // Inicia animação de magia
                    startAttackAnimation(player.getCharacter(), selectedEnemy.getCharacter(), battleSystem.getMagicMenu().getSelectedMagic());
                } else {
                    // Inicia animação de ataque físico
                    startAttackAnimation(player.getCharacter(), selectedEnemy.getCharacter(), null);
                }
                // Muda o estado para esperar a animação terminar
                battleSystem.setState(BattleSystem.BattleState.ACTION_COMPLETE);
            }
        } else if (keycode == Input.Keys.ESCAPE) {
            battleSystem.setState(BattleSystem.BattleState.PLAYER_TURN);
        }
    }

    public void handleMagicSelectInput(int keycode) {
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
            Item selectedItem = battleSystem.getItemMenu().getSelectedItem();
            if (selectedItem != null) {
                battleSystem.playerUseItem(selectedItem);
            }
        } else if (keycode == Input.Keys.ESCAPE) {
            battleSystem.setState(BattleSystem.BattleState.PLAYER_TURN);
            battleSystem.setBattleMessage("O que deseja fazer?");
        }
    }

    private void handleBattleEndInput(int keycode) {
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.ESCAPE) {
            Gdx.app.log("BattleScreen", "Fim da batalha");
            battleSystem.resetBattleRewards(); // acabou batalha entao reseta as recompensas

            if (game != null) {
                if (battleSystem.getState() == BattleSystem.BattleState.VICTORY) {
                    if (game.mapManager != null) {
                        game.continueGame(); // Volta para o mapa onde esta
                    } else {
                        Gdx.app.error("BattleScreen", "MapManager é nulo. Não é possível voltar ao mapa.");
                        game.setScreen(new MainMenuScreen(game)); // Exemplo se for um teste isolado e quiser fechar.
                    }
                } else { // Caso derrota
                    game.setScreen(new MainMenuScreen(game)); //volta para tela inicial
                }
            }else {
                Gdx.app.error("BattleScreen", "Instância 'game' (Hero) é nula. Não é possível mudar de tela.");
            }
        }
    }

    private void handleBattleEnd() {
        if (!endMusicStarted) {
            if (battleSystem.getState() == BattleSystem.BattleState.VICTORY && !victoryMusicStarted) {
                game.soundManager.playVictoryMusic();
                victoryMusicStarted = true;
            } else if (battleSystem.getState() == BattleSystem.BattleState.DEFEAT) {
                game.soundManager.playMusic("audio/music/dead_theme.mp3", false);
            }
            endMusicStarted = true;
        }
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
            font.draw(batch, "Game Over", centerX - font.getXHeight() * 3, startY);
        }
        startY -= lineSpacing * 1.5f;
        font.draw(batch, "Pressione ENTER para continuar...", centerX - 150, startY);
        batch.end();
    }


    @Override public void show() {
        Gdx.input.setInputProcessor(this);
        game.soundManager.playBattleMusic();
        // Reseta a flag da música de vitória sempre que uma nova batalha começa
        this.victoryMusicStarted = false;
    }
    @Override public void resize(int width, int height) {batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        player.setInBattleView(false);
        game.soundManager.stopMusic();
    }

    @Override
    public void dispose() {
        batch.dispose();
        bgManager.dispose();
        battleSystem.dispose();
        vfx.dispose();
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
