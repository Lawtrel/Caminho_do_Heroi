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
    public final Animation<TextureRegion> fireEffect; // Exemplo de animação de magia

    private final Array<Texture> texturesToDispose = new Array<>();

    public VFX() {
        slashEffect = createSlashAnimation();
        fireEffect = createFireAnimation(); // Vamos manter esta como exemplo para magias
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
            texturesToDispose.add(texture); // Guarda para ser eliminada depois
            frames.add(new TextureRegion(texture));
            pixmap.dispose();
        }
        // A animação dura 0.21 segundos no total (0.07s por frame)
        return new Animation<>(0.07f, frames, Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> createFireAnimation() {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 4; i++) {
            Pixmap pixmap = new Pixmap(48, 48, Pixmap.Format.RGBA8888);
            // Simula um efeito de fogo com um círculo vermelho/laranja
            pixmap.setColor(new Color(1, 0.2f * i, 0, 0.8f));
            pixmap.fillCircle(24, 24, 20 - (i * 4));
            Texture texture = new Texture(pixmap);
            texturesToDispose.add(texture);
            frames.add(new TextureRegion(texture));
            pixmap.dispose();
        }
        return new Animation<>(0.1f, frames, Animation.PlayMode.NORMAL);
    }


    @Override
    public void dispose() {
        for (Texture tex : texturesToDispose) {
            tex.dispose();
        }
        texturesToDispose.clear();
    }
}
