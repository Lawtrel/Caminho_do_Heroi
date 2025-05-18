package br.lawtrel.hero.screens;

import br.lawtrel.hero.entities.*;
import br.lawtrel.hero.battle.*;
import br.lawtrel.hero.entities.Character;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class BattleTestScreen extends BattleScreen {
    public BattleTestScreen() {
        super(createTestPlayer(), createTestEnemies());
    }

    private static Player createTestPlayer() {
        Character playerChar = new CharacterBuilder()
            .setName("Herói")
            .setMaxHp(100)
            .setMaxMP(50)
            .setAttack(15)
            .setDefense(10)
            .build();

        return new PlayerBuilder()
            .setCharacter(playerChar)
            .loadAnimation("sprites/hero.png")
            .build();
    }

    private static Array<Enemy> createTestEnemies() {
        Array<Enemy> enemies = new Array<>();
        enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.GOBLIN, 100f, 100f));
        enemies.add(EnemyFactory.createEnemy(EnemyFactory.EnemyType.SKELETON, 100f, 100f));
        return enemies;
    }

    @Override
    public void render(float delta) {
        super.render(delta); // Usa a implementação base
        // Lógica adicional específica do teste pode ser adicionada aqui
    }
}
