package br.lawtrel.hero.ui.components;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import java.util.ArrayList;

public class ItemsSection extends Table {
    private final Player player;
    private final Skin skin;
    private final List<String> itemList;

    public ItemsSection(Hero game) {
        this.player = game.getPlayer();
        this.skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        this.itemList = new List<>(skin);

        setBackground(skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 0.8f));
        pad(10);
        top().left();
        updateItemsDisplay();
    }
    public void updateItemsDisplay() {
        clear();

        Label title = new Label("Inventário (" + player.getInventory().size() + ")", skin);
        add(title).left().row();

        if (player.getInventory().isEmpty()) {
            add(new Label("Nenhum item.", skin)).left().row();
        } else {
            // Converte itens para strings descritivas
            String[] items = player.getInventory().stream()
                .map(item -> item.getName() + " (" + item.getType() + ")")
                .toArray(String[]::new);

            itemList.setItems(items);
            ScrollPane scrollPane = new ScrollPane(itemList, skin);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollingDisabled(true, false);
            add(scrollPane).height(150).width(250).left().row();
        }
    }
}
