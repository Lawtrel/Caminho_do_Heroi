package br.lawtrel.hero.utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * Lida com a lógica de animação de um personagem.
 * Carrega uma spritesheet e gerencia os frames da animação.
 */

public class AnimationComponent implements Disposable{
    private final Animation<TextureRegion> animation;
    private final Texture animationSheet;
    private float stateTime = 0f;

    public AnimationComponent(String sheetPath, int frameCols, int frameRows, float frameDuration) {
        // Carrega a spritesheet como uma Texture
        animationSheet = new Texture(Gdx.files.internal(sheetPath));

        // Usa o método split da TextureRegion para dividir a imagem em um array 2D
        TextureRegion[][] tmp = TextureRegion.split(animationSheet,
            animationSheet.getWidth() / frameCols,
            animationSheet.getHeight() / frameRows);

        // Converte o array 2D em um array 1D de frames
        TextureRegion[] frames = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // Cria a animação com a duração do frame e os frames
        animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP); // A animação repete
    }
    public void update(float delta) {
        stateTime += delta;
    }

    /**
     * Obtém o frame atual da animação.
     * @return A TextureRegion do frame atual.
     */
    public TextureRegion getCurrentFrame() {
        return animation.getKeyFrame(stateTime, true);
    }

    /**
     * Libera os recursos da spritesheet.
     */
    @Override
    public void dispose() {
        if (animationSheet != null) {
            animationSheet.dispose();
        }
    }
}
