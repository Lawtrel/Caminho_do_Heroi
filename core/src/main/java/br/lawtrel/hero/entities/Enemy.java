package br.lawtrel.hero.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    private Character character;
    private Texture sprite;
    private float x, y;
    private Array<Skill> spells;

    public Enemy(Character character, Texture sprite) {
        this.character = character;
        this.sprite = sprite;
        this.spells = new Array<>();
    }

    public void setSpells(Array<Skill> spells) {
        this.spells = spells;
    }

    public Array<Skill> getSpells() {
        return spells;
    }

    public String getName() {
        return character.getName();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Rectangle getBounds() {
        if (sprite == null) {
            return new Rectangle(x, y, 0, 0); // Retorna um retângulo vazio se não houver sprite
        }
        // Retorna as dimensões originais da textura. A escala será aplicada ao desenhar.
        return new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
    }
    public Texture getSpriteTexture() { // caso se BattleScreen precisar das dimensões originais
        return sprite;
    }

    public void render(SpriteBatch batch, float screenX, float screenY, float scale) {
        if (sprite != null && character.isAlive()) {
            float scaledWidth = sprite.getWidth() * scale;
            float scaledHeight = sprite.getHeight() * scale;
            // screenX e screenY agora representam o canto inferior esquerdo ONDE o sprite (escalado) deve ser desenhado.
            // O alinhamento pela base já foi feito ao calcular screenY em BattleScreen.
            batch.draw(sprite, screenX, screenY, scaledWidth, scaledHeight);
        }
    }

    public void performAttack(Character target) {
        character.performAttack(target);
    }

    public Character getCharacter() { return character; }
}
