package br.lawtrel.hero.battle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import br.lawtrel.hero.entities.Item;
import java.util.List;

public class BattleItemMenu {
    private List<Item> availableItems;
    private int selectedItem = 0;

    public void setItems(List<Item> items) {
        this.availableItems = items;
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        if (availableItems == null) return;

        for (int i = 0; i < availableItems.size(); i++) {
            Item item = availableItems.get(i);
            String prefix = (i == selectedItem) ? "> " : "  ";
            font.draw(batch, prefix + item.getName(), 50, 150 - (i * 25));
        }
    }

    public Item getSelectedItem() {
        return availableItems.get(selectedItem);
    }

    public void nextItem() {
        selectedItem = (selectedItem + 1) % availableItems.size();
    }

    public void previousItem() {
        selectedItem = (selectedItem - 1 + availableItems.size()) % availableItems.size();
    }
}
