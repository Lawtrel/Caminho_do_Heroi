package br.lawtrel.hero.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class VFX implements Disposable {

    public final Animation<TextureRegion> slashEffect;
    private final Array<Texture> texturesToDispose = new Array<>();

    public VFX() {
        // Cria uma animação de corte simples
        slashEffect = createSlashAnimation();
    }

    private Animation<TextureRegion> createSlashAnimation() {
        Array<TextureRegion> frames = new Array<>();

        // Cria 3 frames de um "flash" branco que encolhe
        for (int i = 0; i < 3; i++) {
            int size = 32 - (i * 10);
            Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            texturesToDispose.add(texture); // Guarda a textura para ser disposta depois
            frames.add(new TextureRegion(texture));
            pixmap.dispose();
        }

        // A animação dura 0.2 segundos no total
        return new Animation<>(0.07f, frames, Animation.PlayMode.NORMAL);
    }

    @Override
    public void dispose() {
        for (Texture tex : texturesToDispose) {
            tex.dispose();
        }
        texturesToDispose.clear();
    }
}
