package br.lawtrel.hero.entities;

import br.lawtrel.hero.entities.items.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.ArrayList;
import java.util.List;

public class Player{
    private float x, y;
    private final float speed;
    private final Animation<TextureRegion> walkDown;
    private final Animation<TextureRegion> walkLeft;
    private final Animation<TextureRegion> walkRight;
    private final Animation<TextureRegion> walkUp;
    private final Animation<TextureRegion> animIdleBattle;
    private TextureRegion currentFrame;
    private Texture textureSheet;
    private float stateTime;
    private Direction currentDirection;
    private enum Direction { UP, DOWN, LEFT, RIGHT}
    private boolean moving;
    private final Character character;
    private final List<Item> inventory;
    private int money;
    private boolean isInBattleView;
    private String currentArea;
    private float scale = 1.0f;

    Player(float x, float y, float speed,
           Animation<TextureRegion> walkDown, Animation<TextureRegion> walkLeft,
           Animation<TextureRegion> walkRight, Animation<TextureRegion> walkUp,
           Animation<TextureRegion> animIdleBattle,
           Character character, Texture textureSheet) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.walkDown = walkDown;
        this.walkLeft = walkLeft;
        this.walkRight = walkRight;
        this.walkUp = walkUp;
        this.animIdleBattle = animIdleBattle;
        this.character = character;
        this.textureSheet = textureSheet;
        this.stateTime = 0f;
        this.currentDirection = Direction.DOWN;
        this.moving = false;
        this.isInBattleView = false;
        this.inventory = new ArrayList<>();
        this.money = 0;
        this.currentFrame = walkDown.getKeyFrames()[1];
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
    }

    public void setInBattleView(boolean isInBattleView) {
        this.isInBattleView = isInBattleView;
        this.stateTime = 0f;
    }


    //Atualizar movimentaçao do jogador
    public void update(float delta, boolean up, boolean down, boolean left, boolean right) {
        stateTime += delta;

        if (isInBattleView) {
            // Em batalha, o jogador não se move pelos inputs do mapa.
            // A animação de idle de batalha é selecionada.
            moving = false; // Garante que não está no estado de "moving" do mapa
            if (animIdleBattle != null && animIdleBattle.getKeyFrames().length > 0) {
                currentFrame = animIdleBattle.getKeyFrame(stateTime, true);
            } else if (walkDown != null && walkDown.getKeyFrames().length > 1) { // Fallback
                currentFrame = walkDown.getKeyFrames()[1];
            }
            return;
        }

        // Lógica original de movimento e animação para o mapa
        this.moving = false; // Começa como não se movendo neste frame

        if (up) {
            y += speed * delta;
            currentDirection = Direction.UP;
            this.moving = true;
        } else if (down) {
            y -= speed * delta;
            currentDirection = Direction.DOWN;
            this.moving = true;
        } else if (left) {
            x -= speed * delta;
            currentDirection = Direction.LEFT;
            this.moving = true;
        } else if (right) {
            x += speed * delta;
            currentDirection = Direction.RIGHT;
            this.moving = true;
        }

        Animation<TextureRegion> currentMapAnimation = getCurrentMapAnimation();
        if (currentMapAnimation != null && currentMapAnimation.getKeyFrames().length > 0) {
            if (this.moving) {
                currentFrame = currentMapAnimation.getKeyFrame(stateTime, true);
            } else {
                // Pega o frame parado (ex: segundo frame) da animação da direção atual
                if (currentMapAnimation.getKeyFrames().length > 1) {
                    currentFrame = currentMapAnimation.getKeyFrames()[1];
                } else {
                    currentFrame = currentMapAnimation.getKeyFrame(0); // Fallback para o primeiro frame
                }
            }
        } else if (this.currentFrame == null && walkDown != null && walkDown.getKeyFrames().length > 1) {
            currentFrame = walkDown.getKeyFrames()[1];
        }
    }

    public void render(SpriteBatch batch, float x, float y) {
        batch.draw(currentFrame, x, y, getWidth(), getHeight());
    }

    public void renderAt(SpriteBatch batch, float screenX, float screenY) {
        batch.draw(currentFrame, screenX, screenY, getWidth(), getHeight());
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, getWidth(), getHeight());
    }
    public float getWidth() {
        if (currentFrame == null) return 0;
        return currentFrame.getRegionWidth() * scale;
    }

    public float getHeight() {
        if (currentFrame == null) return 0;
        return currentFrame.getRegionHeight() * scale;
    }

    private Animation<TextureRegion> getCurrentMapAnimation() {
        switch (currentDirection) {
            case UP:
                return walkUp;
            case LEFT:
                return walkLeft;
            case RIGHT:
                return walkRight;
            case DOWN:
            default:
                return walkDown;
        }
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

    public boolean useItem(Item item) {
        if (item == null || item.getType() != Item.Type.CONSUMABLE || !inventory.contains(item)) return false;
        character.heal(item.getHpRecovery());
        character.restoreMp(item.getMpRecovery());
        inventory.remove(item);
        Gdx.app.log("Player", "Usou o item: " + item.getName());
        return true;
    }



    public void equip(Item item) {
        if (item == null || !inventory.contains(item)) return;
        Equipment equipment = character.getEquipment();
        if (equipment == null ) return;
        Item previouslyEquippedItem = null;

        switch (item.getType()) {
            case WEAPON:
                previouslyEquippedItem = equipment.getWeapon();
                equipment.equipWeapon(item);
                break;
            case ARMOR:
                previouslyEquippedItem = equipment.getArmor();
                equipment.equipArmor(item);
                break;
            case ACCESSORY:
                previouslyEquippedItem = equipment.getAccessory();
                equipment.equipAccessory(item);
                break;
            default:
                Gdx.app.log("Player", "Tentativa de equipar item nao equipavel: " + item.getName());
                break;
        }
        inventory.remove(item);
        //Se havia um item equipado antes, ele é devolvido ao inventário
        if (previouslyEquippedItem != null) {
            inventory.add(previouslyEquippedItem);
        }
        //Avisa o Character para recalcular os bônus de status!
        character.updateStatsFromEquipment();
        Gdx.app.log("Player", "Equipou: " + item.getName());
    }

    public Item getEquippedWeapon() {
        return character.getEquipment().getWeapon();
    }

    public Item getEquippedArmor() {
        return character.getEquipment().getArmor();
    }

    public Item getEquippedAccessory() {
        return character.getEquipment().getAccessory();
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
    public boolean removeItem(Item item) {
        return inventory.remove(item);
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
    }
}
