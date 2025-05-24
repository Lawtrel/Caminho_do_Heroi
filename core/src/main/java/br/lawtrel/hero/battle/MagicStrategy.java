package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.StatusEffect;
import br.lawtrel.hero.magic.Magics;
import br.lawtrel.hero.entities.effects.PoisonEffect;
import br.lawtrel.hero.entities.effects.AttackBuff;

public class MagicStrategy implements BattleMagicStrategy {
    @Override
    public void executeMagic(Character actor, Character target, Magics magics){
        // Verifica se o ator tem MP suficiente
        if (actor.getMp() >= magics.getMpCost()) { // getMpCost() vem da interface Skill, implementada por Magics
            actor.useMagicPoints(magics.getMpCost()); // Deduz MP
            System.out.println(actor.getName() + " usou " + magics.getName() + "!");

            // 1. Aplica Dano (se houver)
            if (magics.getMagicDMG() > 0) {
                int damage = calculateBaseMagicDamage(actor, target, magics);
                target.receiveDamage(damage);
                System.out.println("Causou " + damage + " de dano em " + target.getName() + ".");
            } else if (magics.getMagicDMG() < 0) { // Convenção para cura
                int healingAmount = Math.abs(magics.getMagicDMG()); // Cura baseada no "dano negativo"
                // Você pode adicionar lógica de bônus de cura aqui, similar ao dano
                // healingAmount += actor.getMagicAttack() * 0.5f; // Exemplo
                target.heal(healingAmount);
                System.out.println(target.getName() + " foi curado em " + healingAmount + " HP.");
            }

            // 2. Aplica Status Effect (se houver)
            // O metodo createAssociatedStatusEffect agora está em Magics.java
            StatusEffect effectToApply = magics.createAssociatedStatusEffect();

            if (effectToApply != null) {
                target.applyStatusEffect(effectToApply, actor); // Passa o 'actor' como caster
                // A mensagem de aplicação do status será feita dentro de effectToApply.onApply()
                // ou dentro de Character.applyStatusEffect()
            }

            // Se a magia não causa dano nem aplica status (raro, mas possível para magias de utilidade pura)
            if (magics.getMagicDMG() == 0 && effectToApply == null) {
                System.out.println(magics.getName() + " foi conjurada, mas não teve efeito aparente em " + target.getName() + ".");
            }

        } else {
            System.out.println(actor.getName() + " não tem MP suficiente para " + magics.getName() + "!");
        }
    }

    private int calculateBaseMagicDamage(Character actor, Character target, Magics magics) {
        // Fórmula de dano exemplo, pode ser ajustada
        // Aqui, actor.getMagicAttack() já incluiria buffs/debuffs se você implementou os modificadores temporários
        int damage = magics.getMagicDMG() + actor.getMagicAttack() - (target.getMagicDefense() / 2);
        return Math.max(1, damage); // Garante pelo menos 1 de dano
    }
}
