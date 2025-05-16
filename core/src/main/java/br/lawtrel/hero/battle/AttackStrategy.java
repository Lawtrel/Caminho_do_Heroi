package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Character;

public class AttackStrategy implements BattleStrategy {
    @Override
    public void executeBattle(Character actor, Character target){
        int dmg = actor.getAttack() - target.getDefense(); //Calcula o valor do dano infligido
        if(dmg > 0){
            target.receiveDamage(dmg);
            System.out.println(actor.getName() + " atacou " + target.getName() + " causando " + actor.getAttack() + " de ataque");
        }
        else{
            System.out.println("Nenhum dano foi causado à " + target.getName());
        }
    }
}
