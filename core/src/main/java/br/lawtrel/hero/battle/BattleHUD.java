package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.Enemy;
import br.lawtrel.hero.entities.Skill;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.Array;


public class BattleHUD {
    // ────────────────────────────────────────────────────────────────────────────
    //  Paleta NES
    // ────────────────────────────────────────────────────────────────────────────
    private static final Color NES_BLUE   = new Color(0.0f, 0.1f, 0.3f, 1);  // fundo da caixa
    private static final Color NES_RED    = new Color(0.8f, 0.2f, 0.2f, 1);
    private static final Color NES_WHITE  = new Color(1f, 1f, 1f, 1);        // borda e texto padrão
    private static final Color NES_YELLOW = new Color(1f, 1f, 0.3f, 1);      // nomes dos personagens
    public  static final Color NES_BLACK  = new Color(0.05f, 0.05f, 0.05f, 1); // fundo principal

    // ────────────────────────────────────────────────────────────────────────────
    //  Layout
    // ────────────────────────────────────────────────────────────────────────────
    private static final int HUD_HEIGHT   = 150;
    private static final int MENU_WIDTH   = 200;
    private static final int STATUS_WIDTH = 200;
    private static final int MARGIN       = 20;

    private final BitmapFont font;
    private final BattleSystem battleSystem;
    private final String[] menuOptions = {"ATACAR", "MAGIA", "ITENS", "FUGIR"};

    private static Texture whitePixel;

    public BattleHUD(BattleSystem battleSystem) {
        this.battleSystem = battleSystem;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        this.font = generator.generateFont(parameter);
        generator.dispose();


        whitePixel = createWhitePixel();
    }

    public void render(SpriteBatch batch) {
        drawBackground(batch);

        drawHeroStatus(batch);
        drawActionMenu(batch);
        drawSubWindow(batch);
        drawBattleMessage(batch);

    }

    private void drawBackground(SpriteBatch batch) {
        batch.setColor(NES_BLACK);
        batch.draw(whitePixel, 0, 0, Gdx.graphics.getWidth(), HUD_HEIGHT);
    }

    private void drawHeroStatus(SpriteBatch batch) {
        Character hero = battleSystem.getPlayer().getCharacter();
        int startX = MARGIN;
        int startY = MARGIN;

        drawNESWindow(batch, startX, startY, STATUS_WIDTH, HUD_HEIGHT - 2 * MARGIN);

        int textX = startX + 15;
        int textY = HUD_HEIGHT - MARGIN - 15;

        font.setColor(NES_WHITE);
        font.draw(batch, hero.getName(), textX, textY);

        font.draw(batch, "HP: " + hero.getHp() + "/" + hero.getMaxHp(), textX, textY - 25);

        if (hero.getMaxMP() > 0) {
            font.draw(batch, "MP: " + hero.getMp() + "/" + hero.getMaxMP(), textX, textY - 50);
        }
    }

    private void drawActionMenu(SpriteBatch batch) {
        int startX = Gdx.graphics.getWidth() - MENU_WIDTH - MARGIN;
        int startY = MARGIN;
        int spacing = 25;

        drawNESWindow(batch, startX, startY, MENU_WIDTH, HUD_HEIGHT - 2 * MARGIN);

        int textX = startX + 15;
        int textY = startY + HUD_HEIGHT - MARGIN - 30;

        for (int i = 0; i < menuOptions.length; i++) {
            font.setColor(i == battleSystem.getSelectedOption() ? NES_YELLOW : NES_WHITE);
            font.draw(batch, (i == battleSystem.getSelectedOption() ? "> " : "  ") + menuOptions[i],
                textX, textY - i * spacing);
        }
    }

    private void drawBattleMessage(SpriteBatch batch) {
        int msgWidth = Gdx.graphics.getWidth() - 2 * MARGIN;
        int msgHeight = 40;
        int startX = MARGIN;
        // Posiciona a mensagem acima da subjanela ou no topo se não houver subjanela
        int startY ;
        if (battleSystem.isSubWindowVisible()) {
            startY = HUD_HEIGHT + 200 + 30; // Subjanela (200) + espaço (30)
        } else {
            startY = Gdx.graphics.getHeight() - msgHeight - MARGIN;
        }
        if (startY < 0) startY = 20;

        drawNESWindow(batch, startX, startY, msgWidth, msgHeight);

        font.setColor(NES_WHITE);
        font.draw(batch, battleSystem.getBattleMessage(), startX + 10, startY + msgHeight - 10);
    }

