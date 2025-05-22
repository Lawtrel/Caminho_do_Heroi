package br.lawtrel.hero.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import br.lawtrel.hero.entities.Enemy;

public class TargetSelector {
    private int selectedEnemy = 0;

    public void render(SpriteBatch batch, BitmapFont font, Enemy[] enemies, int x, int y ){
        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i].getCharacter().isAlive()) {
                String prefix = (i == selectedEnemy) ? "[X] " : "[ ] ";
                font.draw(batch, prefix + enemies[i].getCharacter().getName(), x, y - (i * 30));
            }
        }
    }

    public void nextTarget(Enemy[] enemies) {
        do {
            selectedEnemy = (selectedEnemy + 1) % enemies.length;
        } while (!enemies[selectedEnemy].getCharacter().isAlive());
    }
    public void previousTarget(Enemy[] enemies) {
        int startIndex = selectedEnemy;
        do {
            selectedEnemy = (selectedEnemy - 1 + enemies.length) % enemies.length;
            if (selectedEnemy == startIndex) break;
        } while (!enemies[selectedEnemy].getCharacter().isAlive());
    }

    public int getSelectedTarget() {
        return selectedEnemy;
    }
}
