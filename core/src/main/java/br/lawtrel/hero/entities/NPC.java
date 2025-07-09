package br.lawtrel.hero.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class NPC {
    private final float x, y;
    private final Texture sprite;
    private final Rectangle bounds;
    private final Array<String> dialogueLines;
    private final String npcType; // Ex: "villager", "shopkeeper"

    public NPC(Texture sprite, float x, float y, String type) {
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
        this.npcType = type;
        this.dialogueLines = new Array<>();
    }

    public void render(SpriteBatch batch) {
        if (sprite != null) {
            batch.draw(sprite, x, y);
        }
    }

    // Define o diálogo para este NPC
    public void setDialogue(String[] lines) {
        this.dialogueLines.clear();
        this.dialogueLines.addAll(lines);
    }

    public Array<String> getDialogueLines() {
        return dialogueLines;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getNpcType() {
        return npcType;
    }

    public void dispose() {
        if (sprite != null) {
            sprite.dispose();
        }
    }
}
