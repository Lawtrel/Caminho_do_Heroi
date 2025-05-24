package br.lawtrel.hero.entities;

public interface Skill {
    void use(Character user, Character target);
    int getMpCost();
    String getName();
    SkillType getType();


    enum SkillType {
        BASIC_ATTACK,  // Ataque básico padrão
        PHYSICAL,      // Habilidades físicas (especiais)
        MAGIC,         // Habilidades mágicas
        HEALING,       // Habilidades de cura
        SUPPORT,        // Habilidades de suporte (buff/debuff)
        DOT
    }

}
