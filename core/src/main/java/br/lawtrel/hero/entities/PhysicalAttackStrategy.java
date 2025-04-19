package br.lawtrel.hero.entities;

public class PhysicalAttackStrategy implements CharacterStrategy {
    @Override
    public void attack(Character self, Character target) {
        int damage = self.attack - target.defense / 2;
        target.receiveDamage(damage);
        System.out.println(self.getName() + " atacou fisicamente " + target.getName() + " e causou " + damage + " de dano!");
    }
}
