package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Character;

public interface BattleEventCallback {

    void onAttackVFX(Character target);

}
