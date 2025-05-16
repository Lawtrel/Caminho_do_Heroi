package br.lawtrel.hero.magic;

import com.badlogic.gdx.utils.Array;

public class Grimoire{
    public Array<Magics> chooseMagic(){
        //Magias de efeito
        Magics darkness = new MagicBuilder("Escuridão", 5, "Dark")
            .setMagicSTTS("Blind")
            .setTimeSTTS(3)
            .build();

        //Magias de ataque
        Magics damageOrb = new MagicBuilder("Esfera de Dano", 5, "Dark")
            .setMagicDMG(15)
            .build();

        //Magias de efeito e ataque
        Magics iceImpact = new MagicBuilder("Ice Berg", 8, "Water")
            .setMagicDMG(10)
            .setMagicSTTS("Cold")
            .setTimeSTTS(3)
            .build();

        Array<Magics> magicList = new Array<>();
        magicList.add(darkness);
        magicList.add(damageOrb);
        magicList.add(iceImpact);
        return magicList;
    }
}
