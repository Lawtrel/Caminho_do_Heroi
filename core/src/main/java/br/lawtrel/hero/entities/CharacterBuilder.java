package br.lawtrel.hero.entities;

public class CharacterBuilder {
    private String name;
    private int maxHp;
    private int maxMP;
    private int attack;
    private int defense;
    private CharacterStrategy strategy;

    public CharacterBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CharacterBuilder setMaxHp(int maxHp) {
        this.maxHp = maxHp;
        return this;
    }

    public CharacterBuilder setMaxMP(int maxMP) {
        this.maxMP = maxMP;
        return this;
    }

    public CharacterBuilder setAttack(int attack) {
        this.attack = attack;
        return this;
    }

    public CharacterBuilder setDefense(int defense) {
        this.defense = defense;
        return this;
    }

    public CharacterBuilder setStrategy(CharacterStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public Character build() {
        return new Character(name, maxHp, maxMP, attack, defense, strategy);
    }
}
