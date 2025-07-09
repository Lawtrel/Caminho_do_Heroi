package br.lawtrel.hero.entities;

import br.lawtrel.hero.magic.MagicBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import br.lawtrel.hero.entities.items.ItemFactory;

public class EnemyFactory {
    public enum EnemyType {
        GOBLIN, SKELETON, WIZARD, UNDEAD, CHAOS, GIGANT
    }
    public static Enemy createEnemy(EnemyType type, float x, float y) {
        Character character;
        Texture sprite;
        Array<Skill> spells = new Array<>();

        switch (type) {
            case GOBLIN:
                character = new CharacterBuilder()
                    .setName("Goblin")
                    .setMaxHp(30).setMaxMP(0)
                    .setAttack(8).setDefense(4)
                    .setSpeed(8)
                    .setStrategy(new PhysicalAttackStrategy())
                    //.setStrategy(new MagicalAttackStrategy())
                    .setExpYield(10)
                    .setGoldYield(5)
                    .addDrop("ITM001", 0.50f) // 50% chance dropar porção pequena
                    .addDrop("ITM002", 0.50f) // 25% chance dropar pele goblim
                    .setIsLargeEnemy(false) // Goblin não é grande
                    .setRenderScale(1.0f)   // Escala normal
                    .setVisualAnchorYOffset(0f) // Assume que o pé do goblin está na base da imagem
                    .build();
                sprite = new Texture("enemies/goblin.png");
                break;
            case SKELETON:
                character = new CharacterBuilder()
                    .setName("Esqueleto")
                    .setMaxHp(45).setMaxMP(20)
                    .setAttack(12).setDefense(6)
                    .setSpeed(6)
                    .setStrategy(new PhysicalAttackStrategy())
                    .setExpYield(15)
                    .setGoldYield(8)
                    .addDrop("ITM003", 0.10f) // 10% chance de dropar "Espada Curta"
                    .setIsLargeEnemy(false)
                    .setRenderScale(1.0f)
                    .setVisualAnchorYOffset(0f)
                    .build();
                sprite = new Texture("enemies/skeleton.png");
                break;
            case WIZARD:
                character = new CharacterBuilder()
                    .setName("Mago Negro")
                    .setMaxHp(60).setMaxMP(50)
                    .setAttack(5).setDefense(8)
                    .setMagicAttack(15)
                    .setSpeed(7)
                    .setStrategy(new MagicalAttackStrategy())
                    .setExpYield(20)
                    .setGoldYield(15)
                    .addDrop("ITM004", 0.30f) // 30% chance de dropar "Éter Pequeno"
                    .setIsLargeEnemy(false)
                    .setRenderScale(1.0f)
                    .setVisualAnchorYOffset(0f)
                    .build();
                sprite = new Texture("enemies/wizard.png");
                spells.add(new MagicBuilder("FireBall", 10, "Fire").setMagicDMG(15).build());
                break;
            case UNDEAD:
                character = new CharacterBuilder()
                    .setName("Dragão Vermelho")
                    .setMaxHp(100).setMaxMP(100)
                    .setAttack(25).setDefense(20)
                    .setSpeed(12)
                    .setStrategy(new PhysicalAttackStrategy())
                    .setExpYield(200)
                    .setGoldYield(100)
                    //.addDrop("ITM_DRAGON_SCALE", 1.0f)
                    .setIsLargeEnemy(true)
                    .setRenderScale(3f)
                    .setVisualAnchorYOffset(-10f)// Ex: Se os "pés" do dragão estiverem 10 pixels acima da borda inferior da imagem.
                    .build();
                sprite  = new Texture("enemies/undead.png");
                break;
            default:
                character = new CharacterBuilder()
                    .setName("Goblin")
                    .setMaxHp(30).setMaxMP(0)
                    .setAttack(8).setDefense(4)
                    .setExpYield(10)
                    .setGoldYield(5)
                    .addDrop("ITM001", 0.50f) // 50% chance dropar porção pequena
                    .build();
                sprite = new Texture("enemies/goblin.png");
                break;
        }
        Enemy enemy = new Enemy(character, sprite);
        enemy.setPosition(x,y);
        return  enemy;
    }
}
