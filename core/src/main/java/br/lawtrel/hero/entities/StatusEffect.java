package br.lawtrel.hero.entities;

public enum StatusEffect {
    POISONED("Envenenado", true) {
        @Override
        public void apply(Character character) {
            character.receiveDamage(character.getMaxHp() / 10);
        }
    },
    REGENERATING("Regeneração", false) {
        @Override
        public void apply(Character character) {
            character.heal(character.getMaxHp() / 15);
        }
    };

    private final String displayName;
    private final boolean isNegative;

    StatusEffect(String displayName, boolean isNegative) {
        this.displayName = displayName;
        this.isNegative = isNegative;
    }

    public abstract void apply(Character character);

    public boolean isExpired() {
        // Implementar lógica de duração
        return false;
    }

}
