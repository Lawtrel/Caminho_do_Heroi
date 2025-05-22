package br.lawtrel.hero.entities;

import br.lawtrel.hero.magic.MagicBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
public class EnemyFactory {
    public enum EnemyType {
        GOBLIN, SKELETON, WIZARD, UNDEAD
    }
    public static Enemy createEnemy(EnemyType type, float x, float y) {
        Character character;
        Texture sprite;
        String name;
        Array<Skill> spells = new Array<>();

        switch (type) {
            case GOBLIN:
                character = new CharacterBuilder()
                    .setName("Goblin")
                    .setMaxHp(30).setMaxMP(0)
                    .setAttack(8).setDefense(4)
                    .build();
                sprite = new Texture("enemies/goblin.png");
                break;
            case SKELETON:
                character = new CharacterBuilder()
                    .setName("Esqueleto")
                    .setMaxHp(45).setMaxMP(20)
                    .setAttack(12).setDefense(6)
                    .build();
                sprite = new Texture("enemies/skeleton.png");
                break;
            case WIZARD:
                character = new CharacterBuilder()
                    .setName("Mago Negro")
                    .setMaxHp(60).setMaxMP(50)
                    .setAttack(15).setDefense(8)
                    .build();
                sprite = new Texture("enemies/wizard.png");
                spells.add(new MagicBuilder("FireBall", 10, "Fire").setMagicDMG(15).build());
                break;
            case UNDEAD:
                character = new CharacterBuilder()
                    .setName("Morto-Vivo")
                    .setMaxHp(100).setMaxMP(80)
                    .setAttack(20).setDefense(15)
                    .build();
                sprite = new Texture("enemies/wizard.png");
                spells.add(new MagicBuilder("FireBall", 10, "Fire").setMagicDMG(15).build());
            default:
                character = new CharacterBuilder()
                    .setName("Goblin")
                    .setMaxHp(30).setMaxMP(0)
                    .setAttack(8).setDefense(4)
                    .build();
                sprite = new Texture("enemies/goblin.png");
                break;
        }
        Enemy enemy = new Enemy(character, sprite);
        enemy.setPosition(x,y);
        return  enemy;
    }
}
