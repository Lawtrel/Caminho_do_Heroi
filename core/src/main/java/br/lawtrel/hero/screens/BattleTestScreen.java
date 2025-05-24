package br.lawtrel.hero.screens;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.*;
import br.lawtrel.hero.battle.*;
import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.utils.BackgroundManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class BattleTestScreen extends BattleScreen {

    public BattleTestScreen(Hero game) {
        super(game, createTestPlayer(game), createTestEnemies());
    }

    private static Player createTestPlayer(Hero game) {
        Character playerChar = new CharacterBuilder()
            .setName("Herói")
            .setMaxHp(500)
            .setMaxMP(50)
            .setAttack(50)
            .setDefense(10)
            .setSpeed(50)
            .setExpYield(0)
            .setGoldYield(0)
            .setStrategy(new PhysicalAttackStrategy())
            .setIsLargeEnemy(false)
            .setRenderScale(1.0f)
            .setVisualAnchorYOffset(0f)
            .build();

        return new PlayerBuilder()
            .setCharacter(playerChar)
            .loadAnimation("sprites/hero.png")
            .setPosition(0,0)
            .build();

    }


    private static Array<Enemy> createTestEnemies() {
        Array<Enemy> enemies = new Array<>();

        // --- CENÁRIO 1: Um Inimigo Gigante (Exemplo: UNDEAD como gigante) ---
        // Certifique-se que no EnemyFactory, UNDEAD está configurado com setIsLargeEnemy(true)
        // e com uma renderScale e visualAnchorYOffset apropriados.
         enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.UNDEAD, 0, 0));

        // --- CENÁRIO 2: Um Inimigo Normal ---
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.GOBLIN, 0, 0));

        // --- CENÁRIO 3: Dois Inimigos Normais ---
        //enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.GOBLIN, 0, 0));
        //enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.SKELETON, 0, 0));

        // --- CENÁRIO 4: Três Inimigos Normais ---
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.GOBLIN, 0, 0));
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.SKELETON, 0, 0));
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.WIZARD, 0, 0)); // Assegure que WIZARD está configurado

        // --- CENÁRIO 5: Quatro Inimigos Normais ---
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.GOBLIN, 0, 0));
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.SKELETON, 0, 0));
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.WIZARD, 0, 0));
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.GOBLIN, 0, 0)); // Outro Goblin

        // --- CENÁRIO 6: Mais de 4 inimigos (para testar o fallback) ---
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.GOBLIN, 0, 0));
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.SKELETON, 0, 0));
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.WIZARD, 0, 0));
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.GOBLIN, 0, 0));
        // enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.SKELETON, 0, 0));


        // As posições X, Y passadas para createEnemy aqui (0,0) não são tão importantes
        // porque a BattleScreen.renderBattleEntities() vai calcular as posições de renderização.
        return enemies;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
