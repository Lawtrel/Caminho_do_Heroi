package br.lawtrel.hero.magic;

import br.lawtrel.hero.battle.BattleMagicStrategy;
import br.lawtrel.hero.battle.MagicStrategy;
import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.Skill;

public class Magics implements Skill {
    private String magicName; //Define nome da magia
    private int costMP; //Define quantos pontos de magia custa/usa
    private int magicDMG; //Define quanto de dano a magia inflinge
    private String magicSTTS; //Define qual o status da magia
    private int timeSTTS; //Define quanto tempo dura o status
    private String magicType; //Define o tipo da magia

    //Construtor da magia
    public Magics(MagicBuilder builder){
        this.magicName = builder.magicName;
        this.costMP = builder.costMP;
        this.magicDMG = builder.magicDMG;
        this.magicSTTS = builder.magicSTTS;
        this.timeSTTS = builder.timeSTTS;
        this.magicType = builder.magicType;
    }

    @Override
    public void use(Character user, Character target) {
        BattleMagicStrategy strategy = new MagicStrategy();
        strategy.executeMagic(user, target, this);
    }

    @Override
    public int getMpCost() {
        return costMP;
    }

    @Override
    public String getName() {
        return magicName;
    }

    @Override
    public SkillType getType() {
        if (this.magicDMG < 0) {
            return SkillType.HEALING; // caso a skill seja de cura
        } else if (this.magicSTTS != null) {
            return SkillType.SUPPORT; // caso a skill seja de buff
        } else {
            return SkillType.MAGIC;
        }
    }


    //Valores a serem pegos

    public int getMagicDMG() {return magicDMG;}

    public String getMagicSTTS() {return magicSTTS;}

    public int getTimeSTTS() {return timeSTTS;}

    public String getMagicType() {return magicType;}
}
