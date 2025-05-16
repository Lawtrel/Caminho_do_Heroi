package br.lawtrel.hero.magic;

public class Magics {
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

    //Valores a serem pegos
    public String getMagicName() {return magicName;}

    public int getCostMP() {return costMP;}

    public int getMagicDMG() {return magicDMG;}

    public String getMagicSTTS() {return magicSTTS;}

    public int getTimeSTTS() {return timeSTTS;}

    public String getMagicType() {return magicType;}
}
