package br.lawtrel.hero.entities;

public class Character {
    protected String name;
    protected int maxHp, hp, attack, defense, maxMP, mp;
    protected CharacterStrategy strategy;

    //construção do status do Hero
    public Character(String name, int maxHp, int maxMP, int attack, int defense, CharacterStrategy strategy) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMP = maxMP;
        this.mp = maxMP;
        this.attack = attack;
        this.defense = defense;
        this.strategy = strategy;
    }

    public void performAttack(Character target) {
        strategy.attack(this, target);
    }

    public void receiveDamage(int dmg) {
        hp -= Math.max(0, dmg);
        if (hp < 0) hp = 0;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }

    public void setStrategy(CharacterStrategy strategy) {
        this.strategy = strategy;
    }

    public int getMaxHp() { return maxHp; }
    public int getMaxMP() { return maxMP; }
    public int getMp() { return mp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
}
