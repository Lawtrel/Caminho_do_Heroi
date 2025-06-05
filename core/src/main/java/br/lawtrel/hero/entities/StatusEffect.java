package br.lawtrel.hero.entities;

import br.lawtrel.hero.battle.BattleSystem;

public abstract class StatusEffect {
    protected String name;
    protected String description;
    protected EffectType type; // BUFF, DEBUFF
    protected int durationTurns; // Duração em turnos
    protected int remainingTurns;
    protected Character affectedCharacter; // Referência ao personagem afetado
    protected Character caster; // Quem aplicou o efeito (opcional, para cálculos de potência)
    protected boolean isActive;
    protected float potency; // Para efeitos que têm magnitude (ex: cura por turno, dano por turno, % de buff)
    public enum EffectType {
        BUFF,      // Aumenta atributos (Ataque+, Defesa+)
        DEBUFF,    // Diminui atributos (Ataque-, Defesa-)
        DOT,       // Damage Over Time (Veneno, Queimadura)
        HOT,       // Heal Over Time (Regeneração)
        CONTROL    // Efeitos de controle (Sono, Paralisia, Silêncio)
    }

    public StatusEffect(String name, String description, EffectType type, int durationTurns, float potency) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.durationTurns = durationTurns;
        this.remainingTurns = durationTurns;
        this.potency = potency;
        this.isActive = false; // Será ativado quando aplicado ao personagem
    }

    public void onApply(Character target, Character caster) {
        this.affectedCharacter = target;
        this.caster = caster; // Guardar o caster pode ser útil para calcular potência baseada nos stats dele
        this.isActive = true;
        this.remainingTurns = this.durationTurns; // Reinicia a duração se reaplicado
        // Exemplo: Se for um buff de ataque, modificar o attackModifier do target aqui
        // System.out.println(target.getName() + " está sob o efeito de " + name);
    }

    /**
     * Chamado no início/fim do turno do personagem afetado, ou a cada tick de tempo.
     * É aqui que DOTs causam dano, HOTs curam, durações são decrementadas.
     */
    public abstract void onTurnTick(float delta);

    /**
     * Chamado quando o efeito é removido (duração acabou ou foi dissipado).
     * Deve reverter quaisquer modificadores de status aplicados em onApply.
     */

    public void onRemove() {
        this.isActive = false;
        // Exemplo: Se for um buff de ataque, reverter o attackModifier do affectedCharacter aqui
        // System.out.println(name + " acabou para " + affectedCharacter.getName());
    }

    /**
     * Decrementa a duração do efeito. Chamado geralmente no final do turno do personagem afetado.
     */
    public void decrementDuration() {
        if (durationTurns > 0) { // Ignora efeitos com duração "infinita" (negativa ou 0)
            remainingTurns--;
            if (remainingTurns <= 0) {
                isActive = false; // Marcar para remoção
            }
        }
    }

    public boolean isFinished() {
        return !isActive || (durationTurns > 0 && remainingTurns <= 0);
    }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public EffectType getType() { return type; }
    public int getRemainingTurns() { return remainingTurns; }
    public boolean isActive() { return isActive; }
    public float getPotency() { return potency; }

    // Opcional: para efeitos que se acumulam (stack)
    // public boolean canStackWith(StatusEffect other) { return false; }

}
