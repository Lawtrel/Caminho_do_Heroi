package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Character;
import com.badlogic.gdx.graphics.Color;

public interface BattleEventCallback {

    void onAttackVFX(Character target);
    void showFloatingText(String text, Character target, Color color);

}
