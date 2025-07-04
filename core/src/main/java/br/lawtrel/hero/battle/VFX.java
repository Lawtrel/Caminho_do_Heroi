package br.lawtrel.hero.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class VFX implements Disposable {

    // Um mapa para guardar todas as nossas animações por um nome (chave)
    private final ObjectMap<String, Animation<TextureRegion>> effects;
    private final Array<Texture> texturesToDispose;

    public VFX() {
        this.effects = new ObjectMap<>();
        this.texturesToDispose = new Array<>();

        // Carrega as folhas de sprites
        Texture magicSheet = new Texture(Gdx.files.internal("effects/magic_effects.png"));
        Texture weaponSheet = new Texture(Gdx.files.internal("effects/weapon_effects.png"));
        texturesToDispose.add(magicSheet);
        texturesToDispose.add(weaponSheet);

        // --- Extrai e armazena as animações ---

        // Efeito de Corte (da weapon_effects.png)
        // Usaremos a 5ª linha, que parece ser um "critical hit"
        // Coordenadas (x,y), tamanho de cada frame (largura, altura), nº de frames, duração de cada frame
        effects.put("slash_critical", createAnimationFromSheet(weaponSheet, 0, 384, 96, 96, 6, 0.07f));

        // Magia de Fogo (da magic_effects.png)
        // Primeira linha, "Fire"
        effects.put("fire", createAnimationFromSheet(magicSheet, 0, 0, 32, 32, 7, 0.1f));

        // Magia de Gelo (da magic_effects.png)
        // Segunda linha, "Ice"
        effects.put("ice", createAnimationFromSheet(magicSheet, 0, 40, 64, 56, 7, 0.1f));

        // Adicione outras magias e efeitos aqui. Exemplo:
        // effects.put("bolt", createAnimationFromSheet(magicSheet, 0, 152, 48, 48, 7, 0.08f));
    }

    /**
     * Obtém uma animação pelo seu nome (chave).
     * @param key O nome da animação (ex: "fire", "slash_critical")
     * @return A animação correspondente ou null se não for encontrada.
     */
    public Animation<TextureRegion> getEffect(String key) {
        return effects.get(key);
    }

    /**
     * Método auxiliar para criar uma animação a partir de uma secção de um spritesheet.
     */
    private Animation<TextureRegion> createAnimationFromSheet(Texture sheet, int x, int y, int frameWidth, int frameHeight, int frameCount, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            frames.add(new TextureRegion(sheet, x + (i * frameWidth), y, frameWidth, frameHeight));
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.NORMAL);
    }

    @Override
    public void dispose() {
        for (Texture tex : texturesToDispose) {
            tex.dispose();
        }
        texturesToDispose.clear();
    }
}
