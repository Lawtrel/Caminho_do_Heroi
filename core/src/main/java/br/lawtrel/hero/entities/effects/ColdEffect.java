package br.lawtrel.hero.entities.effects;

import br.lawtrel.hero.entities.StatusEffect;

public class ColdEffect extends StatusEffect{
    public ColdEffect(int duration, float missChance) {
        super("Veneno", "Causa dano ao longo do tempo.", EffectType.DOT, duration,missChance);
    }
    @Override
    public void onTurnTick(float delta) {
    }
}
