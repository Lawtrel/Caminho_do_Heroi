package br.lawtrel.hero.entities;

import br.lawtrel.hero.entities.items.Item;
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
    private boolean moving = false;
    private String currentArea;
    private final Character character;
    private final List<Item> inventory;
    private final Equipment equipment;
    private int money;

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
        this.stateTime = 0;
        this.currentDirection = Direction.DOWN;
        this.currentFrame = walkDown.getKeyFrames()[1];
        this.inventory = new ArrayList<>();
        this.equipment = new Equipment();
        this.money = 0;
    }

    //Atualizar movimentaçao do jogador
    public void update(float delta, boolean up, boolean down, boolean left, boolean right) {
        this.moving = (up || down || left || right);
        stateTime += delta;

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

    public void render(SpriteBatch batch, float x, float y) {
        this.x = x;
        this.y = y;
        batch.draw(currentFrame, x, y);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }


    private Animation<TextureRegion> getCurrentAnimation() {
        switch (currentDirection) {
            case UP: return walkUp;
            case LEFT: return walkLeft;
            case RIGHT: return walkRight;
            case DOWN:
            default: return walkDown;
        }
    }

    public boolean isMoving() {
        return moving;
    }

    // Métodos para equiparv items
    public void addItem(Item item) {
        inventory.add(item);
    }


    public boolean equipItm(Item item) {
        if (!inventory.contains(item)) return false;

        switch(item.getType()) {
            case WEAPON:
                equipment.equipWeapon(item);
                return true;
            case ARMOR:
                equipment.equipArmor(item);
                return true;
            case ACCESSORY:
                equipment.equipAccessory(item);
                return true;
            default:
                return false;
        }
    }

    public Item getEquippedWeapon() {
        return equipment.getWeapon();
    }

    public Item getEquippedArmor() {
        return equipment.getArmor();
    }

    public Item getEquippedAccessory() {
        return equipment.getAccessory();
    }

    public List<Item> getInventory() {
        return new ArrayList<>(inventory);
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int amount) {
        if (amount > 0) {
            this.money += amount;
        }
    }

    public boolean spendMoney(int amount) {
        if (amount > 0 && this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }


    public float getX() { return x; }
    public float getY() { return y; }
    public String getCurrentArea() {
        return currentArea;
    }

    public void setCurrentArea(String area) {
        this.currentArea = area;
    }
    public Character getCharacter() {
        return character;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
