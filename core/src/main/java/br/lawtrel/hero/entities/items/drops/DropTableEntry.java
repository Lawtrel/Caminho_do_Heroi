package br.lawtrel.hero.entities.items.drops;


public class DropTableEntry {
    private final String itemId; // O item que pode ser dropado
    private final float dropChance; // Probabilidade de 0.0 (0%) a 1.0 (100%)

    public DropTableEntry(String  itemId, float dropChance) {
        this.itemId = itemId;
        // Garante que a chance esteja entre 0 e 1
        this.dropChance = Math.max(0.0f, Math.min(1.0f, dropChance));
    }

    public String  getItemId() {
        return itemId;
    }

    public float getDropChance() {
        return dropChance;
    }
}
