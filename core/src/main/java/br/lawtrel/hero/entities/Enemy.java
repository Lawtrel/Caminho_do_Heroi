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
        return new Rectangle(x, y, 64, 64);
    }

    public void render(SpriteBatch batch, float v, int i) {
        batch.draw(sprite, x, y);
    }

    public void performAttack(Character target) {
        character.performAttack(target);
    }

    public Character getCharacter() { return character; }
}
