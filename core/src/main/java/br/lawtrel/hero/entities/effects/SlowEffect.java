package br.lawtrel.hero.entities.effects;

import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.StatusEffect;

public class SlowEffect extends StatusEffect {
    private int speedReduction;

    // A correção garante que 'speedReductionAmount' seja o nome do parâmetro.
    public SlowEffect(int duration, int speedReductionAmount) {
        super("Lentidão", "Reduz a velocidade.", EffectType.DEBUFF, duration, speedReductionAmount);
        this.speedReduction = speedReductionAmount;
    }

    @Override
    public void onApply(Character target, Character caster) {
        super.onApply(target, caster);
        if (affectedCharacter != null) {
            affectedCharacter.addTemporarySpeedModifier(-speedReduction);
            System.out.println(affectedCharacter.getName() + " está mais lento!");
        }
    }

    @Override
    public void onTurnTick(float delta) {
        // Debuffs de stat geralmente não têm efeito de tick.
    }

    @Override
    public void onRemove() {
        if (affectedCharacter != null) {
            affectedCharacter.removeTemporarySpeedModifier(-speedReduction); // Reverte o debuff
            System.out.println("A velocidade de " + affectedCharacter.getName() + " voltou ao normal.");
        }
        super.onRemove();
    }
}
