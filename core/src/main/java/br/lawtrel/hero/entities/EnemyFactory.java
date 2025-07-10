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
                    .setMaxHp(40).setMaxMP(0)
                    .setAttack(10).setDefense(5)
                    .setSpeed(9)
                    .setStrategy(new PhysicalAttackStrategy())
                    .setExpYield(8)
                    .setGoldYield(10)
                    .addDrop("ITM001", 0.30f) // 30% chance dropar porção pequena
                    .addDrop("ITM002", 0.50f) // 50% chance dropar pele goblim
                    .setIsLargeEnemy(false) // Goblin não é grande
                    .setRenderScale(1.0f)   // Escala normal
                    .setVisualAnchorYOffset(0f) // Assume que o pé do goblin está na base da imagem
                    .build();
                sprite = new Texture("enemies/goblin.png");
                break;
            case SKELETON:
                character = new CharacterBuilder()
                    .setName("Esqueleto")
                    .setMaxHp(65).setMaxMP(0)
                    .setAttack(14).setDefense(8)
                    .setSpeed(7)
                    .setStrategy(new PhysicalAttackStrategy())
                    .setExpYield(12)
                    .setGoldYield(15)
                    .addDrop("ITM003", 0.05f) // 10% chance de dropar "Espada Curta"
                    .setIsLargeEnemy(false)
                    .setRenderScale(1.0f)
                    .setVisualAnchorYOffset(0f)
                    .build();
                sprite = new Texture("enemies/skeleton.png");
                break;
            case WIZARD:
                character = new CharacterBuilder()
                    .setName("Mago Negro")
                    .setMaxHp(56).setMaxMP(40)
                    .setAttack(6).setDefense(7)
                    .setMagicAttack(18)
                    .setSpeed(10)
                    .setStrategy(new MagicalAttackStrategy())
                    .setExpYield(18)
                    .setGoldYield(25)
                    .addDrop("ITM004", 0.20f) // 30% chance de dropar "Éter Pequeno"
                    .setIsLargeEnemy(false)
                    .setRenderScale(1.0f)
                    .setVisualAnchorYOffset(0f)
                    .build();
                sprite = new Texture("enemies/wizard.png");
                spells.add(new MagicBuilder("FireBall", 10, "Fire").setMagicDMG(25).setVfxKey("fire").build());
                spells.add(new MagicBuilder("Raio Congelante", 12, "Thunder").setMagicDMG(20).setVfxKey("thunder").build());
            case UNDEAD:
                character = new CharacterBuilder()
                    .setName("Dragão Vermelho")
                    .setMaxHp(450).setMaxMP(100)
                    .setAttack(35).setDefense(25)
                    .setSpeed(15)
                    .setStrategy(new PhysicalAttackStrategy())
                    .setExpYield(150)
                    .setGoldYield(200)
                    //.addDrop("ITM_DRAGON_SCALE", 1.0f)
                    .setIsLargeEnemy(true)
                    .setRenderScale(3f)
                    .setVisualAnchorYOffset(-10f)// Ex: Se os "pés" do dragão estiverem 10 pixels acima da borda inferior da imagem.
                    .build();
                sprite  = new Texture("enemies/undead.png");
                break;

            case CHAOS:
                character = new CharacterBuilder()
                    .setName("Chaos, o Lorde Demonio")
                    .setMaxHp(1200)
                    .setMaxMP(500)
                    .setAttack(40)
                    .setDefense(30)
                    .setMagicAttack(45)
                    .setMagicDefense(25)
                    .setSpeed(20)
                    .setExpYield(1000)
                    .setGoldYield(5000)
                    .setIsLargeEnemy(true)      // É um inimigo grande
                    .setRenderScale(1f)     // Escala para o fazer parecer maior
                    .setVisualAnchorYOffset(-20f) // Ajusta a posição vertical na batalha
                    .build();
                // Adiciona magias poderosas ao chefe
                character.learnSpell(new MagicBuilder("Meteoro", 40, "Fire").setMagicDMG(80).setVfxKey("fire").build());
                character.learnSpell(new MagicBuilder("Abismo Gélido", 35, "Ice").setMagicDMG(65).setVfxKey("ice").build());
                sprite = new Texture("enemies/Chaos.png"); // Sprite da batalha
                break;
            default:
                character = new CharacterBuilder()
                    .setName("Goblin")
                    .setMaxHp(40).setMaxMP(0)
                    .setAttack(10).setDefense(5)
                    .setExpYield(8)
                    .setGoldYield(10)
                    .addDrop("ITM001", 0.30f) // 50% chance dropar porção pequena
                    .build();
                sprite = new Texture("enemies/goblin.png");
                break;
        }
        Enemy enemy = new Enemy(character, sprite);
        enemy.setSpells(spells);
        enemy.setPosition(x,y);
        return  enemy;
    }
}
