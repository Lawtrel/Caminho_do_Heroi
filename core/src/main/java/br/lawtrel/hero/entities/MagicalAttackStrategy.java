package br.lawtrel.hero.entities;

import br.lawtrel.hero.magic.Grimoire;
import br.lawtrel.hero.magic.MagicBuilder;

public class MagicalAttackStrategy  implements CharacterStrategy, Skill{
    @Override
    public void attack(Character self, Character target) {
        use(self, target);
    }

    public void use(Character self, Character target){
        int damage = self.getMagicAttack() - (target.getMagicDefense() / 2);
        target.receiveDamage(Math.max(1, damage));
        System.out.println(self.getName() + " atacou " + target.getName() +
            " com magia, causando " + damage + " de dano mágico!");
    }

    @Override
    public int getMpCost() {
        return 0;
    }

    public MagicBuilder getMpCost(MagicBuilder mP){
        return mP;
    }

    @Override
    public String getName() {
        return "Magic Attack";
    }

    @Override
    public SkillType getType(){return SkillType.MAGIC;}

    @Override
    public void executeBattle(Character actor, Character target) {

    }
}
