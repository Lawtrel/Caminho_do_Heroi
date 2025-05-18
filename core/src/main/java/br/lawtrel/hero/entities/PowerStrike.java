package br.lawtrel.hero.entities;

public class PowerStrike implements Skill {
    @Override
    public void use(Character user, Character target) {
        int damage = (int)(user.getAttack() * 1.5) - (target.getDefense() / 2);
        target.receiveDamage(Math.max(1, damage));
    }

    @Override
    public int getMpCost() {
        return 5;
    }

    @Override
    public String getName() {
        return "Golpe Poderoso";
    }

    @Override
    public SkillType getType() {
        return SkillType.PHYSICAL;
    }
}
