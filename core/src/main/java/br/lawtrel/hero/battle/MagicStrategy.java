package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.magic.Magics;

public class MagicStrategy implements BattleMagicStrategy {
    @Override
    public void executeMagic(Character actor, Character target, Magics magics){
        int dmg = magics.getMagicDMG();//Dano que a magia vai causar
        int costMp = magics.getCostMP();//Custo que a magia tem
        String stts = magics.getMagicSTTS();//Efeito que a magia causa

        //Verificação
        System.out.println("Mana incial: " + actor.getMp());
        System.out.println("Custo da magia selecionada: " + magics.getCostMP());

        if(actor.getMp() >= magics.getCostMP()){
            if(dmg > magics.getMagicDMG()){
                target.receiveDamage(dmg);
                System.out.println(actor.getName() + " atacou " + target.getName() + " usando " + magics.getMagicName() + " causando " + magics.getMagicDMG() + " de dano");
            }if(stts != null){
                System.out.println(actor.getName() + " causou o efeito de " + magics.getMagicSTTS() + " magia irá durar " + magics.getTimeSTTS() + " turno(s)");
            }else{
                System.out.println("A magia não causou nenhum dano e/ou efeito à " + target.getName());
            }
        }else{
            System.out.println(actor.getName() + " está sem mana");
        }
    }
}
