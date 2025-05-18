package br.lawtrel.hero.entities;

import java.util.ArrayList;
import java.util.List;

import br.lawtrel.hero.magic.Grimoire;
import br.lawtrel.hero.magic.Magics;
import com.badlogic.gdx.utils.Disposable;

public class Character implements Disposable {
    // Informações básicas
    private final String name;
    private int level;

    // Atributos primários
    private int maxHp;
    private int hp;
    private int maxMP;
    private int mp;
    private int attack;
    private int defense;
    private int magicAttack;
    private int magicDefense;
    private int speed;
    private int luck;

    // Modificadores de equipamentos
    private int attackModifier;
    private int defenseModifier;
    private int magicAttackModifier;
    private int magicDefenseModifier;
    private int speedModifier;
    private int luckModifier;

    // Progressão
    private int exp;
    private int expToNextLevel;

    //Status e efeitos
    private final List<StatusEffect> activeStatusEffects;
    private ElementalAffinity elementalAffinity;

    //Equipamento e habilidades
    private  Equipment equipment;
    private final List<Skill> learnedSkills;

    //Estrategia
    private CharacterStrategy strategy;
    private Grimoire grimoire;

    //construção do status do Hero
    public Character(String name, int maxHp, int maxMP, int attack, int defense,
                     int magicAttack, int magicDefense, int speed, int luck,
                     CharacterStrategy strategy) {
        this.name = name;
        this.level = 1;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMP = maxMP;
        this.mp = maxMP;
        this.attack = attack;
        this.defense = defense;
        this.magicAttack = magicAttack;
        this.magicDefense = magicDefense;
        this.speed = speed;
        this.luck = luck;
        this.exp = 0;
        this.expToNextLevel = calculateExpToNextLevel();
        this.strategy = strategy != null ? strategy : new PhysicalAttackStrategy();
        this.equipment = new Equipment();
        this.learnedSkills = new ArrayList<>();
        this.activeStatusEffects = new ArrayList<>();
        this.elementalAffinity = ElementalAffinity.NEUTRAL;

        // Habilidade básica para todos os personagens
        this.learnedSkills.add(PhysicalAttackStrategy.BASIC_ATTACK);
        this.grimoire = new Grimoire();

    }

    // --- Sistema de Níveis e Progressão ---
    public void gainExp(int amount) {
        this.exp += amount;
        while (canLevelUp()) {
            levelUp();
        }
    }

    private boolean canLevelUp() {
        return this.exp >= this.expToNextLevel && this.level < 99;
    }

    private void levelUp() {
        this.level++;
        this.exp -= this.expToNextLevel;
        this.expToNextLevel = calculateExpToNextLevel();

        // Aumento de atributos baseado no nível
        this.maxHp += 10 + (int)(Math.random() * 5);
        this.maxMP += 5 + (int)(Math.random() * 2);
        this.attack += 2;
        this.defense += 1;
        this.magicAttack += 2;
        this.magicDefense += 1;
        this.speed += 1;

        // Restaurar HP/MP completamente ao subir de nível
        this.hp = this.maxHp;
        this.mp = this.maxMP;

        // Verificar se aprendeu nova habilidade
        checkNewSkills();
    }

    private int calculateExpToNextLevel() {
        return (int)(100 * Math.pow(1.2, level - 1));
    }

    private void checkNewSkills() {
        // Lógica para aprender habilidades em níveis específicos
        if (level == 3) {
            //learnSkill(Skill.FIRE_SPELL);
        }
        // Adicionar mais habilidades conforme o nível aumenta
    }
    public void learnSpell(Magics spell) {
        grimoire.addSpell(spell);
    }

    // Metodo para usar uma habilidade/magia
    public void useSkill(Skill skill, Character target) {
        //if (skill == null || target == null) return;
        if (skill.getType() == Skill.SkillType.BASIC_ATTACK) {
            skill.use(this, target);
        } else if (this.mp >= skill.getMpCost()) {
            skill.use(this, target);
            this.mp -= skill.getMpCost();

        }

        if (!learnedSkills.contains(skill)) {
            System.out.println("Habilidade não aprendida: " + skill.getName());
            return;
        }

        if (mp >= skill.getMpCost()) {
            skill.use(this, target);
            useMagicPoints(skill.getMpCost());
        } else {
            System.out.println("MP insuficiente para usar " + skill.getName());
        }
    }


