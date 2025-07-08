package br.lawtrel.hero.magic;

import br.lawtrel.hero.entities.Skill;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Grimoire {

    private final ObjectMap<String, Skill> spells; // faz buscar das skills/magia pelo nome
    private final Array<Skill> spellsArray; // busca ordenada

    public Grimoire() {
        this.spells = new ObjectMap<>();
        this.spellsArray = new Array<>();
        initializeDefaultSpells();
    }

    private void initializeDefaultSpells() {
        //Magias de fogo
        Magics fire = new MagicBuilder("Fireball", 10, "Fire")
            .setMagicDMG(20)
            .setVfxKey("fire")
            .build();

        //Magias de Terra
        Magics eath = new MagicBuilder("Eathquake", 20, "Eath")
            .setMagicDMG(30)
            .setVfxKey("eath")
            .build();

        //Magias de efeito e ataque
        Magics iceImpact = new MagicBuilder("Ice Berg", 8, "Water")
            .setMagicDMG(15)
            .setMagicSTTS("Cold")
            .setTimeSTTS(3)
            .setStatusPotency(2) // reduz speed em 2
            .setVfxKey("ice")
            .build();

        //adicionar no grimorio
        addSpell(fire);
        addSpell(eath);
        addSpell(iceImpact);
    }

    //Adiciona Magia ao grimorio
    public boolean addSpell(Skill spell) {
        if (spell == null || spells.containsKey(spell.getName().toLowerCase())) {
            return false;
        }
        spells.put(spell.getName().toLowerCase(), spell);
        spellsArray.add(spell);
        return true;
    }

    public Skill getSpell(String name) {
        return spells.get(name.toLowerCase());
    }

    public Array<Skill> getAvailableSpells() {
        return new Array<>(spellsArray);
    }
    //Verifica se uma magia existe no grimório
    public boolean hasSpell(String spellName) {
        return spells.containsKey(spellName.toLowerCase());
    }

    //Remove magia do grimorio
    public boolean removeSpell(String spellName) {
        Skill removed = spells.remove(spellName.toLowerCase());
        if (removed != null) {
            spellsArray.removeValue(removed, true);
            return true;
        }
        return false;
    }

    public int getSpellCount() {
        return spells.size;
    }
}
