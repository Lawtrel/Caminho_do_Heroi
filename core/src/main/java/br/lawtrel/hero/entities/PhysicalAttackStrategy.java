package br.lawtrel.hero.entities;

public class PhysicalAttackStrategy implements CharacterStrategy, Skill {
    @Override
    public void attack(Character self, Character target) {
        use(self, target); // Reutiliza a implementação de Skill
    }
    @Override
    public void use(Character self, Character target) {
        int damage = self.getAttack() - (target.getDefense() / 2);
        target.receiveDamage(Math.max(1, damage));
        System.out.println(self.getName() + " atacou fisicamente " + target.getName() + " e causou " + damage + " de dano!");
    }
    @Override
    public int getMpCost() {
        return 0; // Ataque físico não custa MP
    }

    @Override
    public String getName() {
        return "Ataque Físico";
    }

    @Override
    public SkillType getType() {
        return SkillType.BASIC_ATTACK;
    }

    public static final Skill BASIC_ATTACK = new PhysicalAttackStrategy();
}
