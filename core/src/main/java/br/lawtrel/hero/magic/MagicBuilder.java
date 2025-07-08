package br.lawtrel.hero.magic;

public class MagicBuilder {
    public String magicName;
    public int costMP;
    public int magicDMG;
    public String magicSTTS;
    public int timeSTTS;
    public String magicType;
    public float statusPotency;
    public String vfxKey;

    //Metodo para construir a parte obrigatoria das magias
    public MagicBuilder(String magicName, int costMP, String magicType){
        this.magicName = magicName;
        this.costMP = costMP;
        this.magicType = magicType;
    }

    //Metodo para construir parte que dependem das magias
    public MagicBuilder setMagicDMG(int magicDMG){ //dano que a magia inflinge
        this.magicDMG = magicDMG;
        return  this;
    }

    public MagicBuilder setMagicSTTS(String magicSTTS){ //efeito que a magia aplica
        this.magicSTTS = magicSTTS;
        return this;
    }

    public MagicBuilder setTimeSTTS(int timeSTTS){ //tempo que o efeito da magia dura
        this.timeSTTS = timeSTTS;
        return this;
    }
    public MagicBuilder setStatusPotency(float statusPotency) {
        this.statusPotency = statusPotency;
        return this;
    }

    public MagicBuilder setVfxKey(String key) {
        this.vfxKey = key;
        return this;
    }

    public Magics build(){return new Magics(this);}
}
