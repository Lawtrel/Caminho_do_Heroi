package br.lawtrel.hero.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class FloatingText {
    private String text;
    private float x, y;
    private float lifeTimer;
    private Color color;
    private static final float DURATION = 1.5f; // Duração em segundos
    private static final float SPEED = 60f; // Pixels por segundo que o texto sobe

    public FloatingText(String text, float x, float y, Color color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = new Color(color);
        this.lifeTimer = DURATION;
    }

    public void update(float delta) {
        lifeTimer -= delta;
        y += SPEED * delta; // Move para cima
        color.a = lifeTimer / DURATION; // Desaparece gradualmente
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        font.setColor(color);
        font.draw(batch, text, x, y);
    }

    public boolean isFinished() {
        return lifeTimer <= 0;
    }
}
