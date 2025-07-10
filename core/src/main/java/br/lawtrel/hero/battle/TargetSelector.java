package br.lawtrel.hero.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import br.lawtrel.hero.entities.Enemy;
import com.badlogic.gdx.utils.Array;

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
        if (enemies.length == 0) return;
        do {
            selectedEnemy = (selectedEnemy + 1) % enemies.length;
        } while (!enemies[selectedEnemy].getCharacter().isAlive());
    }
    public void previousTarget(Enemy[] enemies) {
        if (enemies.length == 0) return;
        int startIndex = selectedEnemy;
        do {
            selectedEnemy = (selectedEnemy - 1 + enemies.length) % enemies.length;
            if (selectedEnemy == startIndex) break;
        } while (!enemies[selectedEnemy].getCharacter().isAlive());
    }

    public int getSelectedTarget() {
        return selectedEnemy;
    }
    public void resetToFirstAlive(Array<Enemy> enemies) {
        if (enemies.isEmpty()) {
            selectedEnemy = -1; // Nenhum alvo válido
            return;
        }
        for (int i = 0; i < enemies.size; i++) {
            if (enemies.get(i).getCharacter().isAlive()) {
                selectedEnemy = i;
                return;
            }
        }
        // Se nenhum inimigo estiver vivo (improvável, mas seguro)
        selectedEnemy = -1;
    }
}
