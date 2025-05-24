package br.lawtrel.hero.magic;

import br.lawtrel.hero.battle.BattleMagicStrategy;
import br.lawtrel.hero.battle.MagicStrategy;
import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.Skill;
import br.lawtrel.hero.entities.StatusEffect;
import br.lawtrel.hero.entities.effects.AttackBuff;
import br.lawtrel.hero.entities.effects.PoisonEffect;

public class Magics implements Skill {
    private String magicName; //Define nome da magia
    private int costMP; //Define quantos pontos de magia custa/usa
    private int magicDMG; //Define quanto de dano a magia inflinge
    private String magicSTTS; //Define qual o status da magia
    private int timeSTTS; //Define quanto tempo dura o status
    private String magicType; //Define o tipo da magia
    private float statusPotency; // Potência específica do status (ex: dano do veneno, valor do buff)

    //Construtor da magia
    public Magics(MagicBuilder builder){
        this.magicName = builder.magicName;
        this.costMP = builder.costMP;
        this.magicDMG = builder.magicDMG;
        this.magicSTTS = builder.magicSTTS;
        this.timeSTTS = builder.timeSTTS;
        this.magicType = builder.magicType;
        this.statusPotency = builder.statusPotency;
    }

    public StatusEffect createAssociatedStatusEffect() {
        if (this.magicSTTS == null || this.magicSTTS.isEmpty()) {
            return null; // Magia não tem efeito de status
        }
        int potencyAsInt = (int) this.statusPotency;

        switch (this.magicSTTS.toUpperCase()) {
            case "POISON":
                return new PoisonEffect(this.timeSTTS, potencyAsInt); // Duração e dano por turno
            case "ATTACK_UP":
                return new AttackBuff(this.timeSTTS, potencyAsInt); // Duração e quanto aumenta o ataque
            case "BLIND":
                // return new BlindEffect(this.timeSTTS, this.statusPotency); // Duração e chance de errar (statusPotency seria float)
                System.out.println("TODO: Implementar BlindEffect com potência: " + this.statusPotency);
                return null;
            case "COLD":
                // return new ColdEffect(this.timeSTTS, potencyAsInt); // Duração e redução de atributo
                System.out.println("TODO: Implementar ColdEffect com potência: " + this.statusPotency);
                return null;
            default:
                System.out.println("Atenção: Status Effect '" + this.magicSTTS + "' não reconhecido em Magics.createAssociatedStatusEffect()");
                return null;
        }
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
        } else if (this.magicSTTS != null && !this.magicSTTS.isEmpty()) {
            return SkillType.SUPPORT; // caso a skill seja de buff
        } else  if (this.magicDMG > 0){
            return SkillType.MAGIC;
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
