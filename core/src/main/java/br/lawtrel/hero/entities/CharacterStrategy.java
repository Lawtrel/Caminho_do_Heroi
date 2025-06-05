package br.lawtrel.hero.entities;

import br.lawtrel.hero.battle.BattleStrategy;

public interface CharacterStrategy extends BattleStrategy {
    void attack(Character self, Character target);

    Skill.SkillType getType();
}
