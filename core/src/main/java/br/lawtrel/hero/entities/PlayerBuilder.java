package br.lawtrel.hero.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

//Metodo de Buildar o Personagem(HERO)

public class PlayerBuilder {
    private float x = 100;
    private float y = 100;
    private  float speed = 100;
    private Texture texture;
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;

    public PlayerBuilder setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return  this;
    }

    public  PlayerBuilder setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public PlayerBuilder loadAnimation(String spritePath) {
        texture = new Texture(spritePath);
        TextureRegion[][] frames = TextureRegion.split(texture, 32, 32);

        walkDown = new Animation<>(0.2f, frames[0]);
        walkLeft = new Animation<>(0.2f, frames[1]);
        walkRight = new Animation<>(0.2f, frames[2]);
        walkUp = new Animation<>(0.2f, frames[3]);

        walkUp.setPlayMode(Animation.PlayMode.LOOP);
        walkDown.setPlayMode(Animation.PlayMode.LOOP);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);
        walkRight.setPlayMode(Animation.PlayMode.LOOP);

        return this;
    }

    public  Player build() {
        return new Player(x, y, speed, walkDown, walkLeft, walkRight, walkUp, texture);
    }

    public Texture getTexture() {
        return  texture;
    }
}
