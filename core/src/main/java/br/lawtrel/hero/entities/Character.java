package br.lawtrel.hero.entities;

import java.util.ArrayList;
import java.util.List;

import br.lawtrel.hero.battle.AttackStrategy;
import br.lawtrel.hero.battle.BattleStrategy;
import br.lawtrel.hero.entities.items.Item;
import br.lawtrel.hero.entities.items.drops.DropTableEntry;
import br.lawtrel.hero.magic.Grimoire;
import br.lawtrel.hero.magic.Magics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.List;

public class Character implements Disposable {

    // Estados de animação para a batalha
    public enum BattleAnimationState {
        IDLE,          // Parado
        MOVING_FORWARD, // Movendo para frente para atacar
        ATTACKING,     // No ponto máximo, executando o ataque
        MOVING_BACK    // Movendo de volta para a posição inicial
    }

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
    private int attackModifier = 0;
    private int defenseModifier = 0;
    private int magicAttackModifier = 0;
    private int magicDefenseModifier = 0;
    private int speedModifier = 0;
    private int luckModifier = 0;

    // Modificadores temporários de stats (para buffs/debuffs)
    private int temporaryAttackModifier = 0;
    private int temporaryDefenseModifier  = 0;
    private int temporarySpeedModifier = 0;

    // Progressão
    private int exp;
    private int expToNextLevel;
    private int expYieldOnDefeat;
    private boolean hasLeveledUpThisGain = false;;
    private int goldYieldOnDefeat;
    private List<DropTableEntry> dropTable;

    //Status e efeitos
    private List<StatusEffect> activeStatusEffects = new ArrayList<>();

    // ... adicione outros conforme necessário (magicAttack, speed, etc.)
    private ElementalAffinity elementalAffinity;

    //Equipamento e habilidades
    private  Equipment equipment;
    private final List<Skill> learnedSkills;

    //Estrategia
    private CharacterStrategy strategy;
    private CharacterStrategy magicStrategy;
    private Grimoire grimoire;

    private boolean isLargeEnemy = false; // caso for inimigo gigante
    private float renderScale = 1.0f;
    private float visualAnchorYOffset = 0f;

    // Campos para Animação de Batalha
    private BattleAnimationState battleState = BattleAnimationState.IDLE;
    private float originalBattleX, originalBattleY;
    private float targetBattleX, targetBattleY;
    private float animationTimer = 0f;
    private static final float ANIMATION_DURATION = 0.3f; // Duração do movimento (em segundos)

    //construção do status do Hero
    public Character(String name, int maxHp, int maxMP, int attack, int defense,
                     int magicAttack, int magicDefense, int speed, int luck, int expYield, int goldYield,
                     CharacterStrategy strategy, CharacterStrategy magicStrategy, boolean isLargeEnemy, float renderScale, float visualAnchorYOffset) {
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
        this.expYieldOnDefeat = expYield;
        this.hasLeveledUpThisGain = false;
        this.goldYieldOnDefeat = goldYield;
        this.strategy = strategy != null ? strategy : new PhysicalAttackStrategy();
        this.magicStrategy = magicStrategy != null ? strategy : new MagicalAttackStrategy();
        this.equipment = new Equipment();
        this.learnedSkills = new ArrayList<>();
        this.activeStatusEffects = new ArrayList<>();
        this.elementalAffinity = ElementalAffinity.NEUTRAL;
        this.dropTable = new ArrayList<>();

        // Habilidade básica para todos os personagens
        this.learnedSkills.add(PhysicalAttackStrategy.BASIC_ATTACK);
        this.grimoire = new Grimoire();

        this.isLargeEnemy = isLargeEnemy;
        this.renderScale = renderScale;
        this.visualAnchorYOffset = visualAnchorYOffset;


    }

    public void updateStatsFromEquipment() {
        if (this.equipment == null) this.equipment = new Equipment();

        this.attackModifier = equipment.getTotalAttackBonus();
        this.defenseModifier = equipment.getTotalDefenseBonus();
        magicAttackModifier = 0;
        magicDefenseModifier = 0;
        speedModifier = 0;
        luckModifier = 0;

        Gdx.app.log("Character", name + " stats updated. ATK Bonus: " + attackModifier + ", DEF Bonus: " + defenseModifier);
    }


