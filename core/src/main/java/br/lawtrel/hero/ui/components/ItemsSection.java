package br.lawtrel.hero.ui.components;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Player;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import java.util.ArrayList;

public class ItemsSection extends Table {
    private Hero game;
    private Skin skin;

    public ItemsSection(Hero game) {
        this.game = game;
        this.skin = new Skin(game.assets.get("skins/uiskin.json"));
        setBackground(skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 0.8f));
        pad(10);
        top().left();

        Label title = new Label("Itens", skin);
        add(title).left().row();

        // Simulação de inventário
        Player player = game.getPlayer(); // Assumindo que existe esse getter
        java.util.List<String> items = player.getInventory(); // Deve retornar lista de nomes

        if (items == null) items = new ArrayList<>();
        if (items.isEmpty()) {
            add(new Label("Nenhum item.", skin)).left().row();
        } else {
            List<String> itemList = new List<>(skin);
            itemList.setItems(items.toArray(new String[0]));
            ScrollPane scrollPane = new ScrollPane(itemList, skin);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollingDisabled(true, false);
            add(scrollPane).height(100).width(200).left().row();
        }

    }
}
