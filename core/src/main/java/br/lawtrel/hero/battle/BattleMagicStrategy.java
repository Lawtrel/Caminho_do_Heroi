package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.magic.Magics;

//Strategia para sistema de batla usando magias
public interface BattleMagicStrategy {
    void executeMagic(Character actor, Character target, Magics magics); //declara que existe  um ator, um alvo e uma magia
}
