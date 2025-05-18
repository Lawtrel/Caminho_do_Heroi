package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Character;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BattleHUD {
    // Cores no estilo NES
    private static final Color NES_BLUE = new Color(0.2f, 0.3f, 0.8f, 1);
    private static final Color NES_RED = new Color(0.8f, 0.2f, 0.2f, 1);
    private static final Color NES_WHITE = new Color(0.9f, 0.9f, 0.9f, 1);
    private static final Color NES_YELLOW = new Color(0.9f, 0.9f, 0.2f, 1);
    public static final Color NES_BLACK = new Color(0.1f, 0.1f, 0.1f, 1);

    // Dimensões
    private static final int HUD_HEIGHT = 150;
    private static final int MENU_WIDTH = 200;
    private static final int STATUS_WIDTH = 200;
    private static final int MARGIN = 20;

    private final BitmapFont font;
    private final BattleSystem battleSystem;
    private final String[] menuOptions = {"ATACAR", "MAGIA", "ITENS", "FUGIR"};
    private static Texture whitePixel;

    public BattleHUD(BattleSystem battleSystem) {
        this.battleSystem = battleSystem;
        this.font = new BitmapFont();
        this.font.getData().setScale(1.2f);
        this.whitePixel = createWhitePixel();
    }

    public void render(SpriteBatch batch) {
        // Fundo do HUD
        drawBackground(batch);

        // Status do Herói (lado esquerdo)
        drawHeroStatus(batch);

        // Menu de ações (lado direito)
        drawActionMenu(batch);

        // Mensagem de batalha
        drawBattleMessage(batch);
    }

    private void drawBackground(SpriteBatch batch) {
        // Janela principal
        batch.setColor(NES_BLACK.r, NES_BLACK.g, NES_BLACK.b, 0.8f);
        batch.draw(whitePixel, 0, 0, Gdx.graphics.getWidth(), HUD_HEIGHT);
    }

    private void drawHeroStatus(SpriteBatch batch) {
        Character hero = battleSystem.getPlayer().getCharacter();
        int startX = MARGIN;
        int startY = HUD_HEIGHT - MARGIN;

        // Nome do Herói
        font.setColor(NES_YELLOW);
        font.draw(batch, hero.getName(), startX, startY);

        // HP
        font.setColor(NES_WHITE);
        font.draw(batch, "HP:", startX, startY - 25);
        font.draw(batch, hero.getHp() + "/" + hero.getMaxHp(), startX + 50, startY - 25);

        // Barra de HP
        drawStatusBar(batch, startX, startY - 40, STATUS_WIDTH, 10,
            (float)hero.getHp() / hero.getMaxHp(), NES_RED);

        // MP (se tiver)
        if (hero.getMaxMP() > 0) {
            font.draw(batch, "MP:", startX, startY - 60);
            font.draw(batch, hero.getMp() + "/" + hero.getMaxMP(), startX + 50, startY - 60);
            drawStatusBar(batch, startX, startY - 75, STATUS_WIDTH, 10,
                (float)hero.getMp() / hero.getMaxMP(), NES_BLUE);
        }
    }

    private void drawActionMenu(SpriteBatch batch) {
        int startX = Gdx.graphics.getWidth() - MENU_WIDTH - MARGIN;
        int startY = HUD_HEIGHT - MARGIN;

        // Caixa do menu
        drawNESWindow(batch, startX - 10, startY - 120, MENU_WIDTH, 120);

        // Opções do menu
        for (int i = 0; i < menuOptions.length; i++) {
            boolean isSelected = (i == battleSystem.getSelectedOption());
            font.setColor(isSelected ? NES_YELLOW : NES_WHITE);
            font.draw(batch, (isSelected ? "> " : "  ") + menuOptions[i],
                startX, startY - (i * 25));
        }
    }

    private void drawBattleMessage(SpriteBatch batch) {
        int startX = MARGIN;
        int startY = MARGIN + 30;

        // Caixa de mensagem
        drawNESWindow(batch, startX, startY - 30, Gdx.graphics.getWidth() - 2*MARGIN, 30);

        // Texto da mensagem
        font.setColor(NES_WHITE);
        font.draw(batch, battleSystem.getBattleMessage(), startX + 10, startY);
    }
    private void drawStatusBar(SpriteBatch batch, float x, float y, float width, float height,
                               float percent, Color color) {
        // Fundo
        batch.setColor(NES_BLACK);
        batch.draw(whitePixel, x, y, width, height);

        // Preenchimento
        batch.setColor(color);
        batch.draw(whitePixel, x + 1, y + 1, (width - 2) * Math.max(0, percent), height - 2);
    }


    private void drawNESWindow(SpriteBatch batch, float x, float y, float width, float height) {
        // Fundo
        batch.setColor(NES_BLACK.r, NES_BLACK.g, NES_BLACK.b, 0.8f);
        batch.draw(whitePixel, x, y, width, height);

        // Bordas
        batch.setColor(NES_BLUE);
        batch.draw(whitePixel, x, y, width, 2); // Superior
        batch.draw(whitePixel, x, y + height - 2, width, 2); // Inferior
        batch.draw(whitePixel, x, y, 2, height); // Esquerda
        batch.draw(whitePixel, x + width - 2, y, 2, height); // Direita
        batch.setColor(Color.WHITE);
    }

    private static Texture createWhitePixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void dispose() {
        font.dispose();
        whitePixel.dispose();
    }

    public BitmapFont getFont() {
        return font;
    }
}
