package br.lawtrel.hero.entities.effects;
import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.StatusEffect;

public class BlindEffect extends StatusEffect {
    public BlindEffect(int duration, float missChance) {
        super("Cegueira", "Reduz a precisäo do alvo.", EffectType.DEBUFF, duration, missChance);
    }
    @Override
    public void onApply(Character target, Character caster) {
        super.onApply(target, caster);
        // Em Character.java, você precisaria de um campo como temporaryAccuracyModifier
        // ou uma forma de a lógica de ataque checar se o atacante está cego.
        // target.addTemporaryAccuracyModifier(-this.potency); // Exemplo se precisão for um stat
        System.out.println(target.getName() + " está cego! (Chance de errar aumentada em " + (this.potency * 100) + "%)");
    }
    @Override
    public void onTurnTick(float delta) {
        // Cegueira geralmente não tem efeito de tick, apenas dura.
    }
    @Override
    public void onRemove() {
        super.onRemove();
        // if (affectedCharacter != null) {
        //    affectedCharacter.removeTemporaryAccuracyModifier(-this.potency); // Reverte
        // }
        System.out.println(affectedCharacter.getName() + " não está mais cego.");
    }

}