    private void drawSubWindow(SpriteBatch batch) {
        if (!battleSystem.isSubWindowVisible()) return;

        int width = Math.min(400, Gdx.graphics.getWidth() - 40);
        int height = 200;

        int y = HUD_HEIGHT + 20;
        int x = (Gdx.graphics.getWidth() - width) / 2; // Centralizado

        if (y + height > Gdx.graphics.getHeight()) {
            y = Gdx.graphics.getHeight() - height - 20;
        }

        drawWindowBoxWithShadow(batch, x, y, width, height);

        // Calcula posição do conteúdo
        int contentX = x + 20;
        int contentY = y + height - 30;

        switch (battleSystem.getState()) {
            case PLAYER_TARGET_SELECT:
                drawTargetSelection(batch, contentX, contentY);
                break;
            case PLAYER_MAGIC_SELECT:
                drawMagicSelection(batch, contentX, contentY);
                break;
            case PLAYER_ITEM_SELECT:
                drawItemSelection(batch, contentX, contentY);
                break;
        }
    }

    private void drawTargetSelection(SpriteBatch batch, int x, int y) {
        font.setColor(NES_YELLOW);
        font.draw(batch, "SELECIONE O ALVO", x, y + 25);

        Array<Enemy> enemies = battleSystem.getEnemies();
        int selectedIndex = battleSystem.getTargetSelector().getSelectedTarget(); // Corrigido para getSelectedTarget()

        for (int i = 0; i < enemies.size; i++) {
            Enemy enemy = enemies.get(i);
            boolean isSelected = (i == selectedIndex);
            String text = (isSelected ? "> " : "  ") + enemy.getName() + " (HP: " + enemy.getCharacter().getHp() + ")";
            font.setColor(isSelected ? NES_YELLOW : NES_WHITE);
            font.draw(batch, text, x, y - (i * 25));
        }
    }

    private void drawMagicSelection(SpriteBatch batch, int x, int y) {
        font.setColor(NES_YELLOW);
        font.draw(batch, "SELECIONE A MAGIA", x, y + 25);

        Array<Skill> magics = battleSystem.getPlayer().getCharacter().getGrimoire().getAvailableSpells();
        Skill selectedMagic = battleSystem.getMagicMenu().getSelectedMagic();

        for (int i = 0; i < magics.size; i++) {
            Skill magic = magics.get(i);
            boolean isSelected = magic == selectedMagic; // Compara objetos Skill diretamente
            String text = (isSelected ? "> " : "  ") + magic.getName() + " (" + magic.getMpCost() + " MP)";
            font.setColor(isSelected ? NES_YELLOW : NES_WHITE);
            font.draw(batch, text, x, y - (i * 25));
        }
    }

    private void drawItemSelection(SpriteBatch batch, int x, int y) {
        font.setColor(NES_YELLOW);
        font.draw(batch, "SELECIONE O ITEM", x, y + 25);
    }

    private void drawWindowBoxWithShadow(SpriteBatch batch, int x, int y, int width, int height) {
        Color shadowColor = new Color(0, 0, 0, 0.4f);
        batch.setColor(shadowColor);
        batch.draw(whitePixel, x + 4, y - 4, width, height);

        drawNESWindow(batch, x, y, width, height);
    }

    private void drawNESWindow(SpriteBatch batch, int x, int y, int width, int height) {
        // Fundo azul escuro
        batch.setColor(NES_BLUE);
        batch.draw(whitePixel, x, y, width, height);

        // Borda branca
        batch.setColor(NES_WHITE);
        int thickness = 2;
        batch.draw(whitePixel, x, y, width, thickness); // baixo
        batch.draw(whitePixel, x, y + height - thickness, width, thickness); // topo
        batch.draw(whitePixel, x, y, thickness, height); // esquerda
        batch.draw(whitePixel, x + width - thickness, y, thickness, height); // direita
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
