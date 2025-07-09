package br.lawtrel.hero.entities.items;

public class Item {
    public enum Type { WEAPON, ARMOR, ACCESSORY, CONSUMABLE , MATERIAL }

    private final String id;
    private final String name;
    private final Type type;
    private final String description;
    private final int price;

    private final int attackBonus;
    private final int defenseBonus;
    private final int magicAttackBonus;
    private final int magicDefenseBonus;
    private final int hpBonus;
    private final int mpBonus;
    // Para consumíveis
    private final int hpRecovery;
    private final int mpRecovery;
    // private final StatusEffect statusToApply; // Para itens que aplicam status
    // private final StatusEffect statusToCure;  // Para itens que curam status

    public Item(String id, String name, String description, Type type, int price, int attackBonus, int defenseBonus, int magicAttackBonus,
                int magicDefenseBonus, int hpBonus, int mpBonus, int hpRecovery, int mpRecovery) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.magicAttackBonus = magicAttackBonus;
        this.magicDefenseBonus = magicDefenseBonus;
        this.hpBonus = hpBonus;
        this.mpBonus = mpBonus;
        this.hpRecovery = hpRecovery;
        this.mpRecovery = mpRecovery;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Type getType() { return type; }
    public int getPrice() {
        return price;
    }
    public int getAttackBonus() { return attackBonus; }
    public int getDefenseBonus() { return defenseBonus; }
    public int getMagicAttackBonus() { return magicAttackBonus; }
    public int getMagicDefenseBonus() { return magicDefenseBonus; }
    public int getHpBonus() { return hpBonus; }
    public int getMpBonus() { return mpBonus; }
    public int getHpRecovery() { return hpRecovery; }
    public int getMpRecovery() { return mpRecovery; }

    @Override
    public String toString() { // Útil para debug e para a ItemsSection
        return name + " (" + type + ")";
    }

    // Equals e hashCode baseados no ID para garantir unicidade se necessário
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
