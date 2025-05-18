package br.lawtrel.hero.entities;

public class MagicalAttackStrategy  implements CharacterStrategy{
    @Override
    public void attack(Character self, Character target) {
        int damage = self.getMagicAttack() - (target.getMagicDefense() / 2);
        target.receiveDamage(Math.max(1, damage));
        System.out.println(self.getName() + " atacou " + target.getName() +
            " com magia, causando " + damage + " de dano mágico!");
    }
}
