package br.lawtrel.hero.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.ArrayList;
import java.util.List;

public class Player{
    private float x, y;
    private float speed;
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;
    private TextureRegion currentFrame;
    private Texture texture;
    private float stateTime;
    private Direction currentDirection;
    private enum Direction { UP, DOWN, LEFT, RIGHT}
    private Character character;
    private List<String> inventory = new ArrayList<>();
    private String equippedWeapon;
    private String equippedArmor;
    private String equippedAccessory;

    Player(float x, float y, float speed, Animation<TextureRegion> walkDown,
           Animation<TextureRegion> walkLeft,
           Animation<TextureRegion> walkRight,
           Animation<TextureRegion> walkUp,
           Character character) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.walkUp = walkUp;
        this.walkDown = walkDown;
        this.walkLeft = walkLeft;
        this.walkRight = walkRight;
        this.character = character;
        //this.texture = texture;
        this.stateTime = 0;
        this.currentDirection = Direction.DOWN;
        this.currentFrame = walkDown.getKeyFrames()[1];
    }
    public void update(float delta, boolean up, boolean down, boolean left, boolean right) {
        stateTime += delta;
        boolean moving = false;

        if (up) {
            y += speed * delta;
            currentDirection = Direction.UP;
            moving = true;
        } else if (down) {
            y -= speed * delta;
            currentDirection = Direction.DOWN;
            moving = true;
        } else if (left) {
            x -= speed * delta;
            currentDirection = Direction.LEFT;
            moving = true;
        } else if (right) {
            x += speed * delta;
            currentDirection = Direction.RIGHT;
            moving = true;
        }

        if (moving) {
            currentFrame = getCurrentAnimation().getKeyFrame(stateTime, true);
        } else {
            currentFrame = getCurrentAnimation().getKeyFrames()[1]; // frame parado
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, x, y);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    public float getX() { return x; }
    public float getY() { return y; }

    private Animation<TextureRegion> getCurrentAnimation() {
        switch (currentDirection) {
            case UP: return walkUp;
            case LEFT: return walkLeft;
            case RIGHT: return walkRight;
            case DOWN:
            default: return walkDown;
        }
    }
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    public Character getCharacter() {
        return character;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public void addItem(String item) {
        inventory.add(item);
    }

    public String getEquippedWeapon() {
        return equippedWeapon;
    }

    public String getEquippedArmor() {
        return equippedArmor;
    }

    public String getEquippedAccessory() {
        return equippedAccessory;
    }

    // Métodos para equipar
    public void equipWeapon(String weapon) {
        this.equippedWeapon = weapon;
    }
}
