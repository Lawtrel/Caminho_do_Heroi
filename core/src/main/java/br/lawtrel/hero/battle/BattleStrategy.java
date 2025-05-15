package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Character;

//Sistema de batalha para ataques fisicos
public interface BattleStrategy {
    void executeBattle(Character actor, Character target); //Declara que existe um ator e um alvo
}
