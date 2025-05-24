package br.lawtrel.hero.entities.effects;

import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.StatusEffect;

public class PoisonEffect extends StatusEffect {
    public PoisonEffect(int duration, float damagePerTurn) {
        super("Veneno", "Causa dano ao longo do tempo.", EffectType.DOT, duration, damagePerTurn);
    }

    @Override
    public void onApply(Character target, Character caster) {
        super.onApply(target, caster);
        // Nenhuma modificação de stat imediata para veneno simples
        System.out.println(target.getName() + " foi envenenado!");
    }

    @Override
    public void onTurnTick(float delta) {
        if (isActive && affectedCharacter != null && affectedCharacter.isAlive()) {
            int damage = (int) (potency); // Ou potency * caster.getMagicAttack() * 0.1f, por exemplo
            affectedCharacter.receiveDamage(damage); // Usa o metodo existente em Character
            System.out.println(affectedCharacter.getName() + " sofreu " + damage + " de dano de veneno.");
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        System.out.println("O veneno em " + affectedCharacter.getName() + " passou.");
    }
}
