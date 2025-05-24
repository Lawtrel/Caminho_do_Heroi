package br.lawtrel.hero.entities;

import br.lawtrel.hero.entities.items.drops.DropTableEntry;
import java.util.ArrayList;
import java.util.List;

public class CharacterBuilder {
    private String name = "Herói";
    private int level = 1;
    private int maxHp = 100;
    private int maxMP = 50;
    private int attack = 10;
    private int defense = 8;
    private int magicAttack = 5;
    private int magicDefense = 5;
    private int speed = 10;
    private int luck = 5;
    private int expYield = 0;
    private int goldYield = 0;

    // Metodo Estrategia e progressao de nivel
    private CharacterStrategy strategy;
    private int startingExp = 0;
    private List<DropTableEntry> dropTableEntries = new ArrayList<>();

    private boolean isLargeEnemy = false;
    private float renderScale = 1.0f;
    private float visualAnchorYOffset = 0f;

    // Sistema de status
    private Character.ElementalAffinity elementalAffinity = Character.ElementalAffinity.NEUTRAL;

    //Habilidades inicias
    private List<Skill> startingSkills = new ArrayList<>();

    public CharacterBuilder() {
        // Habilidade básica para todos os personagens
        startingSkills.add(PhysicalAttackStrategy.BASIC_ATTACK);
    }


    public CharacterBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CharacterBuilder setLevel(int level) {
        this.level = Math.max(1, Math.min(99, level));
        return this;
    }

    public CharacterBuilder setMaxHp(int maxHp) {
        this.maxHp = Math.max(1, maxHp);
        return this;
    }

    public CharacterBuilder setMaxMP(int maxMP) {
        this.maxMP = Math.max(0, maxMP);
        return this;
    }

    public CharacterBuilder setAttack(int attack) {
        this.attack = Math.max(1, attack);
        return this;
    }

    public CharacterBuilder setDefense(int defense) {
        this.defense = Math.max(0, defense);
        return this;
    }

    public CharacterBuilder setMagicAttack(int magicAttack) {
        this.magicAttack = Math.max(0, magicAttack);
        return this;
    }

    public CharacterBuilder setMagicDefense(int magicDefense) {
        this.magicDefense = Math.max(0, magicDefense);
        return this;
    }

    public CharacterBuilder setSpeed(int speed) {
        this.speed = Math.max(1, speed);
        return this;
    }

    public CharacterBuilder setLuck(int luck) {
        this.luck = Math.max(1, luck);
        return this;
    }

    public CharacterBuilder setExpYield(int expYield) {
        this.expYield = expYield;
        return this;
    }
    public CharacterBuilder setGoldYield(int goldYield) {
        this.goldYield = goldYield;
        return this;
    }

    public CharacterBuilder setStrategy(CharacterStrategy strategy) {
        this.strategy = (strategy != null) ? strategy : new PhysicalAttackStrategy();
        return this;
    }

    public CharacterBuilder setElementalAffinity(Character.ElementalAffinity affinity) {
        this.elementalAffinity = (affinity != null) ? affinity : Character.ElementalAffinity.NEUTRAL;
        return this;
    }

    public CharacterBuilder addStartingSkill(Skill skill) {
        if (skill != null && !startingSkills.contains(skill)) {
            startingSkills.add(skill);
        }
        return this;
    }

    public CharacterBuilder setStartingExp(int exp) {
        this.startingExp = Math.max(0, exp);
        return this;
    }
    public CharacterBuilder addDrop(String itemId, float chance) {
        this.dropTableEntries.add(new DropTableEntry(itemId, chance));
        return this;
    }

    public CharacterBuilder setIsLargeEnemy(boolean isLarge) {
        this.isLargeEnemy = isLarge;
        return this;
    }

    public CharacterBuilder setRenderScale(float scale) {
        this.renderScale = scale;
        return this;
    }

    public CharacterBuilder setVisualAnchorYOffset(float offset) {
        this.visualAnchorYOffset = offset;
        return this;
    }

    // --- Métodos de construção rápida para classes ---
    public CharacterBuilder Warrior() {
        return this.setMaxHp(120)
            .setMaxMP(20)
            .setAttack(15)
            .setDefense(12)
            .setMagicAttack(2)
            .setMagicDefense(4)
            .setStrategy(new PhysicalAttackStrategy());
    }

    public CharacterBuilder Mage() {
        return this.setMaxHp(80)
            .setMaxMP(100)
            .setAttack(5)
            .setDefense(6)
            .setMagicAttack(15)
            .setMagicDefense(12)
            .setStrategy(new PhysicalAttackStrategy());
    }

    public Character build() {
        Character character = new Character(name, maxHp, maxMP, attack, defense, magicAttack, magicDefense, speed, luck,
            this.expYield,this.goldYield ,strategy, this.isLargeEnemy, this.renderScale, this.visualAnchorYOffset);

        character.setElementalAffinity(elementalAffinity);

        //adicionar habilidades inicial
        startingSkills.forEach(character::learnSkill);

        //configurar nivel e xp
        if (this.level > 1) {
            for (int i = 1; i < this.level; i++) {
            }
        }
        if (this.startingExp > 0) { // Aplica o XP inicial após setar o nível, se necessário.
            character.gainExp(this.startingExp); // gainExp já lida com level ups
        }


        character.gainExp(startingExp);

        for (DropTableEntry entry : this.dropTableEntries) {
            character.addDrop(entry.getItemId(), entry.getDropChance());
        }

        return character;
    }
}