    // --- Sistema de Níveis e Progressão ---
    public void gainExp(int amount) {
        this.hasLeveledUpThisGain = false;
        if (amount <= 0) return;

        this.exp += amount;
        System.out.println(this.name + " ganhou " + amount + " EXP. (Total: " + this.exp + "/" + this.expToNextLevel + ")");
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
        if (this.exp < 0) this.exp = 0;
        this.expToNextLevel = calculateExpToNextLevel();
        this.hasLeveledUpThisGain = true;

        System.out.println("LEVEL UP! " + this.name + " alcançou o nível " + this.level + "!");
        // Aumento de atributos baseado no nível
        int oldMaxHp = this.maxHp;
        int oldMaxMp = this.maxMP;
        int oldMaxATK = this.attack;
        int oldMaxDEF = this.defense;
        int oldMaxMA = this.magicAttack;
        int oldMaxMD = this.magicDefense;
        int oldMaxSP = this.speed;
        int oldMaxLU = this.luck;
        this.maxHp += 10 + (int)(Math.random() * 5);
        this.maxMP += 5 + (int)(Math.random() * 2);
        this.attack += 2;
        this.defense += 1;
        this.magicAttack += 2;
        this.magicDefense += 1;
        this.speed += 1;
        this.luck += 2;

        System.out.println("HP Máximo: " + oldMaxHp + " -> " + this.maxHp);
        System.out.println("MP Máximo: " + oldMaxMp + " -> " + this.maxMP);
        System.out.println("ATK Máximo: " + oldMaxATK + " -> " + this.attack);
        System.out.println("DEF Máximo: " + oldMaxDEF + " -> " + this.defense);
        System.out.println("MagATK Máximo: " + oldMaxMA + " -> " + this.magicAttack);
        System.out.println("MagDEF Máximo: " + oldMaxMD + " -> " + this.magicDefense);
        System.out.println("MagDEF Máximo: " + oldMaxSP + " -> " + this.speed);
        System.out.println("MagDEF Máximo: " + oldMaxLU + " -> " + this.luck);

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
    public int receiveDamage(int dmg) {
        int effectiveDamage = Math.max(0, dmg);
        hp -= effectiveDamage;
        if (hp < 0) hp = 0;
        return effectiveDamage;
    }

    //Metodo que  utiliza os pontos de magia
    public void useMagicPoints(int mana){
        mp -= Math.max(0, mana);
        if (mp < 0) mp = 0; //verifica se o valor de mp é menor que zero para iguala-lo a zero
    }



    //Metodo para retornar se character está vivo ou não
    public boolean isAlive() {
        return hp > 0;
    }

    public int heal(int amount) {
        if (amount < 0) return 0;
        int oldHp = this.hp;
        this.hp = Math.min(this.maxHp, this.hp + amount);
        return this.hp - oldHp;
    }
    public void restoreMp(int amount) {
        if (amount < 0 ) return;
        this.mp = Math.min(this.maxMP, this.mp + amount);
    }


    // --- Sistema de Status ---
    public void applyStatusEffect(StatusEffect effectInstance, Character caster) {
        if (effectInstance == null || isImmuneTo(effectInstance)) { // isImmuneTo precisaria ser implementado
            return;
        }
        // Lógica de Stacking (opcional, exemplo simples: reiniciar duração)
        for (StatusEffect existingEffect : activeStatusEffects) {
            if (existingEffect.getName().equals(effectInstance.getName())) {
                // Efeito já existe, reinicia a duração e talvez a potência
                existingEffect.onRemove(); // Remove o antigo para limpar seus efeitos
                activeStatusEffects.remove(existingEffect);
                break; // Remove apenas um e adiciona o novo
            }
        }
        activeStatusEffects.add(effectInstance);
        effectInstance.onApply(this, caster); // Chama o onApply do efeito específico
    }
    public boolean isImmuneTo(StatusEffect effect) {
        // Implementação básica - pode ser expandida com sistema de imunidades
        return false;

        // Exemplo de implementação mais completa:
        // return this.immunities.contains(effect.getType());
    }

    public void updateStatusEffectsOnTurnStart(float delta) { // Ou OnTurnEnd
        List<StatusEffect> toRemove = new ArrayList<>();
        for (StatusEffect effect : activeStatusEffects) {
            if (effect.isActive()) {
                effect.onTurnTick(delta); // Aplica efeitos de tick (DOT, HOT)
                effect.decrementDuration(); // Decrementa a duração para todos os efeitos baseados em turno
                if (effect.isFinished()) {
                    toRemove.add(effect);
                }
            } else {
                toRemove.add(effect);
            }
        }

        for (StatusEffect effect : toRemove) {
            effect.onRemove(); // Garante que os efeitos sejam revertidos
            activeStatusEffects.remove(effect);
        }
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

    public void addTemporaryAttackModifier(int amount) {
        this.temporaryAttackModifier += amount;
    }

    public void removeTemporaryAttackModifier(int amount) {
        this.temporaryAttackModifier -= amount; // Reverte o buff/debuff específico
    }


    // --- Getters para atributos finais (com modificadores) ---
    public int getAttack() { return attack + attackModifier + temporaryAttackModifier; } // Faça o mesmo para outros stats (getDefense, getMagicAttack, etc.)
    public int getDefense() { return defense + defenseModifier; }
    public int getMagicAttack() { return magicAttack + magicAttackModifier; }
    public int getMagicDefense() { return magicDefense + magicDefenseModifier; }
    public int getSpeed() { return speed + speedModifier + temporarySpeedModifier; }
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

    public void setElementalAffinity(ElementalAffinity elementalAffinity) {
    }

    public int getExpYieldOnDefeat() {
        return expYieldOnDefeat;
    }

    public int getGoldYieldOnDefeat() {
        return goldYieldOnDefeat;
    }

    public boolean didLevelUpThisGain() {
        return hasLeveledUpThisGain;
    }

    public List<DropTableEntry> getDropTable() {
        return dropTable;
    }

    // Metodo para adicionar itens à tabela de drop
    public void addDrop(String itemId, float chance) {
        if (this.dropTable == null) {
            this.dropTable = new ArrayList<>();
        }
        this.dropTable.add(new DropTableEntry(itemId, chance));
    }

    public boolean isLargeEnemy() {
        return isLargeEnemy;
    }

    public float getRenderScale() {
        return renderScale;
    }

    public float getVisualAnchorYOffset() {
        return visualAnchorYOffset;
    }

    public void addTemporarySpeedModifier(int amount) {
        this.temporarySpeedModifier += amount;
    }
    public void removeTemporarySpeedModifier(int amount) {
        this.temporarySpeedModifier -= amount;
    }

    // --- Enums internos ---
    public enum ElementalAffinity {
        FIRE, ICE, LIGHTNING, EARTH, WIND, WATER, LIGHT, DARK, NEUTRAL
    }

    public BattleAnimationState getBattleState() { return battleState; }
    public void setBattleState(BattleAnimationState battleState) { this.battleState = battleState; }
    public float getOriginalBattleX() { return originalBattleX; }
    public void setOriginalBattleX(float originalBattleX) { this.originalBattleX = originalBattleX; }
    public float getOriginalBattleY() { return originalBattleY; }
    public void setOriginalBattleY(float originalBattleY) { this.originalBattleY = originalBattleY; }
    public float getTargetBattleX() { return targetBattleX; }
    public void setTargetBattleX(float targetBattleX) { this.targetBattleX = targetBattleX; }
    public float getTargetBattleY() { return targetBattleY; }
    public void setTargetBattleY(float targetBattleY) { this.targetBattleY = targetBattleY; }
    public float getAnimationTimer() { return animationTimer; }
    public void setAnimationTimer(float animationTimer) { this.animationTimer = animationTimer; }
    public static float getAnimationDuration() { return ANIMATION_DURATION; }

    @Override
    public void dispose() {

    }

}
