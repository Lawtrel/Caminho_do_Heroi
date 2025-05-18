package br.lawtrel.hero.entities;


public class Item {
    public enum Type { WEAPON, ARMOR, ACCESSORY, CONSUMABLE }

    private final String id;
    private final String name;
    private final Type type;
    private final int attackBonus;
    private final int defenseBonus;
    private final int magicAttackBonus;

    public Item(String id, String name, Type type, int attackBonus, int defenseBonus, int magicAttackBonus) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.magicAttackBonus = magicAttackBonus;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public Type getType() { return type; }
    public int getAttackBonus() { return attackBonus; }
    public int getDefenseBonus() { return defenseBonus; }
    public int getMagicAttackBonus() { return magicAttackBonus; }
}