    public void castSpell(String spellName, Character target) {
        Skill spell = grimoire.getSpell(spellName);
        if (spell != null) {
            useSkill(spell, target);
        }
    }


    // --- Sistema de Combate ---


    public void performAttack(Character target) {
        strategy.attack(this, target);
    }

    //Metodo para receber dano
    public void receiveDamage(int dmg) {
        hp -= Math.max(0, dmg);
        if (hp < 0) hp = 0;
    }

    //Metodo que  utiliza os pontos de magia
    public void useMagicPoints(int mana){
        mp -= Math.max(0, mana);
        if (mp < 0) mp = 0; //verifica se o valor de mp é menor que zero para iguala-lo a zero
    }

    public void restoreMp(int amount) {
        this.mp = Math.min(this.maxMP, this.mp + amount);
    }

    //Metodo para retornar se character está vivo ou não
    public boolean isAlive() {
        return hp > 0;
    }

    public void heal(int amount) {
        this.hp = Math.min(this.maxHp, this.hp + amount);
    }

    // --- Sistema de Status ---
    public void applyStatusEffect(StatusEffect effect) {
        if (!isImmuneTo(effect)) {
            activeStatusEffects.add(effect);
        }
    }
    public boolean isImmuneTo(StatusEffect effect) {
        // Implementação básica - pode ser expandida com sistema de imunidades
        return false;

        // Exemplo de implementação mais completa:
        // return this.immunities.contains(effect.getType());
    }

    public void removeStatusEffect(StatusEffect effect) {
        activeStatusEffects.remove(effect);
    }

    public void clearStatusEffects() {
        activeStatusEffects.clear();
    }

    public boolean hasStatusEffect(StatusEffect effect) {
        return activeStatusEffects.contains(effect);
    }

    public void updateStatusEffects() {
        activeStatusEffects.forEach(effect -> effect.apply(this));
        activeStatusEffects.removeIf(StatusEffect::isExpired);
    }

    // --- Sistema de Equipamentos ---
    public void applyEquipmentBonuses(int attackBonus, int defenseBonus,
                                      int magicAttackBonus, int magicDefenseBonus,
                                      int speedBonus, int luckBonus) {
        this.attackModifier = attackBonus;
        this.defenseModifier = defenseBonus;
        this.magicAttackModifier = magicAttackBonus;
        this.magicDefenseModifier = magicDefenseBonus;
        this.speedModifier = speedBonus;
        this.luckModifier = luckBonus;
    }

    // --- Getters para atributos finais (com modificadores) ---
    public int getAttack() { return attack + attackModifier; }
    public int getDefense() { return defense + defenseModifier; }
    public int getMagicAttack() { return magicAttack + magicAttackModifier; }
    public int getMagicDefense() { return magicDefense + magicDefenseModifier; }
    public int getSpeed() { return speed + speedModifier; }
    public int getLuck() { return luck + luckModifier; }


    // --- Getters básicos ---
    public String getName() { return name; }
    public int getLevel() {return level;}
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMaxMP() { return maxMP; }
    public int getMp() { return mp; }
    public int getExp() { return exp;}
    public int getExpToNextLevel() { return expToNextLevel; }
    public List<Skill> getLearnedSkills() { return new ArrayList<>(learnedSkills); }
    public Equipment getEquipment() { return equipment; }

    public boolean learnSkill(Skill skill) {
        if (!learnedSkills.contains(skill)) {
            learnedSkills.add(skill);
            return true;
        }
        return false;
    }

    //Metodo para verificar se uma habilidade está aprendida
    public boolean hasSkill(Skill skill) {
        return learnedSkills.contains(skill);
    }
    public Grimoire getGrimoire() {
        return grimoire;
    }

    @Override
    public void dispose() {

    }

    public void setElementalAffinity(ElementalAffinity elementalAffinity) {
    }

    // --- Enums internos ---
    public enum ElementalAffinity {
        FIRE, ICE, LIGHTNING, EARTH, WIND, WATER, LIGHT, DARK, NEUTRAL
    }
}
