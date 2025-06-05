package br.lawtrel.hero.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

//Metodo de Buildar o Personagem(HERO)

public class PlayerBuilder {
    private float x = 100;
    private float y = 100;
    private  float speed = 100;
    private Texture textureSheet;
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;
    private Animation<TextureRegion> animIdleBattle;
    private Character character;

    public PlayerBuilder setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return  this;
    }

    public  PlayerBuilder setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public PlayerBuilder setCharacter(Character character) {
        this.character = character;
        return this;
    }
    public PlayerBuilder loadAnimation(String spritePath) {
        textureSheet  = new Texture(spritePath);
        TextureRegion[][] frames = TextureRegion.split(textureSheet , 32, 32);

        if (frames.length > 0 && frames[0].length > 0) {
            walkDown = new Animation<>(0.2f,frames[0]);
            walkDown.setPlayMode(Animation.PlayMode.LOOP);
        }
        if (frames.length > 1 && frames[1].length > 0) {
            walkLeft = new Animation<>(0.2f, frames[1]);
            walkLeft.setPlayMode(Animation.PlayMode.LOOP);

            animIdleBattle = new Animation<>(0.25f, frames[1]);
            animIdleBattle.setPlayMode(Animation.PlayMode.LOOP);
        }

        if (frames.length > 2 && frames[2].length > 0) {
            walkRight = new Animation<>(0.2f, frames[2]);
            walkRight.setPlayMode(Animation.PlayMode.LOOP);
        }
        if (frames.length > 3 && frames[3].length > 0) {
            walkUp = new Animation<>(0.2f, frames[3]);
            walkUp.setPlayMode(Animation.PlayMode.LOOP);
        }

        if (animIdleBattle == null) {
            animIdleBattle = walkDown;
        }


        return this;
    }

    public  Player build() {
        return new Player(x, y, speed, walkDown, walkLeft, walkRight, walkUp, animIdleBattle, character, textureSheet); //texture
    }
}
