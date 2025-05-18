package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.StatusEffect;
import br.lawtrel.hero.magic.Magics;

public class MagicStrategy implements BattleMagicStrategy {
    @Override
    public void executeMagic(Character actor, Character target, Magics magics){
        if (actor.getMp() >= magics.getMpCost()) {
            actor.useMagicPoints(magics.getMpCost());

            // Aplica dano se houver
            if (magics.getMagicDMG() > 0) {
                int damage = calculateMagicDamage(actor, target, magics);
                target.receiveDamage(damage);
                System.out.println(actor.getName() + " usou " + magics.getName() +
                    " causando " + damage + " de dano!");
            }

            // Aplica status se houver
            if (magics.getMagicSTTS() != null) {
                StatusEffect effect = createStatusEffect(magics);
                target.applyStatusEffect(effect);
                System.out.println(actor.getName() + " aplicou " + effect +
                    " em " + target.getName());
            }
        } else {
            System.out.println(actor.getName() + " não tem MP suficiente!");
        }
    }

    private int calculateMagicDamage(Character actor, Character target, Magics magics) {
        // Cálculo considerando ataque mágico do usuário e defesa mágica do alvo
        return Math.max(1, magics.getMagicDMG() + actor.getMagicAttack() - target.getMagicDefense() / 2);
    }

    private StatusEffect createStatusEffect(Magics magics) {
        // Converte a string do status em um enum StatusEffect
        return StatusEffect.valueOf(magics.getMagicSTTS().toUpperCase());
    }
}
