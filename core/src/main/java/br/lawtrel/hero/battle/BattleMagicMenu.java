package br.lawtrel.hero.battle;

import br.lawtrel.hero.entities.Skill;
import com.badlogic.gdx.graphics.g2d.*;
import br.lawtrel.hero.magic.*;
import com.badlogic.gdx.utils.Array;

public class BattleMagicMenu {
    private Array<Skill> availableMagics;
    private int selectedMagicIndex = 0;
    private Skill selectedMagic;

    public void setMagics(Array<Skill> magics) {
        this.availableMagics = magics;
        if (magics != null && magics.size > 0) {
            this.selectedMagic = magics.get(0);
        } else {
            this.selectedMagic = null;
        }
        this.selectedMagicIndex = 0;
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        if  (availableMagics == null) return;

        for (int i = 0; i < availableMagics.size; i++) {
            Skill magic = availableMagics.get(i);
            String prefix = (i == selectedMagicIndex) ? "> " : "  ";
            String cost = " (" + magic.getMpCost() + " MP)";
            font.draw(batch, prefix + magic.getName() + cost, 50, 150 - (i * 25));
        }
    }

    public Skill getSelectedMagic() {
        return selectedMagic;
    }
    public void nextMagic() {
        if (availableMagics != null && availableMagics.size > 0) {
            selectedMagicIndex = (selectedMagicIndex + 1) % availableMagics.size;
            selectedMagic = availableMagics.get(selectedMagicIndex);
        }
    }

    public void previousMagic() {
        if (availableMagics != null && availableMagics.size > 0) {
            selectedMagicIndex = (selectedMagicIndex - 1 + availableMagics.size) % availableMagics.size;
            selectedMagic = availableMagics.get(selectedMagicIndex);
        }
    }

    public void setSelectedMagic(Skill spell) {
        if (availableMagics != null && availableMagics.contains(spell, true)) {
            this.selectedMagic = spell;
            this.selectedMagicIndex = availableMagics.indexOf(spell, true);
        }
    }

}
