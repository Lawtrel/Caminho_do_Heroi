package br.lawtrel.hero.entities.effects;

import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.StatusEffect;

public class AttackBuff extends StatusEffect {
    private int attackIncrease;

    public AttackBuff(int duration, int attackIncreaseAmount) {
        super("Fúria de Batalha", "Aumenta o ataque.", EffectType.BUFF, duration, attackIncreaseAmount);
        this.attackIncrease = attackIncreaseAmount; // potency pode ser usado como o valor do buff
    }
    @Override
    public void onApply(Character target, Character caster) {
        super.onApply(target, caster);
        if (affectedCharacter != null) {
            // Supondo que Character.java tenha como lidar com modificadores temporários
            affectedCharacter.addTemporaryAttackModifier(attackIncrease);
            System.out.println(affectedCharacter.getName() + " teve seu ataque aumentado por " + attackIncrease);
        }
    }

    @Override
    public void onTurnTick(float delta) {
        // Buffs geralmente não fazem nada no tick, apenas contam a duração.
        // A lógica de modificação de stat já foi feita em onApply e será revertida em onRemove.
    }

    @Override
    public void onRemove() {
        if (affectedCharacter != null) {
            affectedCharacter.removeTemporaryAttackModifier(attackIncrease);
            System.out.println("O bônus de ataque de " + affectedCharacter.getName() + " acabou.");
        }
        super.onRemove();
    }
}
