package br.lawtrel.hero.battle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class VisualEffect {
    private float x, y;
    private float stateTime;
    private Animation<TextureRegion> animation;
    private boolean isFinished = false;

    public VisualEffect(Animation<TextureRegion> animation, float x, float y) {
        this.animation = animation;
        this.x = x;
        this.y = y;
        this.stateTime = 0f;
    }

    public void update(float delta) {
        stateTime += delta;
        if (animation.isAnimationFinished(stateTime)) {
            isFinished = true;
        }
    }

    public void render(SpriteBatch batch) {
        if (!isFinished) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime);
            // Desenha o efeito centralizado na posição X, Y
            batch.draw(currentFrame,
                x - currentFrame.getRegionWidth() / 2f,
                y - currentFrame.getRegionHeight() / 2f);
        }
    }

    public boolean isFinished() {
        return isFinished;
    }
}
